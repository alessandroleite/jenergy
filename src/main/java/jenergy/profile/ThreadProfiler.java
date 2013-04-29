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
import java.math.BigDecimal;

import jenergy.agent.Cpu;
import jenergy.profile.data.ThreadInfo;
import jenergy.utils.Timer;

public class ThreadProfiler implements Profiler
{
    /**
     * The Cpu instance of the thread.
     */
    private final Cpu cpu;

    /**
     * The information about the thread execution.
     */
    private final ThreadInfo threadInfo;

    /**
     * The state of this thread profiler.
     */
    private volatile State state;

    /**
     * Creates a new {@link ThreadProfiler} instance with the CPU and thread id.
     * 
     * @param threadCpu
     *            The thread's CPU. Might not be <code>null</code>.
     * @param tid
     *            The thread id. Might not be <code>null</code>.
     */
    public ThreadProfiler(Cpu threadCpu, Long tid)
    {
        this.cpu = threadCpu;
        threadInfo = new ThreadInfo(tid, Timer.start());
        this.state = State.CREATED;
    }

    @Override
    public void run()
    {
        if (State.CREATED.equals(this.state()))
        {
            state = State.RUNNING;
        }

        final ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();

        while (State.RUNNING.equals(this.state()))
        {
            if (mxBean != null)
            {
                double threadCpuTime = mxBean.getThreadCpuTime(threadInfo.getId()) / 1000000.0d;
                threadInfo.setPower(BigDecimal.valueOf(threadCpuTime * cpu.power() / cpu.cycleDuration()));
                threadInfo.getTimes().setCpuTime(mxBean.getThreadCpuTime(threadInfo.getId()));
                threadInfo.getTimes().setUserTime(mxBean.getThreadUserTime(threadInfo.getId()));
                threadInfo.setManagementInfo(mxBean.getThreadInfo(threadInfo.getId()));
            }
        }
    }

    @Override
    public State state()
    {
        return state;
    }

    @Override
    public void stop()
    {
        this.state = State.FINISHED;
        this.threadInfo.getTimer().stop();
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

}
