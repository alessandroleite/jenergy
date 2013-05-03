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

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import jenergy.agent.Cpu;

public final class ThreadTimesMonitor extends Thread
{
    /**
     * The interval in milliseconds to collect the data.
     */
    private final long interval;

    /**
     * The CPu where the threads are executed.
     */
    private final Cpu cpu;
    

    /***
     * Create a polling thread to track times.
     * 
     * @param collectInterval
     *            The interval to collect the data.
     * @param cpuInstance
     *            The CPU to update the data.
     */
    public ThreadTimesMonitor(final long collectInterval, Cpu cpuInstance)
    {
        super("Threads monitor");
        this.interval = collectInterval;
        this.cpu = cpuInstance;
        
        setDaemon(true);
    }

    @Override
    public void run()
    {
        while (!isInterrupted())
        {
            update();
            try
            {
                sleep(interval);
            }
            catch (InterruptedException exception)
            {
                break;
            }
        }
    }

    /**
     * Update the hash table of thread times.
     */
    private void update()
    {
        final ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        final long[] ids = bean.getAllThreadIds();

        for (long id : ids)
        {
            
            if (id == getId())
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
                info = cpu.monitor(id).getThreadInfo();
            }
            
            Times times = info.getTimes();
            
            if (times == null)
            {
                times = new Times(id, new Period(cpuTime, cpuTime), new Period(userTime, userTime));
                //info.setTimes(times);
            }
            else
            {
                times.getCpuTime().setEndTime(cpuTime);
                times.getUserTime().setEndTime(userTime);
            }
        }
    }
}
