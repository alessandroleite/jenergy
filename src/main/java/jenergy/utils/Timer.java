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
package jenergy.utils;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public final class Timer implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -7331421405049501760L;

    /**
     * The time when this {@link Timer} start.
     */
    private volatile long beginTimeInNano;

    /**
     * The time when this {@link Timer} stop.
     */
    private volatile long endTimeInNano;

    /**
     * A flag to indicate if the timer had already finished.
     */
    private volatile boolean stoped;

    /**
     * Private constructor to avoid instance of this class without invoke the method {@link #start()}.
     */
    private Timer()
    {
        this.beginTimeInNano = System.nanoTime();
    }

    /**
     * Starts and returns a new {@link Timer}.
     * 
     * @return A {@link Timer} with the time started.
     */
    public static Timer start()
    {
        return new Timer();
    }

    /**
     * Stops the timer.
     */
    public void stop()
    {
        if (!stoped)
        {
            this.stoped = true;
            this.endTimeInNano = System.nanoTime();
        }
    }

    /**
     * Restart this {@link Timer}.
     */
    public void restart()
    {
        this.stoped = false;
        this.endTimeInNano = 0;
        this.beginTimeInNano = System.nanoTime();
    }

    /**
     * Returns the interval between the begin and the end in nanoseconds.
     * 
     * @return The interval between the begin and the end in nanoseconds.
     */
    public long time()
    {
        return (stoped ? this.endTimeInNano : System.nanoTime()) - this.beginTimeInNano;
    }

    /**
     * Returns the interval between the begin and the end in seconds.
     * 
     * @return The interval between the begin and the end in seconds.
     */
    public long millis()
    {
        return TimeUnit.NANOSECONDS.toMillis(time());
    }

    /**
     * Returns the interval between the begin and the end in seconds.
     * 
     * @return The interval between the begin and the end in seconds.
     */
    public long seconds()
    {
        return TimeUnit.NANOSECONDS.toSeconds(time());
    }

    /**
     * Returns the interval between the begin and the end in minutes.
     * 
     * @return The interval between the begin and the end in minutes.
     */
    public long minutes()
    {
        return TimeUnit.NANOSECONDS.toMinutes(time());
    }

    /**
     * Returns the interval between the begin and the end in minutes.
     * 
     * @return The interval between the begin and the end in minutes.
     */
    public long hours()
    {
        return TimeUnit.NANOSECONDS.toHours(time());
    }

    @Override
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
