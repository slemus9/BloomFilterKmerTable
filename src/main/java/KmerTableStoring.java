import fileIO.SequenceIO;

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
        sequenceIO.readFiles(directoryPath).subscribe(
                sequence -> System.out.println("Emission: " + sequence.getId()),
                Throwable::printStackTrace
        );
    }

    public static void main(String[] args) {
        directoryReadingTest(testYeastDirectory);
    }
}
