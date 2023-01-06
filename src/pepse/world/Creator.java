package pepse.world;

/**
 * An abstract class that writes the create in range method which computes the real range that need to be
 * modified and saves the range in the class arguments: minXToUpdate, maxXToUpdate
 * The class keeps track on which range something was created, it saves this range in the class arguments:
 * minXCreated and maxXCreated
 */
public abstract class Creator {
    private int blockSize;
    private int minXCreated;
    private int maxXCreated;
    private int minXToUpdate;
    private int maxXToUpdate;


    /**
     * constructor
     */
    public Creator(){
        minXCreated = 0;
        maxXCreated = 0;
        blockSize = 0; // default value
    }

    /**
     * A setter for the block size
     * @param size repersents the new block size
     */
    public void setBlockSize(int size) {blockSize = size;}

    /**
     * Getter for the minXToUpdate
     * @return minXToUpdate
     */
    public int getMinXToUpdate () {return minXToUpdate;}

    /**
     * Getter for the maxXToUpdate
     * @return maxXToUpdate
     */
    public int getMaxXToUpdate () {return maxXToUpdate;}

    /**
     * Getter for the minXCreated
     * @return minXCreated
     */
    public int getMinXCreated() {return minXCreated;}

    /**
     * Getter for the maxXCreated
     * @return maxXCreated
     */
    public int getMaxXCreated() {return maxXCreated;}

    /**
     * A method that computes the real range where we need to create our objects and saves it in
     * minXToUpdate and in maxXToUpdate. the method uses the block size which is 0 if wasn't change!!!!
     * @param minX represents the lower bound
     * @param maxX represents the top bound
     */
    public void createInRange(int minX, int maxX) {
        // normalizing minX and maxX to a size that fits the block size
        int normalizedMaxX = (maxX / blockSize) * blockSize + blockSize;
        int normalizedMinX = (minX / blockSize) * blockSize - blockSize;
        this.minXToUpdate = 0;
        this.maxXToUpdate = 0;

        // creating for the first time:
        if (this.minXCreated == 0 & this.maxXCreated == 0) {
            // saving values to update
            this.minXToUpdate = normalizedMinX;
            this.maxXToUpdate = normalizedMaxX;

            // saving the new state of the classes range of creation
            this.minXCreated = normalizedMinX;
            this.maxXCreated = normalizedMaxX;
        }

        // extending to the right:
        else if (normalizedMaxX > this.maxXCreated) {
            // saving values to update
            this.minXToUpdate = this.maxXCreated;
            this.maxXToUpdate = normalizedMaxX;

            // saving the new state of the classes range of creation
            int deltaMax = Math.abs(Math.abs(normalizedMaxX) - Math.abs(this.maxXCreated));
            this.minXCreated = this.minXCreated + deltaMax;
            this.maxXCreated = normalizedMaxX;
        }

        // extending to the left:
        else if (normalizedMinX < this.minXCreated) {
            // saving values to update
            this.minXToUpdate = normalizedMinX;
            this.maxXToUpdate = this.minXCreated;

            // saving the new state of the classes range of creation
            int deltaMin = Math.abs(Math.abs(normalizedMinX) - Math.abs(this.minXCreated));
            this.maxXCreated = this.maxXCreated - deltaMin;
            this.minXCreated = normalizedMinX;
        }
    }



}
