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
package jenergy.agent;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jenergy.profile.MethodProfiler;
import jenergy.profile.ThreadProfiler;

public final class Cpu implements Runnable
{
    /**
     * Global instance of the CPU.
     */
    private static final Cpu INSTANCE = new Cpu();

    /**
     * 
     */
    private final Map<Long, ThreadProfiler> threads = new ConcurrentHashMap<Long, ThreadProfiler>();

    /**
     * 
     */
    private final Map<String, List<MethodProfiler>> methods = new ConcurrentHashMap<String, List<MethodProfiler>>();

    /**
     * The data of this CPU.
     */
    private final CpuInfo cpuInfo = new CpuInfo();
    
    
    /**
     * Flag to indicate if the CPU is active.
     */
    private volatile boolean active;

    /**
     * Private constructor to avoid instance of this class.
     */
    private Cpu()
    {
    }

    private static final class CpuInfo
    {
        /**
         * The CPU cycle duration.
         */
        private volatile long cycleDuration;

        /**
         * The CPU computation time. This time means how long the CPU is active.
         */
        private volatile long computationTime;

        /**
         * Update the information about the CPU cycle duration.
         */
        public void updateCycleDuration()
        {
            synchronized (this)
            {
                this.cycleDuration = System.currentTimeMillis() - computationTime;
                this.computationTime = System.currentTimeMillis();
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
    public MethodProfiler monitor(Method method)
    {
        synchronized (this.methods)
        {
            MethodProfiler profiler = new MethodProfiler(method, this);
            ThreadProfiler threadProfiler = this.threads.get(profiler.getInfo().getThreadId());

            if (threadProfiler == null)
            {
                threadProfiler = this.monitor(Thread.currentThread());
            }

            profiler.getInfo().setThreadInfo(threadProfiler.getThreadInfo());

            List<MethodProfiler> profiles = this.methods.get(method.getName());

            if (profiles == null)
            {
                profiles = new ArrayList<MethodProfiler>();
            }

            profiles.add(profiler);

            this.methods.put(method.getName(), profiles);

            new Thread(profiler).start();
            return profiler;
        }
    }

    /**
     * 
     * @param thread
     *            The thread to be monitored. Might not be <code>null</code>.
     * @return The instance of the {@link ThreadProfiler}.
     */
    public ThreadProfiler monitor(Thread thread)
    {
        ThreadProfiler profiler = new ThreadProfiler(this, thread.getId());
        this.threads.put(thread.getId(), profiler);

        new Thread(profiler).start();
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
     * Returns the CPU computation time.
     * 
     * @return the CPU computation time.
     */
    public long computationTime()
    {
        return this.cpuInfo.computationTime;
    }

    /**
     * Returns the CPU cycle duration.
     * 
     * @return THe CPU cycle duration.
     */
    public long cycleDuration()
    {
        return this.cpuInfo.cycleDuration;
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

    @Override
    public void run()
    {
        while (active)
        {
            this.cpuInfo.updateCycleDuration();
        }
    }

    /**
     * Starts the CPU's thread.
     */
    public void activate()
    {
        active = true;
        new Thread(this).start();
    }
}
