/**
 * Copyright 2013 Contributors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 *    Contributors:
 *          Alessandro Ferreira Leite - the initial implementation.
 */
package jenergy.util.time;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * <code>Timer</code> provides a convenient API for timings.
 * </p>
 * 
 * <p>
 * To start the watch, call {@link #start()}. At this point you can:
 * </p>
 * <ul>
 * <li>{@link #split()} the watch to get the time whilst the watch continues in the background. {@link #unsplit()} will remove the effect of the
 * split. At this point, these three options are available again.</li>
 * <li>{@link #suspend()} the watch to pause it. {@link #resume()} allows the watch to continue. Any time between the suspend and resume will not be
 * counted in the total. At this point, these three options are available again.</li>
 * <li>{@link #stop()} the watch to complete the timing session.</li>
 * </ul>
 * 
 * <p>
 * It is intended that the output methods {@link #toString()} and {@link #getTime()} should only be called after stop, split or suspend, however a
 * suitable result will be returned at other points.
 * </p>
 * 
 * <p>
 * NOTE: As from v2.1, the methods protect against inappropriate calls. Thus you cannot now call stop before start, resume before suspend or unsplit
 * before split.
 * </p>
 * 
 * <p>
 * NOTE (not Apache Software Foundation): This class was modified to work with nanoseconds or milliseconds configured by the enumeration
 * {@link TimeUnitType}.
 * </p>
 * 
 * <p>
 * 1. split(), suspend(), or stop() cannot be invoked twice<br />
 * 2. unsplit() may only be called if the watch has been split()<br />
 * 3. resume() may only be called if the watch has been suspend()<br />
 * 4. start() cannot be called twice without calling reset()
 * </p>
 * 
 * <p>
 * This class is not thread-safe
 * </p>
 * 
 * @author Apache Software Foundation
 * @since 2.0
 * @version $Id: timer.java 1056988 2011-01-09 17:58:53Z niallp $
 */
public class Timer
{

    // running states
    /**
     * The started state value.
     */
    private static final int STATE_UNSTARTED = 0;

    /**
     * The running state value.
     */
    private static final int STATE_RUNNING = 1;

    /**
     * The stopped state value.
     */
    private static final int STATE_STOPPED = 2;

    /**
     * The suspended state value.
     */
    private static final int STATE_SUSPENDED = 3;

    // split state
    /**
     * The not split state value.
     */
    private static final int STATE_UNSPLIT = 10;

    /**
     * The split state value.
     */
    private static final int STATE_SPLIT = 11;

    public static enum TimeUnitType
    {
        /**
         * The time in nanoseconds. This type can only be used to measure elapsed time and is not related to any other notion of system or wall-clock
         * time.
         */
        NANO
        {
            @Override
            public long time()
            {
                return System.nanoTime();
            }

            @Override
            public TimeUnit unit()
            {
                return TimeUnit.NANOSECONDS;
            }
        },

        /**
         * The current time in milliseconds.
         */
        MILLIS
        {
            @Override
            public long time()
            {
                return System.currentTimeMillis();
            }

            @Override
            public TimeUnit unit()
            {
                return TimeUnit.MILLISECONDS;
            }
        };

        /**
         * @return The value of time. It can be in {@link #NANO} or {@link #MILLIS}.
         */
        public abstract long time();

        /**
         * Returns the {@link TimeUnit} of the measure.
         * 
         * @return The {@link TimeUnit} of the measure.
         */
        public abstract TimeUnit unit();

    }

    /**
     * The current running state of the timer.
     */
    private int runningState = STATE_UNSTARTED;

    /**
     * Whether the timer has a split time recorded.
     */
    private int splitState = STATE_UNSPLIT;

    /**
     * The start time.
     */
    private long startTime = -1;

    /**
     * The stop time.
     */
    private long stopTime = -1;

    /**
     * The type of time measure to be used.
     */
    private final TimeUnitType timeUnitType;

    /**
     * 
     * <p>
     * Constructor.
     * </p>
     * 
     * @param unitType
     *            The unit type of time to be used.
     */
    public Timer(TimeUnitType unitType)
    {
        super();
        this.timeUnitType = unitType;
    }

    /**
     * <p>
     * Default constructor. The default {@link TimeUnitType} is {@link TimeUnitType#NANO}.
     * </p>
     */
    public Timer()
    {
        this(TimeUnitType.NANO);
    }

    /**
     * <p>
     * Start the timer.
     * </p>
     * 
     * <p>
     * This method starts a new timing session, clearing any previous values.
     * </p>
     * 
     * @throws IllegalStateException
     *             if the timer is already running.
     */
    public void start()
    {
        if (this.runningState == STATE_STOPPED)
        {
            throw new IllegalStateException("timer must be reset before being restarted. ");
        }
        if (this.runningState != STATE_UNSTARTED)
        {
            throw new IllegalStateException("timer already started. ");
        }
        this.stopTime = -1;
        this.startTime = timeUnitType.time();
        this.runningState = STATE_RUNNING;
    }
    
    /**
     * Returns an instance of the time with the timer started.
     *  
     * @return An instance of the time with the timer started.
     */
    public static Timer createAndStart()
    {
        Timer timer = new Timer();
        timer.start();
        return timer;
    }

    /**
     * Restart the timer.
     */
    public void restart()
    {
        this.runningState = STATE_UNSTARTED;
        this.start();
    }

    /**
     * <p>
     * Stop the timer.
     * </p>
     * 
     * <p>
     * This method ends a new timing session, allowing the time to be retrieved.
     * </p>
     * 
     * @throws IllegalStateException
     *             if the timer is not running.
     */
    public void stop()
    {
        if (this.runningState != STATE_RUNNING && this.runningState != STATE_SUSPENDED)
        {
            throw new IllegalStateException("timer is not running. ");
        }
        if (this.runningState == STATE_RUNNING)
        {
            this.stopTime = timeUnitType.time();
        }
        this.runningState = STATE_STOPPED;
    }

    /**
     * <p>
     * Resets the timer. Stops it if need be.
     * </p>
     * 
     * <p>
     * This method clears the internal values to allow the object to be reused.
     * </p>
     */
    public void reset()
    {
        this.runningState = STATE_UNSTARTED;
        this.splitState = STATE_UNSPLIT;
        this.startTime = -1;
        this.stopTime = -1;
    }

    /**
     * <p>
     * Split the time.
     * </p>
     * 
     * <p>
     * This method sets the stop time of the watch to allow a time to be extracted. The start time is unaffected, enabling {@link #unsplit()} to
     * continue the timing from the original start point.
     * </p>
     * 
     * @throws IllegalStateException
     *             if the timer is not running.
     */
    public void split()
    {
        if (this.runningState != STATE_RUNNING)
        {
            throw new IllegalStateException("timer is not running. ");
        }
        this.stopTime = timeUnitType.time();
        this.splitState = STATE_SPLIT;
    }

    /**
     * <p>
     * Remove a split.
     * </p>
     * 
     * <p>
     * This method clears the stop time. The start time is unaffected, enabling timing from the original start point to continue.
     * </p>
     * 
     * @throws IllegalStateException
     *             if the timer has not been split.
     */
    public void unsplit()
    {
        if (this.splitState != STATE_SPLIT)
        {
            throw new IllegalStateException("timer has not been split. ");
        }
        this.stopTime = -1;
        this.splitState = STATE_UNSPLIT;
    }

    /**
     * <p>
     * Suspend the timer for later resumption.
     * </p>
     * 
     * <p>
     * This method suspends the watch until it is resumed. The watch will not include time between the suspend and resume calls in the total time.
     * </p>
     * 
     * @throws IllegalStateException
     *             if the timer is not currently running.
     */
    public void suspend()
    {
        if (this.runningState != STATE_RUNNING)
        {
            throw new IllegalStateException("timer must be running to suspend. ");
        }
        this.stopTime = timeUnitType.time();
        this.runningState = STATE_SUSPENDED;
    }

    /**
     * <p>
     * Resume the timer after a suspend.
     * </p>
     * 
     * <p>
     * This method resumes the watch after it was suspended. The watch will not include time between the suspend and resume calls in the total time.
     * </p>
     * 
     * @throws IllegalStateException
     *             if the timer has not been suspended.
     */
    public void resume()
    {
        if (this.runningState != STATE_SUSPENDED)
        {
            throw new IllegalStateException("timer must be suspended to resume. ");
        }
        this.startTime += timeUnitType.time() - this.stopTime;
        this.stopTime = -1;
        this.runningState = STATE_RUNNING;
    }

    /**
     * <p>
     * Get the time on the timer.
     * </p>
     * 
     * <p>
     * This is either the time between the start and the moment this method is called, or the amount of time between start and stop.
     * </p>
     * 
     * @return the time in milliseconds or nanoseconds.
     */
    public long getTime()
    {
        if (this.runningState == STATE_STOPPED || this.runningState == STATE_SUSPENDED)
        {
            return this.stopTime - this.startTime;
        }
        else if (this.runningState == STATE_UNSTARTED)
        {
            return 0;
        }
        else if (this.runningState == STATE_RUNNING)
        {
            return timeUnitType.time() - this.startTime;
        }
        throw new RuntimeException("Illegal running state has occured. ");
    }

    /**
     * Returns the interval between the begin and the end in milliseconds or nanoseconds.
     * 
     * @return The interval between the begin and the end in milliseconds or nanoseconds.
     */
    public long time()
    {
        return this.getTime();
    }

    /**
     * <p>
     * Get the split time on the timer.
     * </p>
     * 
     * <p>
     * This is the time between start and latest split.
     * </p>
     * 
     * @return the split time in milliseconds
     * 
     * @throws IllegalStateException
     *             if the timer has not yet been split.
     * @since 2.1
     */
    public long getSplitTime()
    {
        if (this.splitState != STATE_SPLIT)
        {
            throw new IllegalStateException("timer must be split to get the split time. ");
        }
        return this.stopTime - this.startTime;
    }

    /**
     * Returns the time this timer was started.
     * 
     * @return the time this timer was started
     * @throws IllegalStateException
     *             if this timer has not been started
     * @since 2.4
     */
    public long getStartTime()
    {
        if (this.runningState == STATE_UNSTARTED)
        {
            throw new IllegalStateException("timer has not been started");
        }
        return this.startTime;
    }

    /**
     * <p>
     * Gets a summary of the time that the timer recorded as a string.
     * </p>
     * 
     * <p>
     * The format used is ISO8601-like, <i>hours</i>:<i>minutes</i>:<i>seconds</i>.<i>milliseconds</i>.
     * </p>
     * 
     * @return the time as a String
     */
    public String toString()
    {
        final long time = time();

        if (time > 1000000L)
        {
            long timeInMillis = TimeUnit.NANOSECONDS.toMillis(time);
            long timeInSeconds = seconds();
            long hours = timeInSeconds / 3600L;

            timeInSeconds -= hours * 3600L;
            long minutes = timeInSeconds / 60L;
            timeInSeconds -= minutes * 60L;
            long seconds = timeInSeconds;

            return String.format("%s hour(s) %s minute(s) %s second(s) %s millis", hours, minutes, seconds, timeInMillis);
        }
        else
        {
            return String.format("%s  nanosecond(s)", time);
        }
    }

    /**
     * Returns the interval between the begin and the end in milliseconds.
     * 
     * @return The interval between the begin and the end in milliseconds.
     */
    public long millis()
    {
        return this.timeUnitType.unit().toMillis(time());
    }

    /**
     * Returns the interval between the begin and the end in seconds.
     * 
     * @return The interval between the begin and the end in seconds.
     */
    public long seconds()
    {
        return this.timeUnitType.unit().toSeconds(time());
    }

    /**
     * Returns the interval between the begin and the end in minutes.
     * 
     * @return The interval between the begin and the end in minutes.
     */
    public long minutes()
    {
        return this.timeUnitType.unit().toMinutes(time());
    }

    /**
     * Returns the interval between the begin and the end in minutes.
     * 
     * @return The interval between the begin and the end in minutes.
     */
    public long hours()
    {
        return this.timeUnitType.unit().toHours(time());
    }

    /**
     * Converts the given value expressed in nanoseconds to milliseconds.
     * 
     * @param valueInNanoseconds
     *            The value in nanoseconds to be converted to milliseconds.
     * @return The given value converted to milliseconds.
     */
    public static long nanoToMillis(long valueInNanoseconds)
    {
        return TimeUnit.NANOSECONDS.toMillis(valueInNanoseconds);
    }
}
