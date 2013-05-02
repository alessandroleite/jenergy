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
package jenergy.agent;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Collection;

import jenergy.profile.ThreadProfiler;
import jenergy.profile.data.MethodInfo;
import jenergy.profile.data.ThreadInfo;
import jenergy.utils.ThreadProfilers;
import jenergy.utils.Timer;

public final class Cpu
{
    /**
     * Global instance of the CPU.
     */
    private static final Cpu INSTANCE = new Cpu();

    /**
     * The time sampling defined by the system's variable (-D) <em>jenergy.time.sampling</em> in milliseconds. The default is 10 milliseconds.
     */
    private static final Long DEFAULT_TIME_SAMPLING = Long.parseLong(System.getProperty("jenergy.time.sampling", "10"));

    /**
     * The thread executed by the CPU.
     */
    private final ThreadProfilers threads = new ThreadProfilers();

    /**
     * Private constructor to avoid more than one instance of this class.
     */
    private Cpu()
    {
    }

    public static final class CpuInfo implements Cloneable
    {
        /**
         * The CPU cycle duration.
         */
        private volatile long cycleDuration;

        /**
         * The CPU computation time. This time means how long the CPU was active.
         */
        private volatile long computationTime;

        /**
         * Default constructor.
         */
        public CpuInfo()
        {
            this.computationTime = System.nanoTime();
        }

        /**
         * Creates an instance of the {@link CpuInfo} with the same state of the given instance.
         * 
         * @param other
         *            The instance of be cloned.
         */
        protected CpuInfo(CpuInfo other)
        {
            this.cycleDuration = other.cycleDuration;
            this.computationTime = other.computationTime;
        }

        /**
         * Update the information about the CPU cycle duration.
         */
        public void updateCycleDuration()
        {
            this.cycleDuration = System.nanoTime() - computationTime;
            this.computationTime = System.nanoTime();
        }

        /**
         * Returns the CPU computation time.
         * 
         * @return the CPU computation time.
         */
        public long computationTime()
        {
            return this.computationTime;
        }

        /**
         * Returns the CPU cycle duration.
         * 
         * @return THe CPU cycle duration.
         */
        public long cycleDuration()
        {
            return this.cycleDuration;
        }

        @Override
        public CpuInfo clone()
        {
            try
            {
                return (CpuInfo) super.clone();
            }
            catch (CloneNotSupportedException e)
            {
                return new CpuInfo(this);
            }
        }
    }

    /**
     * Returns the global {@link Cpu} instance.
     * 
     * @return The global {@link Cpu} instance.
     */
    public static Cpu getInstance()
    {
        return INSTANCE;
    }

    /**
     * 
     * @param method
     *            The method to be profiled. Might not be <code>null</code>.
     * @return The instance of the method profiler.
     */
    public MethodInfo monitor(Method method)
    {
        MethodInfo profiler = new MethodInfo(method, Timer.start());
        ThreadProfiler threadProfiler = this.threads.get(profiler.getThreadId());

        if (threadProfiler == null)
        {
            threadProfiler = this.monitor(profiler.getThreadId());
        }

        threadProfiler.addMethod(profiler);
        return profiler;
    }

    /**
     * 
     * @param thread
     *            The thread to be monitored. Might not be <code>null</code>.
     * @return The instance of the {@link ThreadProfiler}.
     */
    public ThreadProfiler monitor(Thread thread)
    {
        return this.monitor(thread.getId());
    }

    /**
     * 
     * @param threadId
     *            The thread to be monitored. Might not be <code>zero</code>.
     * @return The instance of the {@link ThreadProfiler}.
     */
    public ThreadProfiler monitor(long threadId)
    {
        ThreadProfiler profiler = new ThreadProfiler(this, threadId, DEFAULT_TIME_SAMPLING);
        this.threads.put(threadId, profiler);
        new Thread(profiler, "Thread times monitor-" + threadId).start();

        return profiler;
    }

    /**
     * Returns the {@link Cpu} power consumption.
     * 
     * @return The {@link Cpu} power consumption.
     */
    public double power()
    {
        return 10;
    }

    /**
     * Returns the power consumption of a given thread.
     * 
     * @param threadId
     *            The thread to be returned its power consumption.
     * @return The power consumption of the given thread.
     */
    public BigDecimal getThreadCpuPower(long threadId)
    {
        return threads.get(threadId).getThreadInfo().getPower();
    }

    /**
     * Returns the data ({@link ThreadInfo}) about a given {@link Thread}.
     * 
     * @param tid
     *            The thread id to get the data.
     * @return The data of the given {@link Thread} or <code>null</code> if the given thread does not exist anymore.
     */
    public ThreadInfo getThread(long tid)
    {
        ThreadProfiler threadProfiler = getThreadProfiler(tid);
        return threadProfiler != null ? threadProfiler.getThreadInfo() : null;
    }

    /**
     * Returns the data ({@link ThreadProfiler}) about a given {@link Thread}.
     * 
     * @param tid
     *            The thread id to get the data.
     * @return The data of the given {@link Thread} or <code>null</code> if the given thread does not exist anymore.
     */
    public ThreadProfiler getThreadProfiler(long tid)
    {
        return this.threads.get(tid);
    }

    /**
     * Returns the total CPU time so far in nanoseconds.
     * 
     * @return the total CPU time so far in nanoseconds.
     */
    public long getTotalCpuTime()
    {
        final Collection<ThreadProfiler> threadHistory = threads.values();

        long time = 0L;

        for (ThreadProfiler times : threadHistory)
        {
            time += times.getThreadInfo().getTimes().getCpuTime().time();
        }

        return time;
    }

    /**
     * Returns the total user time so far in nanoseconds.
     * 
     * @return The total user time so far in nanoseconds.
     */
    public long getTotalUserTime()
    {
        final Collection<ThreadProfiler> threadHistory = threads.values();

        long time = 0L;

        for (ThreadProfiler times : threadHistory)
        {
            time += times.getThreadInfo().getTimes().getUserTime().time();
        }

        return time;
    }

    /**
     * Returns the total system time so far in nanoseconds.
     * 
     * @return The total system time so far in nanoseconds.
     */
    public long getTotalSystemTime()
    {
        return getTotalCpuTime() - getTotalUserTime();
    }

    /**
     * Starts the {@link ThreadTimesMonitor} to collect the data.
     */
    public void activate()
    {
        // long interval = Long.parseLong(System.getProperty("jenergy.collect.interval", "10"));
        // new ThreadTimesMonitor(interval, this).start();
    }
}
