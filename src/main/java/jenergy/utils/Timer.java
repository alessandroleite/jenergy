/**
 * Copyright 2013 Alessandro
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
 */
package jenergy.utils;

import java.io.Serializable;

public final class Timer implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -7331421405049501760L;

    /**
     * The time when this {@link Timer} start.
     */
    private volatile long beginTimeInMillis;

    /**
     * The time when this {@link Timer} stop.
     */
    private volatile long endTimeInMillis;

    /**
     * A flag to indicate if the timer had already finished.
     */
    private volatile boolean stoped;

    /**
     * Private constructor to avoid instance of this class without invoke the method {@link #start()}.
     */
    private Timer()
    {
        this.beginTimeInMillis = System.currentTimeMillis();
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
            this.endTimeInMillis = System.currentTimeMillis();
        }
    }

    /**
     * Returns the interval between the begin and the end in milliseconds.
     * 
     * @return The interval between the begin and the end in milliseconds.
     */
    public long time()
    {
        return (stoped ? this.endTimeInMillis : System.currentTimeMillis()) - this.beginTimeInMillis;
    }

    /**
     * Returns the interval between the begin and the end in seconds.
     * 
     * @return The interval between the begin and the end in seconds.
     */
    public double seconds()
    {
        return time() / 1000.0;
    }

    /**
     * Returns the interval between the begin and the end in minutes.
     * 
     * @return The interval between the begin and the end in minutes.
     */
    public double minutes()
    {
        return time() / 60000.0;
    }

    /**
     * Returns the interval between the begin and the end in minutes.
     * 
     * @return The interval between the begin and the end in minutes.
     */
    public double hours()
    {
        return time() / 3600000.0;
    }

    @Override
    public String toString()
    {
        final long time = time();

        if (time > 1000L)
        {
            long timeInSeconds = time / 1000L;
            long hours = timeInSeconds / 3600L;
            timeInSeconds -= hours * 3600L;
            long minutes = timeInSeconds / 60L;
            timeInSeconds -= minutes * 60L;
            long seconds = timeInSeconds;

            return String.format("%s hour(s) %s minute(s) %s second(s)", hours, minutes, seconds);
        }
        else
        {
            return String.format("%s  millisecond(s)", time);
        }
    }
}
