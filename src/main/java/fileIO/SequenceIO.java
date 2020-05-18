package fileIO;

import customRxFuncions.BufferUntil;
import dataStructures.Sequence;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOperator;
import io.reactivex.rxjava3.core.Single;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SequenceIO {

    /**
     * Custom function that can be applied to an Observable via lift. It divides the emissions
     * of the observable into segments (Lists); each one progressively constructed with each element
     * from the observable until one fulfills the predicate
     * @param predicate - A function A -> Bool, used to partition the observable
     * @param <A> - The type of the observable
     * @return Observable of A
     */
    private static <A> ObservableOperator<List<A>, A> bufferUntil (Function<A, Boolean> predicate) {
        return new BufferUntil<>(predicate);
    }

    /**
     * Creates a custom name for the output file for storing the k-mer table based
     * on the name of the input file.
     * @param inputFile
     * @return A name for an output file
     */
    private String buildOutputFileNameKmerTable (String inputFile) {
        File file = new File(inputFile);
        String fileName = file.getName();
        return fileName + "_frequencies.out";
    }

    /**
     * Creates a sequence from FASTQ information. It assumes that the
     * received list with the information has 4 lines, following the FASTQ format:
     * line 1: @{sequence_id}
     * line 2: {sequence_characters}
     * line 3: +[{sequence_id}]
     * line 4: {sequence_score}
     * @param info - FASTQ information of one sequence
     * @return A Sequence class
     */
    private Sequence sequenceFromFASTQBlock (List<String> info) {
        String id = info.get(0);
        String characters = info.get(1);
        String score = info.get(3);
        return new Sequence(
                id.substring(1),
                characters,
                score
        );
    }

    /**
     * Create a reader stream in the form of an Observable
     * @param filePath - the path of the file to read
     * @return Observable of the buffered stream
     */
    private Observable<String> createObservableReader (String filePath) {
        return Observable.using(
                () -> new BufferedReader(new FileReader(filePath)),
                reader -> Observable.fromIterable(reader.lines()::iterator),
                BufferedReader::close
        );
    }

    /**
     * Creates an Observable of dataStructures.Sequence from a source file in FASTQ format for further processing
     * @param filePath - the path of the file to read
     * @return Observable of the sequences read from the file
     */
    private Observable<Sequence> readFASTQ (String filePath) {
        return createObservableReader(filePath)
                .buffer(4).map(this::sequenceFromFASTQBlock);
    }

    /**
     * Creates an Observable of dataStructures.Sequence from a source file in FASTA format for further processing
     * @param filePath - the path of the file to read
     * @return Observable of the sequences read from the file
     */
    private Observable<Sequence> readFASTA (String filePath) {
        Observable<List<String>> lineBlocks = createObservableReader(filePath)
                .lift(bufferUntil(line -> line.startsWith(">")));
        return lineBlocks.zipWith(
                    lineBlocks.skip(1),
                    (xs, ys) -> Arrays.asList(xs, ys)
                )
                .map(
                    blockPair -> {
                        List<String> block1 = blockPair.get(0);
                        List<String> block2 = blockPair.get(1);
                        String id = block1.get(block1.size() - 1);
                        String characters = String.join("", block2.subList(0, block2.size() - 1));
                        return new Sequence(id.substring(1), characters);
                    }
                );
    }

    /**
     * Gets the extension of a given file path
     * @param filePath - path of the file
     * @return the extension of the file
     */
    private String getFileExtension (String filePath) {
        File file = new File(filePath);
        String fileName = file.getName();
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /**
     * Reads a file with FASTA or FASTQ format, returning an Observable of the
     * sequences contained in the file
     * @param filePath - the path of the file to read
     * @return Observable of dataStructures.Sequence
     */
    public Observable<Sequence> readFile (String filePath) {
        String extension = getFileExtension(filePath);

        if (extension.equals("fa") || extension.equals("fasta")){
            return readFASTA(filePath);
        } else if (extension.equals("fastq")) {
            return readFASTQ(filePath);
        } else {
            return Observable.error(
                    new Throwable("The format of the file must be either FASTA or FASTQ and must be explicit in the file extension")
            );
        }
    }

    /**
     * Reads all the files (in FASTA of FASTQ format) from the given list,
     * and retunrs and Observable with all the sequences contained in all files
     * @param filePaths - List of files to read
     * @return Observable with the sequences
     */
    public Observable<Sequence> readFiles (List<String> filePaths) {
        String extension = getFileExtension(filePaths.get(0));
        Boolean allWithSameFormat = filePaths.stream().allMatch(path -> getFileExtension(path).equals(extension));

        if (allWithSameFormat) {
            return Observable.fromIterable(filePaths).flatMap(this::readFile);
        } else {
            return Observable.error(
                    new Throwable("The given files must be all of the same format (FASTQ or FASTA)")
            );
        }
    }

    /**
     * Reads all the files (in FASTA or FASTQ format) contained in the given
     * directory, and retunrs and Observable with all the sequences contained in all files
     * @param directoryPath - Path of the directory to read
     * @return Observable with the sequences
     */
    public Observable<Sequence> readFiles (String directoryPath) {
        File dir = new File(directoryPath);
        if(dir.list() == null){
            return Observable.error(
                    new Throwable("The given path to the directory is invalid")
            );
        } else {
            try {
                Path path = Paths.get(directoryPath);
                return readFiles(
                        Files.list(path).map(Path::toString).collect(Collectors.toList())
                );
            } catch (IOException e) {
                return Observable.error(e);
            }
        }
    }

    /**
     * Extracts the kmers of length k from a given sequence
     * @param sequence - sequence to process
     * @param k - length of the kmer
     * @return an Observable of all the kmers of the sequence
     */
    private Observable<String> getKmersFromSequence (Sequence sequence, int k) {
        String characters = sequence.getCharacters();
        int n = characters.length();
        ArrayList<String> kmers = new ArrayList<>();

        for (int i = 0; i <=n - k; i++) {
            String kmer = characters.substring(i, i + k);
            kmers.add(kmer);
        }
        return Observable.fromIterable(kmers);
    }

    /**
     * From an Observable source of sequences, reads all the kmers of length k
     * @param sequences - Observable source of sequences
     * @param k - length of the kmers
     * @return Observable with all the kmers from the sequences
     */
    public Observable<String> getAllKmers (Observable<Sequence> sequences, int k) {
        return sequences.flatMap(sequence -> getKmersFromSequence(sequence, k));
    }

    /**
     * Get the number of kmers from an Observable source of sequences
     * @param sequences - Observable of sequences
     * @param k - length of each kmer
     * @return A Single with the number of kmers
     */
    public Single<Integer> estimateNumberOfKmers (Observable<Sequence> sequences, int k) {
        return sequences.reduce(0, (acc, seq) -> acc + (seq.getCharacters().length() - k) * k);
    }
}

