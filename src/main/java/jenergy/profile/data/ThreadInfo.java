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
import java.math.BigDecimal;

import jenergy.agent.common.Cpu.CpuInfo;
import jenergy.agent.common.util.time.Timer;

public final class ThreadInfo implements Serializable, Cloneable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 6768125970121239760L;

    /**
     * The thread id. Might be greater than zero.
     */
    private final long id;

    /**
     * The CPU times of this thread.
     */
    private final CpuInfo cpuInfo;

    /**
     * The computation times of the thread.
     */
    private final Times times;

    /**
     * The timer with the time spend by the thread.
     */
    private Timer timer;

    /**
     * The estimated power consumption of this thread.
     */
    private BigDecimal power;

    /**
     * The thread management info.
     */
    private transient java.lang.management.ThreadInfo managementInfo;

    /**
     * Creates a new {@link ThreadInfo} instance of a given thread.
     * 
     * @param tid
     *            The thread ID. Might be greater than zero.
     */
    public ThreadInfo(long tid)
    {
        this.id = tid;
        this.cpuInfo = new CpuInfo();
        this.times = new Times(tid);
    }

    /**
     * Creates a new {@link ThreadInfo} instance of a given thread.
     * 
     * @param tid
     *            The thread ID. Might be greater than zero.
     * @param timerInstance
     *            The thread's timer.
     */
    public ThreadInfo(long tid, Timer timerInstance)
    {
        this(tid);
        this.timer = timerInstance;
    }

    /**
     * Creates an instance of the {@link ThreadInfo} cloning the state of a given {@link ThreadInfo}.
     * 
     * @param other
     *            The {@link ThreadInfo} to be cloned. Might not be <code>null</code>.
     */
    protected ThreadInfo(ThreadInfo other)
    {
        this.id = other.getId();
        this.cpuInfo = other.getCpuInfo();
        this.times = other.getTimes();
        this.timer = other.getTimer();
        this.power = other.getPower();
    }

    /**
     * @return the timer
     */
    public Timer getTimer()
    {
        return timer;
    }

    /**
     * @param newTimerInstance
     *            the timer to set
     */
    public void setTimer(Timer newTimerInstance)
    {
        this.timer = newTimerInstance;
    }

    /**
     * @return the power
     */
    public BigDecimal getPower()
    {
        return power;
    }

    /**
     * @param newPowerValue
     *            the power to set
     */
    public void setPower(BigDecimal newPowerValue)
    {
        this.power = newPowerValue;
    }

    /**
     * @return the id
     */
    public long getId()
    {
        return id;
    }

    /**
     * @return the times
     */
    public Times getTimes()
    {
        return times;
    }

    /**
     * @return the managementInfo
     */
    public java.lang.management.ThreadInfo getManagementInfo()
    {
        return managementInfo;
    }

    /**
     * @param threadInfo
     *            the managementInfo to set
     */
    public void setManagementInfo(java.lang.management.ThreadInfo threadInfo)
    {
        this.managementInfo = threadInfo;
    }

    /**
     * @return the cpuInfo
     */
    public CpuInfo getCpuInfo()
    {
        return cpuInfo;
    }

    @Override
    public ThreadInfo clone()
    {
        try
        {
            return (ThreadInfo) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            return new ThreadInfo(this);
        }
    }
}
