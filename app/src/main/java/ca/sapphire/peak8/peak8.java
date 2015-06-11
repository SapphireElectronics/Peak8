package ca.sapphire.peak8;

/**
 * Created by Admin on 10/06/15.
 */


public class peak8 {
    public long segmentStart;       // start time of current segment in milliseconds
    public long segmentLength;      // length of current segment in milliseconds
    public long segmentAccum;       // accumulated length of current segment in milliseconds

    private long accumulated = 0;   // total run time since start in milliseconds

    private int reps;               // number of repetitions to perform
    private long peak;              // length of a peak segment in milliseconds
    private long rest;              // length of a rest segment in milliseconds
    private int repCount = 0;       // number of repetitions performed
    private long totalTime;         // total time for full run in milliseconds

    public enum Mode{ START, WARMUP, RUN, REST, COOLDOWN, END };
    public Mode mode = Mode.START;

    public boolean isRunning = false;
    public boolean modeChanged = true;

    public peak8( int reps, long peak, long rest )
    {
        this.reps = reps;
        this.peak = peak*1000;
        this.rest = rest*1000;
        calcTotal();
        reset();
    }

    public void setValues( int reps, long peak, long rest ) {
        this.reps = reps;
        this.peak = peak * 1000;
        this.rest = rest * 1000;
        calcTotal();
    }

    private void calcTotal() {
        totalTime = (reps * peak) + ((reps + 1) * rest);
    }

    public void reset() {
        isRunning = false;
        segmentAccum = 0;
        accumulated = 0;
        repCount = 0;
        mode = Mode.START;
        segmentStart = System.currentTimeMillis();
    }

    public void start() {
        if( !isRunning ) {
            isRunning = true;
            segmentStart = System.currentTimeMillis();
        }
    }

    public void stop() {
        if (isRunning) {
            isRunning = false;
            segmentAccum += (System.currentTimeMillis() - segmentStart);
        }
    }

    private void nextMode() {
        modeChanged = true;
        accumulated += segmentAccum;
        segmentAccum = 0;
        segmentStart = System.currentTimeMillis();

        switch (mode) {
            case START:
                segmentLength = rest;
                mode = Mode.WARMUP;
                break;
            case WARMUP:
                segmentLength = peak;
                mode = Mode.RUN;
                break;
            case RUN:
                segmentLength = rest;
                repCount++;
                if( repCount >= reps )
                    mode = Mode.COOLDOWN;
                else
                    mode = Mode.REST;
                break;
            case REST:
                segmentLength = peak;
                mode = Mode.RUN;
                break;
            case COOLDOWN:
                segmentLength = 0;
                mode = Mode.END;
                break;
            case END:
                stop();
                break;
        }
    }

    public void update() {
        if( !isRunning )
            return;

        if( mode == Mode.END)
            return;

        long segmentTimeNow = segmentAccum + ( System.currentTimeMillis()-segmentStart );

        if( segmentTimeNow >= segmentLength ) {
            segmentAccum = segmentTimeNow;
            nextMode();
        }
    }

    private long getElapsed() {
        return accumulated + segmentAccum + ( System.currentTimeMillis()-segmentStart );
    }

    public int getSegmentTime() {
        return (int) (segmentAccum + (System.currentTimeMillis()-segmentStart )) / 1000;
    }

    public int getTotalTime() {
        return (int) totalTime/1000;
    }

    public int getTotalMins() {
        return (int) getTotalTime()/60;
    }

    public int getTotalSecs() {
        return getTotalTime() - (getTotalMins() * 60);
    }

    public int getElapsedTime() {
        return (int) getElapsed()/1000;
    }

    public int getElapsedMins() {
        return (int) getElapsedTime()/60;
    }

    public int getElapsedSecs() {
        return getElapsedTime() - (getElapsedMins()*60);
    }
}
