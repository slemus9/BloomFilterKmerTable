package dataStructures;

public class BitArray {

    /**
     * Number of bits per position in array
     */
    private static final int BITS_IN_SEGMENT = 32;

    /**
     * Array of integers. Each integer represents 32 bits
     */
    private int arr[];

    /**
     * Size of the array; number of bits
     */
    private int size;

    public BitArray (int size) {
        this.size = size;
        this.arr = new int[size/BITS_IN_SEGMENT + size % BITS_IN_SEGMENT];
    }

    /**
     * Gets the bit at the requested position. Returns true if the bit is 1 of
     * false if it's 0
     * @param idx - requested index
     * @return bit at position idx: true (1) or false (0)
     */
    public boolean get (int idx) {
        int mask = 1 << (idx % BITS_IN_SEGMENT);
        return (arr[idx / BITS_IN_SEGMENT] & mask) == mask;
    }

    /**
     * Sets the given bit (true: 1, false: 0) at the requested position
     * @param idx - requested index
     * @param bit - Bit to be positioned
     */
    public void set (int idx, boolean bit) {
        int segment = arr[idx / BITS_IN_SEGMENT];
        int mask = 1 << (idx % BITS_IN_SEGMENT);
        arr[idx / BITS_IN_SEGMENT] =
                bit ? segment | mask : segment & (~0 - mask);
    }

    /**
     *
     * @return the size in bits of the array
     */
    public int getSize () {
        return size;
    }

    /**
     * BitArray testing
     * @param args
     */
    public static void main(String[] args) {
        BitArray bitArray = new BitArray(1000);
        bitArray.set(40, true);
        System.out.println(bitArray.get(15));
        System.out.println(bitArray.get(40));
        bitArray.set(15, true);
        System.out.println(bitArray.get(15));
        bitArray.set(40, false);
        System.out.println(bitArray.get(40));
    }
}
