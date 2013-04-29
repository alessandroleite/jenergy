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
package jenergy.profile.data;

import java.io.Serializable;

public final class Times implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code>.
     */
    private static final long serialVersionUID = 6501094740966040960L;

    /**
     * The user time information in milliseconds.
     */
    private volatile long userTime;

    /**
     * The CPU time information in milliseconds.
     */
    private volatile long cpuTime;

    /**
     * 
     * @param userTimeValue
     *            The userTime in milliseconds. Might not be zero or less than it.
     * @param cpuTimeValue
     *            The cpuTime The CPU time in milliseconds. Might not be zero or less than it.
     */
    public Times(long userTimeValue, long cpuTimeValue)
    {
        this.userTime = userTimeValue;
        this.cpuTime = cpuTimeValue;
    }

    /**
     * The default constructor.
     */
    public Times()
    {
    }

    /**
     * @return the userTime
     */
    public long getUserTime()
    {
        return userTime;
    }

    /**
     * @param newUserTime
     *            the userTime to set
     */
    public void setUserTime(long newUserTime)
    {
        synchronized (this)
        {
            this.userTime = newUserTime;
        }
    }

    /**
     * @return the cpuTime
     */
    public long getCpuTime()
    {
        return cpuTime;
    }

    /**
     * @param newCpuTime
     *            the cpuTime to set
     */
    public void setCpuTime(long newCpuTime)
    {
        synchronized (this)
        {
            this.cpuTime = newCpuTime;
        }
    }
}
