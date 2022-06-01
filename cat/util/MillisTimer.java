package cat.util;

public final class MillisTimer {
    public long millis = -1L;

    public boolean hasTimeReached(final long ms) {
        return System.currentTimeMillis() >= millis + ms;
    }

    public long getTimeDiff(final long ms) {
        return (ms + millis) - System.currentTimeMillis();
    }

    public boolean delay(final float milliSec) {
        return (System.nanoTime() / 1000000f) - this.millis >= milliSec;
    }

    public MillisTimer reset() {
        millis = System.currentTimeMillis();
        return this;
    }

    public boolean hasTicksPassed(final float ticks){
        return System.currentTimeMillis() - millis >= (ticks * 50);
    }
}
