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
package jenergy.profile;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import jenergy.agent.common.Cpu;
import jenergy.profile.data.MethodInfo;
import jenergy.profile.data.MethodStatistics;
import jenergy.profile.data.Period;
import jenergy.profile.data.ThreadInfo;
import jenergy.util.Threads;
import jenergy.util.time.Timer;

public class ThreadProfiler implements Profiler
{

    /**
     * The method statistics executed in this thread. The key is the method's name and the values is a {@link List} with the information about the
     * method execution.
     */
    private final Map<String, List<MethodInfo>> threadMethods = new ConcurrentHashMap<String, List<MethodInfo>>();
    
    /**
     * The stack trace of this thread.
     */
    private final List<Method> stack = new CopyOnWriteArrayList<Method>();

    /**
     * The Cpu instance of the thread.
     */
    private final Cpu cpu;

    /**
     * The information about the thread execution.
     */
    private final ThreadInfo threadInfo;

    /**
     * The interval to collect the data.
     */
    private final long timeSample;

    /**
     * The flag to indicate if the thread must be continue running.
     */
    private volatile boolean active = Boolean.TRUE;

    /**
     * Creates a new {@link ThreadProfiler} instance with the CPU and thread id.
     * 
     * @param threadCpu
     *            The thread's CPU. Might not be <code>null</code>.
     * @param tid
     *            The thread id. Might not be <code>null</code>.
     * @param timeSampling
     *            The time sampling of this thread.
     */
    public ThreadProfiler(Cpu threadCpu, Long tid, Long timeSampling)
    {
        this.cpu = threadCpu;
        threadInfo = new ThreadInfo(tid, Timer.createAndStart());
        this.timeSample = timeSampling;
    }

    @Override
    public void run()
    {
        while (active)
        {
            update();
            Threads.sleep(timeSample, true);
        }
    }

    /**
     * Update the hash table of thread times.
     */
    private void update()
    {
        final ThreadMXBean bean = ManagementFactory.getThreadMXBean();

        if (bean == null)
        {
            return;
        }

        final long id = this.threadInfo.getId();

        this.getThreadInfo().getCpuInfo().updateCycleDuration();

        final long cpuTime = bean.getThreadCpuTime(id);
        final long userTime = bean.getThreadUserTime(id);

        if (cpuTime != -1 && userTime != -1)
        {
            if (getThreadInfo().getTimes().getCpuTime() == null)
            {
                getThreadInfo().getTimes().setCpuTime(new Period(cpuTime, cpuTime));
                getThreadInfo().getTimes().setUserTime(new Period(userTime, userTime));
            }
            else
            {
                getThreadInfo().getTimes().getCpuTime().setEndTime(cpuTime);
                getThreadInfo().getTimes().getUserTime().setEndTime(userTime);
            }
            // computeThreadPowerConsumption(cpuTime);
        }
    }

    /**
     * Computes and returns the power consumption of the thread.
     * 
     * @param cpuTime
     *            The cpu time in nanoseconds.
     * @return The power consumption of the thread.
     */
    public double computeThreadPowerConsumption(long cpuTime)
    {
        if (this.getThreadInfo().getCpuInfo().cycleDuration() > 0)
        {
            long threadCpuTime = threadInfo.getTimes().getCpuTime().time() == 0 ? cpuTime : threadInfo.getTimes().getCpuTime().time();
            double threadCpuPower = Timer.nanoToMillis(threadCpuTime) * this.cpu.power() / 
                    Timer.nanoToMillis(this.getThreadInfo().getCpuInfo().cycleDuration());

            return threadCpuPower;
        }
        return 0d;
    }

    @Override
    public void stop()
    {
        this.active = Boolean.FALSE;
    }

    /**
     * Add a new method execution of this thread.
     * 
     * @param method
     *            The method execution of this thread to be monitored.
     */
    public void addMethod(MethodInfo method)
    {
        List<MethodInfo> methodList = this.threadMethods.get(method.getMethodName());

        if (methodList == null)
        {
            methodList = new CopyOnWriteArrayList<MethodInfo>();
            this.threadMethods.put(method.getMethodName(), methodList);
        }
        
        this.stack.add(method.getMethodRef());

        methodList.add(method);
    }
    

    /**
     * Returns an array of methods representing the stack dump of the thread. 
     * 
     * @return An array of methods representing the stack dump of the thread.
     */
    public Method[] getStackTrace()
    {
        Method[] stackTrace = new Method[this.stack.size()];

        int size = this.stack.size() - 1;
        for (int i = size; i >= 0; i++)
        {
            stackTrace[i - size] = stack.get(i);
        }
        
        return stackTrace;
    }
    
    /**
     * Removes the method of the head of this thread stack.
     * @return The method removed or <code>null</code> if the stack is empty.
     */
    public Method popStack()
    {
        return this.stack.remove(this.stack.size() - 1);
    }
    
    /**
     * Returns the method of the head (top) of this thread stack.
     * @return The method of the head (top) of this thread stack or <code>null</code> if the stack is empty.
     */
    public Method peekStack()
    {
        return this.stack.get(this.stack.size() - 1);
    }

    /**
     * @param methodName
     *            The method name.
     * @return Returns a {@link List} that represents the stack of the given method.
     */
    public List<MethodInfo> getMethodInfoByName(String methodName)
    {
        return this.threadMethods.get(methodName);
    }
    
