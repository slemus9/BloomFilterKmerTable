import dataStructures.KmerTable;
import dataStructures.Sequence;
import fileIO.SequenceIO;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;


public class KmerTableStoring {

    /**
     * Test file paths
     */
    private static final String ngsepDemoPath = "../demoNGSEP";
    private static final String testEcoliFASTA = ngsepDemoPath + "/ecoli/reads/correctedTrimmedCanu.fa";
    private static final String testEcoliFASTQ = ngsepDemoPath + "/ecoli/reads/ecoli_p6_25x.filtered.fastq";
    private static final String testYeastDirectory = ngsepDemoPath + "/yeast-reads";
    private static final String defaultOutputPath = "/tmp";

    /**
     * Instance to read and write sequences
     */
    private static final SequenceIO sequenceIO = new SequenceIO();


    private static void directoryReadingTest (String directoryPath) {
        System.out.println("Reading files from directory: " + directoryPath);
        Disposable d = sequenceIO.readFiles(directoryPath).subscribe(
                sequence -> System.out.println("Emission: " + sequence.getId()),
                Throwable::printStackTrace
        );
        d.dispose();
    }

    private static void fileReadingTest (String filePath) {
        System.out.println("Reading file: " + filePath);
        Disposable d = sequenceIO.readFile(filePath).subscribe(
                sequence -> System.out.println("Emission: " + sequence.getCharacters()),
                Throwable::printStackTrace
        );
        d.dispose();
    }

    private static void kmerExtractionFileTest (String filePath, Integer k) {
        System.out.println("Reading kmers of length " + k + " from file: " + filePath);
        Observable<Sequence> sequences = sequenceIO.readFile(filePath);
        Single<Integer> estimatedNumKmers = sequenceIO.estimateNumberOfKmers(sequences, k);
        Observable<String> kmers = sequenceIO.getAllKmers(sequences, k);
        Disposable d = estimatedNumKmers.subscribe(
                numKmers -> {
                    KmerTable kmerTable = new KmerTable(k, numKmers);
                    kmers.subscribe(kmerTable::add);
                }
        );
        d.dispose();
    }

    private static void kmerExtractionDirectoryTest (String directoryPath, Integer k) {
        System.out.println("Reading kmers of length " + k + " from directory: " + directoryPath);
        Observable<Sequence> sequences = sequenceIO.readFiles(directoryPath);
        Single<Integer> estimatedNumKmers = sequenceIO.estimateNumberOfKmers(sequences, k);
        Observable<String> kmers = sequenceIO.getAllKmers(sequences, k);
        Disposable d = estimatedNumKmers.subscribe(
                numKmers -> {
                    KmerTable kmerTable = new KmerTable(k, numKmers);
                    kmers.subscribe(kmerTable::add);
                }
        );
        d.dispose();
    }


    public static void main(String[] args) {
        fileReadingTest(testYeastDirectory + "/PoolEthProd-1M_1.fastq");
    }
}
