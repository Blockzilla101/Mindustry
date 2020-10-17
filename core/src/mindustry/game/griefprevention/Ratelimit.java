package mindustry.game.griefprevention;

import arc.Core;
import arc.func.Cons;
import arc.util.Interval;

/** Simple ratelimit */
public class Ratelimit {
    public int eventLimit;
    public int findTime;
    public Interval begin = new Interval();
    public int count = 0;

    private boolean noUpdate = false;

    /**
     * The constructor
     * @param eventLimit Event limit
     * @param findTime Time interval in milliseconds
     */
    public Ratelimit(int eventLimit, int findTime) {
        this.eventLimit = eventLimit;
        this.findTime = findTime;
    }

    /**
     * Helper to update begin time
     */
    private void updateBegin() {
        if (!begin.get(((float)findTime / 1000) * 60) && !noUpdate) {
            count = 0;
        }
    }

    /**
     * Check and update ratelimit
     * @return True if ratelimit exceeded, false otherwise
     */
    public boolean get() {
        updateBegin();
        count++;
        return count > eventLimit;
    }

    /**
     * Check ratelimit
     * @return True if ratelimit exceeded, false otherwise
     */
    public boolean check() {
        updateBegin();
        return count > eventLimit;
    }

    /** Get number of events in current interval */
    public int events() {
        updateBegin();
        return count;
    }

    /**
     * Provide count next tick. Will inhibit reset
     * @param fn Function to be run
     */
    public void nextTick(Cons<Ratelimit> fn) {
        noUpdate = true;
        Core.app.post(() -> {
            fn.get(this);
            noUpdate = false;
        });
    }
}
