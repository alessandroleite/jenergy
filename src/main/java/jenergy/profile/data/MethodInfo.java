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
import java.math.BigDecimal;

import jenergy.agent.Cpu.CpuInfo;
import jenergy.utils.Timer;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public final class MethodInfo implements Serializable
{

    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 6690005530794954253L;

    /**
     * Number of times a thread is at a particular class/method/line combination.
     */
    private volatile int tickCount;

    /**
     * The method timer.
     */
    private final Timer timer;

    /**
     * The method's times.
     */
    private Times times;

    // private ThreadInfo threadInfo;

    /**
     * The method name.
     */
    private final String methodName;

    /**
     * The thread id.
     */
    private long threadId;
    
    /**
     * The method's Cpu data.
     */
    private final CpuInfo cpuInfo;

    /**
     * The cpu power consumption.
     */
    private BigDecimal cpuPower;

    /**
     * @param name
     *            The name of the method to be analyzed.
     * @param methodTimer
     *            The {@link Timer} with the information about the execution time of the method.
     */
    public MethodInfo(String name, Timer methodTimer)
    {
        this.methodName = name;
        this.timer = methodTimer;
        this.cpuInfo = new CpuInfo();
    }

    /**
     * 
     * @param name
     *            The name of the method to be analyzed.
     * @param methodTimer
     *            The start time of the method execution.
     * @param tid
     *            The thread id.
     */
    public MethodInfo(String name, Timer methodTimer, long tid)
    {
        this(name, methodTimer);
        this.threadId = tid;
    }

    /**
     * @return the tickCount
     */
    public int getTickCount()
    {
        return tickCount;
    }

    /**
     * Increments the tick count.
     */
    public void incrementTickCount()
    {
        this.tickCount++;
    }

    /**
     * Returns the method's execution duration.
     * 
     * @return the method's execution duration.
     */
    public double getMethodDuration()
    {
        return this.timer.time();
    }

    /**
     * @return the times
     */
    public Times getTimes()
    {
        return times;
    }
    
    /**
     * @param newTimesInstance the times to set
     */
    public void setTimes(Times newTimesInstance)
    {
        this.times = newTimesInstance;
    }

    /**
     * @return the cpuPower
     */
    public BigDecimal getCpuPower()
    {
        return cpuPower;
    }

    /**
     * @param newCpuPowerValue
     *            the cpuPower to set
     */
    public void setCpuPower(BigDecimal newCpuPowerValue)
    {
        this.cpuPower = newCpuPowerValue;
    }

    /**
     * @return the timer
     */
    public Timer getTimer()
    {
        return timer;
    }

    /**
     * @return the methodName
     */
    public String getMethodName()
    {
        return methodName;
    }

    /**
     * @return the threadId
     */
    public long getThreadId()
    {
        return threadId;
    }
    
    /**
     * @return the cpuInfo
     */
    public CpuInfo getCpuInfo()
    {
        return cpuInfo;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
