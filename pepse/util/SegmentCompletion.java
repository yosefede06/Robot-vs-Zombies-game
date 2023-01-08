package pepse.util;

/**
 * A class that computes the algorithm in which given two integers (representing a segment) and a block size,
 * calculates the minimum number of blocks needed to complete the segment, satisfying the condition:
 * startAxis % blockSize == 0.
 */
public class SegmentCompletion {
    private final int minAxis;
    private final int maxAxis;
    private final int blockSize;

    /**
     * A constructor of the class to save the needed values.
     * @param minAxis starting point.
     * @param maxAxis ending point.
     * @param blockSize Block size.
     */
    public SegmentCompletion(int minAxis, int maxAxis, int blockSize) {
        this.minAxis = minAxis;
        this.maxAxis = maxAxis;
        this.blockSize = blockSize;
    }

    /**
     * This function calculates the number of columns that we need from the startX.
     * @param startX The start of the first block.
     * @return Number of total columns.
     */
    public int calculateNumberOfCols(int startX) {
        int endX;
        if(maxAxis % blockSize == 0) endX = maxAxis;
        else endX = maxAxis + (blockSize - maxAxis % blockSize);
        int distance = Math.abs(endX - startX);
        return distance / blockSize;
    }

    /**
     * This function calculates the startX of the first block
     * @return StartX.
     */
    public int calculateStartX () {
        if(minAxis % blockSize == 0) return minAxis;
        return minAxis - (minAxis % blockSize);
    }
}
