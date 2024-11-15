// PageFaultRateBuffer.java
package buffer;

public abstract class PageFaultRateBuffer extends Buffer {
    protected int fsCount = 0; // Unbuffered page accesses
    protected int sCount = 0;  // Total page accesses

    public PageFaultRateBuffer(int capacity) {
        super(capacity);
    }

    public double getFSR() {
        return (sCount == 0) ? 0 : (double) fsCount / sCount;
    }

    @Override
    protected Slot fix(char c) throws IllegalStateException {
        sCount++;
        Slot slot = lookUp(c);
        if (slot == null) {
            fsCount++;
        }
        return super.fix(c);
    }
}
