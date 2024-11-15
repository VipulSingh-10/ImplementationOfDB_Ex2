package buffer;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class TwoQueueBuffer extends PageFaultRateBuffer {
    private final Queue<Slot> a1in; // FIFO queue for correlated accesses
    private final Set<Character> a1out; // Track evicted pages for correlation
    private final Queue<Slot> am; // LRU queue for frequently accessed pages
    private final int kout; // Threshold for a1out size
    private final int kin; // Threshold for a1in size

    public TwoQueueBuffer(int capacity) {
        super(capacity);
        this.kin = Math.max(1, capacity / 4);
        this.kout = Math.max(1, capacity / 2);
        this.a1in = new ArrayDeque<>();
        this.a1out = new HashSet<>();
        this.am = new ArrayDeque<>();
    }

    @Override
    protected Slot fix(char c) throws IllegalStateException {
        sCount++;
        Slot slot = lookUp(c);

        if (slot != null) {
            if (am.contains(slot)) {
                // If the page is in am, it is frequently accessed
                am.remove(slot);
                am.add(slot); // Update position in am (LRU behavior)
                slot.fix();
                return slot;
            } else if (a1in.remove(slot)) {
                // Promote page from a1in to am if accessed again
                am.add(slot);
                slot.fix();
                return slot;
            }
        } else if (a1out.contains(c)) {
            // If the page is in a1out, promote directly to am
            a1out.remove(c);
            slot = super.fix(c);
            am.add(slot);
            return slot;
        } else {
            fsCount++;
            // If a1in is full, evict the oldest page
            if (a1in.size() >= kin) {
                Slot victim = a1in.poll();
                if (victim != null) {
                    if (a1out.size() >= kout) {
                        a1out.remove(a1out.iterator().next()); // Remove the oldest entry from a1out
                    }
                    a1out.add(victim.c); // Track evicted page
                    victim.remove();
                }
            }
            slot = super.fix(c);
            a1in.add(slot); // Add to a1in
        }

        return slot;
    }

    @Override
    protected Slot victim() {
        // Prefer to evict from a1in first, then from am
        return !a1in.isEmpty() ? a1in.peek() : am.peek();
    }


}
