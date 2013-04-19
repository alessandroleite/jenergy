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
package jenergy.profile;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public final class ProfileInfo implements Serializable
{

    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 4932690613760153614L;

    /**
     * Number of times a thread is at a particular class/method/line combination.
     */
    private int tickCount;

    /**
     * The start time in nanoseconds.
     */
    private long start;

    /**
     * The time in nanoseconds when the execution finished.
     */
    private volatile long end;

    /**
     * The total CPU time for class/method/line in nanoseconds.
     */
    private volatile long cpuTime;

    /**
     * The total user CPU time for class/method/line in nanoseconds.
     */
    private volatile long userTime;

    /**
     * The method name
     */
    private String fullMethodName;

    private long threadId;

    private double cpuPower;

    public ProfileInfo()
    {
    }

    public ProfileInfo(String methodName, long startTime)
    {
        this.fullMethodName = methodName;
        this.start = startTime;
    }

    public ProfileInfo(String methodName, long startTime, long tid)
    {
        this(methodName, startTime);
        this.threadId = tid;
    }

    public int getTickCount()
    {
        return tickCount;
    }

    public void incrementTickCount()
    {
        this.tickCount++;
    }

    public long getStart()
    {
        return start;
    }

    public long getEnd()
    {
        return end;
    }

    public void setEnd(long end)
    {
        this.end = end;
    }

    public long getCpuTime()
    {
        return cpuTime;
    }

    public void setCpuTime(long cpuTime)
    {
        this.cpuTime = cpuTime;
    }

    public long getUserTime()
    {
        return userTime;
    }

    public void setUserTime(long userTime)
    {
        this.userTime = userTime;
    }

    public String getMethodName()
    {
        return fullMethodName;
    }

    public void setMethodName(String methodName)
    {
        this.fullMethodName = methodName;
    }

    public void setThreadId(long tid)
    {
        this.threadId = tid;
    }

    public long getThreadId()
    {
        return threadId;
    }

    public void setPower(double d)
    {
        this.cpuPower = d;
    }

    public double getCpuPower()
    {
        return cpuPower;
    }

    public double getMethodDuration()
    {
        if (this.end == 0)
        {
            return System.currentTimeMillis() - this.start;
        }

        return this.end - this.start;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