    /**
     * Returns the caller of the given method.
     * 
     * @param method The method to return the caller.
     * @return The caller of the given method.
     */
    public MethodInfo getCallerOf(MethodInfo method)
    {
        if (method != null && method.getCalleeMethod() != null)
        {
            String caller = MethodInfo.formatMethodName(method.getCalleeMethod().getClassName(), method.getCalleeMethod().getMethodName());
            List<MethodInfo> methodStack = getMethodInfoByName(caller);

            return methodStack.get(methodStack.size() - 1);
        }
        return new MethodInfo("<null method>", Timer.createAndStart());
    }

    /**
     * Returns a reference to the {@link MethodInfo} of the given {@link Method}.
     * 
     * @param method
     *            The method to return its {@link MethodInfo} reference. Might not be <code>null</code>.
     * @return A reference to the {@link MethodInfo} of the given {@link Method} or <code>null</code> if the {@link Method} does not exists in this
     *         {@link ThreadProfiler}.
     * @throws NullPointerException If the given method was <code>null</code>
     */
    public MethodInfo getMethodInfoOf(Method method)
    {
        if (method == null)
        {
            throw new NullPointerException("The method might not be null!");
        }
        
        String caller = MethodInfo.formatMethodName(method.getDeclaringClass().getName(), method.getName());
        
        return getMethodInfoOf(caller);
    }
    
    /**
     * Returns a reference to the {@link MethodInfo} of the given {@link Method}.
     * 
     * @param method
     *            The method to return its {@link MethodInfo} reference. Might not be <code>null</code>.
     * @return A reference to the {@link MethodInfo} of the given {@link Method} or <code>null</code> if the {@link Method} does not exists in this
     *         {@link ThreadProfiler}.
     * @throws NullPointerException If the given method was <code>null</code>
     */
    public MethodInfo getMethodInfoOf(String method)
    {
        List<MethodInfo> methodStack = this.getMethodInfoByName(method);
        return methodStack == null || methodStack.isEmpty() ? null : methodStack.get(methodStack.size() - 1);
    }
    
    /**
     * Returns the {@link MethodInfo} of the method that is currently executed by this thread.
     * 
     * @return the {@link MethodInfo} of the method that is currently executed by this thread or <code>null</code> if this thread is not executing any
     *         method.
     */
    public MethodInfo peekMethodInfo()
    {
        return getMethodInfoOf(this.peekStack());
    }

    /**
     * Returns the thread profiler info.
     * 
     * @return The {@link ThreadInfo} with the data about the thread execution.
     */
    public ThreadInfo getThreadInfo()
    {
        return threadInfo;
    }

    /**
     * Returns <code>true</code> if the {@link ThreadProfiler} is still active or <code>false</code> otherwise.
     * 
     * @return <code>true</code> if the {@link ThreadProfiler} is still active or <code>false</code> otherwise.
     */
    public boolean isActive()
    {
        return active;
    }

    /**
     * Update the power consumption of the thread's methods.
     * 
     * @return The statistics of all method of the thread.
     */
    public Map<String, MethodStatistics> computeCpuPowerConsumptionOfThreadMethods()
    {
        final Map<String, MethodStatistics> methodStatistics = this.getMethodStatistics();
        final long allMethDuration = allThreadMethodDuration(methodStatistics);
        final long threadCpuTime = this.getThreadInfo().getTimes().getCpuTime().time();
        final double threadCpuPower = this.getThreadInfo().getPower().doubleValue();

        for (MethodStatistics statistics : methodStatistics.values())
        {
            final double methodCpuTime = Timer.nanoToMillis((statistics.getTime() * threadCpuTime) / allMethDuration);

            if (methodCpuTime > 0)
            {
                double v = (methodCpuTime * this.getThreadInfo().getPower().doubleValue()) / 
                        Timer.nanoToMillis(this.getThreadInfo().getCpuInfo().cycleDuration());

                v = (methodCpuTime * threadCpuPower) / Timer.nanoToMillis(statistics.getTime());
                statistics.setCpuPower(v);
            }
        }
        return methodStatistics;
    }

    /**
     * Returns the duration of all methods.
     * 
     * @param statistics
     *            The {@link Map} with the method to compute the execution time.
     * @return The duration of the execution of all methods.
     */
    protected Long allThreadMethodDuration(Map<String, MethodStatistics> statistics)
    {
        long duration = 0L;

        for (MethodStatistics value : statistics.values())
        {
            duration += value.getTime();
        }
        return duration;
    }

    /***
     * Returns a read-only {@link Map} which the key is the method's name and the value is its execution time in milliseconds.
     * 
     * @return A read-only {@link Map} which the key is the method's name and the value is its execution time in milliseconds.
     */
    protected Map<String, MethodStatistics> getMethodStatistics()
    {
        Map<String, MethodStatistics> meths = new HashMap<String, MethodStatistics>();

        for (List<MethodInfo> methods : this.threadMethods.values())
        {
            for (MethodInfo method : methods)
            {
                MethodStatistics statistics = meths.get(method.getMethodName());

                if (statistics == null)
                {
                    statistics = new MethodStatistics(method.getMethodName(), method.getThreadId());
                    meths.put(method.getMethodName(), statistics);
                }

                statistics.addTime(method.getTimer().time());
                statistics.addCpuTime(method.getTimes().getCpuTime().time());
            }
        }

        return Collections.unmodifiableMap(meths);
    }
}
