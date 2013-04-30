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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import jenergy.agent.Cpu;
import jenergy.profile.data.Period;
import jenergy.profile.data.ThreadInfo;
import jenergy.profile.data.Times;
import jenergy.utils.Timer;

public class ThreadProfiler implements Profiler
{
    
    /**
     * The method statistics executed in this thread.
     */
    private final Map<String, List<MethodProfiler>> threadMethods = new ConcurrentHashMap<String, List<MethodProfiler>>();
    
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
    // private volatile State state;

    private final long timeSample;
    
    /**
     * The thread id of this monitor. 
     */
    private final long monitorThreadId;
    
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
        threadInfo = new ThreadInfo(tid, Timer.start());
        // this.state = State.CREATED;
        this.timeSample = timeSampling;
        this.monitorThreadId = Thread.currentThread().getId();
    }

    @Override
    public void run()
    {
        while (!Thread.currentThread().isInterrupted())
        {
            update();
            try
            {
                Thread.sleep(timeSample);
            }
            catch (InterruptedException exception)
            {
                break;
            }
        }
        
        
        /*
         * if (State.CREATED.equals(this.state())) { state = State.RUNNING; }
         * 
         * final ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();
         * 
         * while (State.RUNNING.equals(this.state())) { final long threadCpuTime = mxBean.getThreadCpuTime(threadInfo.getId());
         * 
         * if (mxBean != null && threadCpuTime > 0) { if (this.threadInfo.getCpuInfo().cycleDuration() > 0) { threadInfo
         * .setPower(BigDecimal.valueOf((threadCpuTime / 1000000.0d) * cpu.power() / this.threadInfo.getCpuInfo().cycleDuration()));
         * threadInfo.getTimes().setCpuTime(threadCpuTime); threadInfo.getTimes().setUserTime(mxBean.getThreadUserTime(threadInfo.getId()));
         * threadInfo.setManagementInfo(mxBean.getThreadInfo(threadInfo.getId())); } else { System.out.println("cycle duration was zero !!!!"); } } }
         */
    }
    
    
    /**
     * Update the hash table of thread times.
     */
    private void update()
    {
        final ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        final long[] ids = bean.getAllThreadIds();

        this.getThreadInfo().getCpuInfo().updateCycleDuration();
        
        for (long id : ids)
        {
            
            if (id == monitorThreadId)
            {
                continue;
            }

            final long cpuTime = bean.getThreadCpuTime(id);
            final long userTime = bean.getThreadUserTime(id);

            if (cpuTime == -1 || userTime == -1)
            {
                continue;
            }

            ThreadInfo info = cpu.getThread(id);
            
            if (info == null)
            {
                // info = cpu.monitor(id).getThreadInfo();
                continue;
            }
            
            Times times = info.getTimes();
            
            if (times == null)
            {
                times = new Times(id, new Period(cpuTime, cpuTime), new Period(userTime, userTime));
                info.setTimes(times);
            }
            else
            {
                times.getCpuTime().setEndTime(cpuTime);
                times.getUserTime().setEndTime(userTime);
            }
        }
    }

    @Override
    public State state()
    {
        // return state;
        return null;
    }

    @Override
    public void stop()
    {
        // if (!State.FINISHED.equals(state))
        // {
        // this.state = State.FINISHED;
        // this.threadInfo.getTimer().stop();
        // }
    }

    /**
     * Add a new method execution of this thread.
     * 
     * @param method
     *            The method execution of this thread to be monitored.
     */
    public void addMethod(MethodProfiler method)
    {
        List<MethodProfiler> methodList = this.threadMethods.get(method.getInfo().getMethodName());

        if (methodList == null)
        {
            methodList = new CopyOnWriteArrayList<MethodProfiler>();
            this.threadMethods.put(method.getInfo().getMethodName(), methodList);
        }
        methodList.add(method);
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
     * Update the thread profile state.
     * 
     * @param newState
     *            The new state to be set.
     */
    // protected void setState(State newState)
    // {
    // this.state = newState;
    // }
}
