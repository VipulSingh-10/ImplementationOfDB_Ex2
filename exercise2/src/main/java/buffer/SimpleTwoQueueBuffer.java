package buffer;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class SimpleTwoQueueBuffer extends PageFaultRateBuffer {
    private final int kin;
    private final Queue<Slot> a1; // FIFO queue for short-term pages
    private final Queue<Slot> am; // LRU queue for long-term pages
    private final Map<Character, Integer> accessCount; // Track access counts for pages

    public SimpleTwoQueueBuffer(int capacity) {
        super(capacity);
        this.kin = Math.max(1, capacity / 4); // Ensure kin is at least 1
        this.a1 = new ArrayDeque<>();
        this.am = new ArrayDeque<>();
        this.accessCount = new HashMap<>();
    }

    @Override
    protected Slot fix(char c) throws IllegalStateException {
        sCount++; // Increment total page access counter
        Slot slot = lookUp(c);

        if (slot != null) {
            if (am.contains(slot)) {
                // Page is in am, refresh its position to maintain LRU behavior
                am.remove(slot);
                am.add(slot);
                slot.fix();
            } else if (a1.contains(slot)) {
                // Page is in a1, increment its access count
                int count = accessCount.getOrDefault(c, 0) + 1;
                accessCount.put(c, count);

                // Promote to am if accessed twice
                if (count >= 1) {
                    a1.remove(slot);
                    am.add(slot);
                    accessCount.remove(c); // Remove from the access count map
                }
                slot.fix();
            }
        } else {
            fsCount++; // Page fault occurred

            // Evict the oldest page from a1 if it exceeds kin
            if (a1.size() >= kin) {
                Slot victim = a1.poll();
                if (victim != null) {
                    accessCount.remove(victim.c); // Remove from access count map
                    victim.remove();
                }
            }

            // Fix the new page and add it to a1
            slot = super.fix(c);
            a1.add(slot);
            accessCount.put(c, 1); // Initialize access count for the new page
        }

        return slot;
    }

    @Override
    protected Slot victim() {
        // Prefer to evict from a1 first, then from am if a1 is empty
        return !a1.isEmpty() ? a1.peek() : am.peek();
    }
}
