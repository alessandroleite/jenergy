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

import java.lang.reflect.Method;

import jenergy.agent.Cpu;
import jenergy.profile.data.MethodInfo;
import jenergy.utils.Timer;

public final class MethodProfiler implements Profiler
{
    /**
     * The data about the method under monitoring.
     */
    private final MethodInfo info;

    /**
     * The state of this profile thread.
     */
    private volatile State state;

    /**
     * The CPU instance used to execute the method.
     */
    private final Cpu cpu;
    
    /**
     * Creates an instance of the profile for the given method.
     * 
     * @param method
     *            The method to be monitored. Might not be <code>null</code>.
     * @param cpuInstance
     *            The CPU instance with the data about the execution.
     */
    public MethodProfiler(Method method, Cpu cpuInstance)
    {
        StringBuilder methodName = new StringBuilder(method.getDeclaringClass().getName()).append(".").append(method.getName()).append("#")
                .append(Thread.currentThread().getId());

        info = new MethodInfo(methodName.toString(), Timer.start(), Thread.currentThread().getId());
        state = State.CREATED;
        this.cpu = cpuInstance;
    }

    @Override
    public void run()
    {
        /*if (State.CREATED.equals(this.state))
        {
            state = State.RUNNING;

            ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
            long threadCpuTime = threadMXBean.getThreadCpuTime(info.getThreadId());

            while (State.RUNNING.equals(state) && threadCpuTime != -1)
            {
                if (this.info.getCpuInfo().cycleDuration() > 0)
                {
                    try
                    {
                        double threadCpuPower = ((threadCpuTime / 1000000.0d) * cpu.power()) / this.info.getCpuInfo().cycleDuration();
                        info.setCpuPower(BigDecimal.valueOf(threadCpuPower));
                    }
                    catch (NumberFormatException e)
                    {
                        e.printStackTrace();
                    }
                }

                info.getTimes().setCpuTime(threadMXBean.getThreadCpuTime(info.getThreadId()));
                info.getTimes().setUserTime(threadMXBean.getThreadUserTime(info.getThreadId()));

                threadCpuTime = threadMXBean.getThreadCpuTime(info.getThreadId());
            }
        }
        state = State.FINISHED;

        dump();*/
    }

    /**
     * Print the information about the profiler.
     */
    private void dump()
    {
        System.err.printf("Method.....: %s\n", this.info.getMethodName());
        System.err.printf("Timer......: %s\n", this.info.getTimer());
        System.err.printf("CPU time...: %s\n", this.info.getTimes().getCpuTime());
        System.err.printf("User time..: %s\n", this.info.getTimes().getUserTime());
        System.err.printf("Power......: %s\n", this.info.getCpuPower());
        System.err.println();
    }

    @Override
    public State state()
    {
        return this.state;
    }

    @Override
    public void stop()
    {
        state = State.FINISHED;
        info.getTimer().stop();
    }

    /**
     * Returns <code>true</code> if the thread is finished; otherwise returns <code>false</code>.
     * 
     * @return <code>true</code> if the thread is finished; otherwise returns <code>false</code>.
     */
    public boolean isFinished()
    {
        return State.FINISHED.equals(state);
    }

    /**
     * Returns the data about the monitored method only when this thread has already finished. Otherwise, returns <code>null</code>.
     * 
     * @return the data about the monitored method only when this thread has already finished. Otherwise, returns <code>null</code>.
     */
    public MethodInfo getInfo()
    {
        return info;
    }

}
