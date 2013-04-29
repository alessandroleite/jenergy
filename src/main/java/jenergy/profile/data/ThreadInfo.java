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

import jenergy.utils.Timer;

public final class ThreadInfo implements Serializable
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
     * The thread name.
     */
    private final String name;

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
        this(tid, Thread.currentThread().getName());
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
     * Creates a new {@link ThreadInfo} instance of a given thread.
     * 
     * @param tid
     *            The thread ID. Might be greater than zero.
     * @param threadName
     *            The thread name. Might not be <code>null</code> or empty.
     */
    public ThreadInfo(long tid, String threadName)
    {
        this.id = tid;
        this.name = threadName;
        this.times = new Times();
    }

    /**
     * Creates a new {@link ThreadInfo} instance of a given thread.
     * 
     * @param tid
     *            The thread ID. Might be greater than zero.
     * @param threadName
     *            The thread name. Might not be <code>null</code> or empty.
     * @param threadTimer
     *            The {@link Timer}'s instance.
     */
    public ThreadInfo(long tid, String threadName, Timer threadTimer)
    {
        this(tid, threadName);
        this.timer = threadTimer;
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
     * @return the name
     */
    public String getName()
    {
        return name;
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
}
