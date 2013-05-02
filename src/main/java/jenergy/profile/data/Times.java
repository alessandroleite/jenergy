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
 */
package jenergy.profile.data;

import java.io.Serializable;

public final class Times implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 2607899185474926955L;

    /**
     * The thread id.
     */
    private final long threadId;

    /**
     * The CPU time information.
     */
    private Period cpuTime;

    /**
     * The user time information.
     */
    private volatile Period userTime;

    /**
     * 
     * @param tid
     *            The thread id.
     */
    public Times(long tid)
    {
        this.threadId = tid;
    }

    /**
     * 
     * @param id
     *            The thread id.
     * @param cpuTimes
     *            The cpu time.
     * @param userTimes
     *            the user time.
     */
    public Times(long id, Period cpuTimes, Period userTimes)
    {
        this(id);
        this.cpuTime = cpuTimes;
        this.userTime = userTimes;
    }

    /**
     * @return the threadId
     */
    public long getThreadId()
    {
        return threadId;
    }

    /**
     * @return the cpuTime
     */
    public Period getCpuTime()
    {
        return cpuTime;
    }

    /**
     * @param newCpuTime
     *            the cpuTime to set
     */
    public void setCpuTime(Period newCpuTime)
    {
        this.cpuTime = newCpuTime;
    }

    /**
     * @return the userTime
     */
    public Period getUserTime()
    {
        return userTime;
    }

    /**
     * @param newUserTime
     *            the userTime to set
     */
    public void setUserTime(Period newUserTime)
    {
        this.userTime = newUserTime;
    }
}
