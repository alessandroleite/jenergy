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

import jenergy.utils.Timer;

public class MethodStatistics implements Comparable<MethodStatistics>
{
    /**
     * The thread if which the method was executed.
     */
    private final long threadId;

    /**
     * The method's name.
     */
    private final String name;

    /**
     * The user time of the method.
     */
    private volatile long time;

    /**
     * The CPU time of the method.
     */
    private volatile long cpuTime;

    /**
     * The number of invocation of the method.
     */
    private long numberOfInvocations;

    /**
     * The minimum execution time of the method.
     */
    private long min = Long.MAX_VALUE;

    /**
     * The maximum execution time of the method.
     */
    private long max = Long.MIN_VALUE;

    /**
     * The CPU power consumption of this method.
     */
    private double cpuPower;

    /**
     * @param method
     *            The name of the method. Might not be <code>null</code> or empty.
     * @param tid
     *            The thread id which the method was executed.
     */
    public MethodStatistics(String method, long tid)
    {
        this.name = method;
        this.threadId = tid;
    }

    /**
     * Increment the user time with the given value.
     * 
     * @param timeSpent
     *            The value to be incremented of the user time.
     */
    public void addTime(final long timeSpent)
    {
        min = Math.min(min, timeSpent);
        max = Math.max(max, timeSpent);
        numberOfInvocations++;

        this.time += timeSpent;
    }

    /**
     * Increments the CPU time of the method.
     * 
     * @param cpuTimeSpent
     *            The CPU time to be incremented.
     */
    public void addCpuTime(final long cpuTimeSpent)
    {
        this.cpuTime += cpuTimeSpent;
    }

    /**
     * @return the threadId
     */
    public long getThreadId()
    {
        return threadId;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return the time
     */
    public long getTime()
    {
        return time;
    }

    /**
     * @return the cpuTime
     */
    public long getCpuTime()
    {
        return cpuTime;
    }

    /**
     * @return the numberOfInvocations
     */
    public long getNumberOfInvocations()
    {
        return numberOfInvocations;
    }

    /**
     * @return the min
     */
    public long getMin()
    {
        return min;
    }

    /**
     * @return the max
     */
    public long getMax()
    {
        return max;
    }

    /**
     * @return the cpuPower
     */
    public double getCpuPower()
    {
        return cpuPower;
    }

    /**
     * @param newCpuPowerValue
     *            the cpuPower to set
     */
    public void setCpuPower(double newCpuPowerValue)
    {
        this.cpuPower = newCpuPowerValue;
    }

    @Override
    public int compareTo(MethodStatistics o)
    {
        return time < o.time ? -1 : (time == o.time ? 0 : 1);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + (int) (time ^ (time >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        MethodStatistics other = (MethodStatistics) obj;
        if (name == null)
        {
            if (other.name != null)
            {
                return false;
            }
        }
        else if (!name.equals(other.name))
        {
            return false;
        }
        if (time != other.time)
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(String.format("%s time: %dms cpu: %dms", getName(), Timer.nanoToMillis(time),
                Timer.nanoToMillis(cpuTime)));
        sb.append(String.format(" (min: %dms, max: %dms) - %d invocations", Timer.nanoToMillis(min), Timer.nanoToMillis(max), numberOfInvocations));

        return sb.toString();
    }
}
