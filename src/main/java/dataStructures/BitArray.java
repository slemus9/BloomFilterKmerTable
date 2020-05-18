package dataStructures;

public class BitArray {

    private static final int BITS_IN_SEGMENT = 32;

    private int arr[];
    private int size;

    public BitArray (int size) {
        this.size = size;
        this.arr = new int[size/BITS_IN_SEGMENT + size % BITS_IN_SEGMENT];
    }

    public boolean get (int idx) {
        int mask = 1 << (idx % BITS_IN_SEGMENT);
        return (arr[idx / BITS_IN_SEGMENT] & mask) == mask;
    }

    public void set (int idx, boolean bit) {
        int segment = arr[idx / BITS_IN_SEGMENT];
        int mask = 1 << (idx % BITS_IN_SEGMENT);
        arr[idx / BITS_IN_SEGMENT] =
                bit ? segment | mask : segment & (~0 - mask);
    }

    public int getSize () {
        return size;
    }

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
