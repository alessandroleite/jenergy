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
package jenergy.profile.data;

import java.io.Serializable;

public class Period implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -6527107885572126396L;

    /**
     * The start time of the period in nanoseconds.
     */
    private volatile long startTime;

    /**
     * The end time of the period in nanoseconds.
     */
    private volatile long endTime;

    /**
     * Creates a new {@link Period} with the start and end time.
     * 
     * @param start
     *            The start time in nanoseconds.
     * @param end
     *            The end time in nanoseconds.
     */
    public Period(long start, long end)
    {
        this(start);
        this.endTime = end;
    }

    /**
     * Creates a new {@link Period} with the start time.
     * 
     * @param start
     *            The start time in nanoseconds.
     */
    public Period(long start)
    {
        this.startTime = start;
    }

    /**
     * Default constructor.
     */
    public Period()
    {
    }

    /**
     * @return the startTime
     */
    public long getStartTime()
    {
        return startTime;
    }

    /**
     * @param newStartTimeValue
     *            the startTime to set
     */
    public void setStartTime(long newStartTimeValue)
    {
        this.startTime = newStartTimeValue;
    }

    /**
     * @return the endTime
     */
    public long getEndTime()
    {
        return endTime;
    }

    /**
     * @param newEndTimeValue
     *            the endTime to set
     */
    public void setEndTime(long newEndTimeValue)
    {
        this.endTime = newEndTimeValue;
    }

    /**
     * Returns the interval between the start and the end time in nanoseconds.
     * 
     * @return The interval between the start and the end time in nanoseconds.
     */
    public long time()
    {
        return this.endTime - this.startTime;
    }
}
