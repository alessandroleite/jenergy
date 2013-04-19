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

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public final class JEnergyProfiler implements Runnable
{

    /**
     * Enum with the valid states of a profiler thread.
     */
    public static enum State
    {
        CREATED, RUNNING, FINISHED;
    }

    /**
     * The data about the method under monitoring.
     */
    private final ProfileInfo info;

    /**
     * The state of this profile thread.
     */
    private volatile State state;

    /**
     * Creates an instance of the profile for the given method.
     * 
     * @param method
     *            The method to be monitored. Might not be <code>null</code>.
     */
    public JEnergyProfiler(Method method)
    {
        StringBuilder methodName = new StringBuilder(method.getDeclaringClass().getName()).append(".").append(method.getName()).append("#")
                .append(Thread.currentThread().getId());

        info = new ProfileInfo(methodName.toString(), System.currentTimeMillis());
        info.setThreadId(Thread.currentThread().getId());
        state = State.CREATED;
    }

    @Override
    public void run()
    {
        state = State.RUNNING;
        Cpu cpu = new Cpu();

        while (State.RUNNING.equals(state))
        {
            cpu.updateCycleDuration();
            Map<Long, Double> threadsCpuPower = this.getThreadCPUPower(cpu);
            Double threadCpuPower = threadsCpuPower.get(info.getThreadId());

            if (threadCpuPower != null)
            {
                // methCPUPower = (methCPUTime.get(methName) * threadCPUPower.get(tid)) / CPU.cycleDuration;
                double d = (info.getCpuTime() * threadCpuPower) / cpu.cycleDuration();
                info.setPower(d);
            }
        }
    }

    public Map<Long, Double> getThreadCPUPower(Cpu cpu)
    {
        Map<Long, Double> threadsPower = new HashMap<Long, Double>();

        final ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();

        if (mxBean != null)
        {
            for (Long tid : mxBean.getAllThreadIds())
            {
                double threadCpuTime = mxBean.getThreadCpuTime(tid) / 1000000.0;
                Double threadCPUTime = threadCpuTime;

                // if (Agent.lastThreadCPUTime.containsKey(id))
                // {
                // threadCPUTime = newThreadCPUTime - Agent.lastThreadCPUTime.get(id);
                // }

                threadsPower.put(tid, (threadCPUTime * cpu.power()) / cpu.cycleDuration());
            }
        }
        return threadsPower;
    }

    /**
     * Sets the execution time of the method.
     * 
     * @param endExecutionTimeInMillis
     *            The times in milliseconds that represents the times spent in the method execution.
     */
    public void endMethodExecution(long endExecutionTimeInMillis)
    {
        info.setEnd(endExecutionTimeInMillis);
        state = State.FINISHED;
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
    public ProfileInfo getInfo()
    {
        return this.isFinished() ? info : null;
    }
}
