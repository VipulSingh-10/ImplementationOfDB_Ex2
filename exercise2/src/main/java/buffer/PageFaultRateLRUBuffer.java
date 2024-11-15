package buffer;

public class PageFaultRateLRUBuffer extends LRUBuffer {
    private int fsCount = 0; // Count of unbuffered page accesses (page faults)
    private int sCount = 0;  // Total number of page accesses

    public PageFaultRateLRUBuffer(int capacity) {
        super(capacity);
    }

    // Method to calculate the page fault rate
    public double getFSR() {
        return (sCount == 0) ? 0 : (double) fsCount / sCount;
    }

    @Override
    protected Buffer.Slot fix(char c) throws IllegalStateException {
        sCount++; // Increment the total page access counter

        Slot slot = (Slot) lookUp(c); // Check if the page is in the buffer

        if (slot == null) { // If the page is not in the buffer
            fsCount++; // Increment the page fault counter
        }

        // Use the parent's fix method to manage the LRU behavior
        return super.fix(c);
    }
}
