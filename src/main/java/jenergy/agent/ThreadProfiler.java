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

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import jenergy.profile.ProfileInfo;

public class ThreadProfiler implements Runnable
{
    private volatile boolean terminateRequest;
    private final long profileInterval = 200;
    private final int maxDepth = 5;

    private final Map<StackTraceElement, ProfileInfo> lineCounts = new ConcurrentHashMap<StackTraceElement, ProfileInfo>();

    private final AtomicInteger lock = new AtomicInteger(0);

    public ThreadProfiler()
    {
    }

    @Override
    public void run()
    {
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();

        try
        {
            while (!terminateRequest)
            {
                long[] threadIds = threadBean.getAllThreadIds();
                ThreadInfo[] infs = threadBean.getThreadInfo(threadIds, maxDepth);

                while (!lock.compareAndSet(0, 1))
                {
                    Thread.sleep(1);
                }

                try
                {

                    for (ThreadInfo inf : infs)
                    {
                        StackTraceElement[] str = inf.getStackTrace();

                        if (str == null)
                        {
                            continue;
                        }

                        StackTraceElement el = mostInterestingElement(str);
                        if (el != null)
                        {
                            incrementCountOf(el, inf, threadBean);
                        }
                    }
                }
                finally
                {
                    lock.set(0);
                }

                Thread.sleep(profileInterval);
            }
        }
        catch (InterruptedException iex)
        {
            Thread.interrupted();
            throw new RuntimeException("Profiler interrupted");
        }
    }

    private StackTraceElement mostInterestingElement(StackTraceElement[] st)
    {
        for (int n = st.length, i = 0; i < n; i++)
        {
            StackTraceElement el = st[i];

            if (el.getLineNumber() >= 0)
            {
                return el;
            }
        }
        return null;
    }

    private void incrementCountOf(StackTraceElement el, ThreadInfo inf, ThreadMXBean threadBean)
    {
        ProfileInfo profile = lineCounts.get(el);

        if (profile == null)
        {
            lineCounts.put(el, profile = new ProfileInfo());
        }

        profile.incrementTickCount();
        profile.setCpuTime(threadBean.getThreadCpuTime(inf.getThreadId()));
        profile.setUserTime(threadBean.getThreadUserTime(inf.getThreadId()));
    }

    public List<LineAndCount> getProfile()
    {
        while (!lock.compareAndSet(0, 1))
        {
            try
            {
                Thread.sleep(1);
            }
            catch (InterruptedException iex)
            {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Unexpected interruption waiting for lock");
            }
        }

        List<LineAndCount> res;

        try
        {
            res = new ArrayList<LineAndCount>(lineCounts.size());
            for (Map.Entry<StackTraceElement, ProfileInfo> entry : lineCounts.entrySet())
            {
                if (include(entry.getKey()))
                {
                    res.add(new LineAndCount(entry.getKey(), entry.getValue()));
                }
            }
            // lineCounts.clear();
        }
        finally
        {
            lock.set(0);
        }

        Collections.sort(res);
        return res;
    }

    private boolean include(StackTraceElement st)
    {
        String clazz = st.getClassName();
        return !clazz.startsWith("java") && !clazz.startsWith("jenergy") && !clazz.startsWith("sun");
    }

    public static class LineAndCount implements Comparable<LineAndCount>
    {
        private final String className, methodName;
        private final long lineNo, tickCount, cpuTime, userTime;

        private LineAndCount(final StackTraceElement lineInfo, final ProfileInfo count)
        {
            this.className = lineInfo.getClassName();
            this.methodName = lineInfo.getMethodName();
            this.lineNo = lineInfo.getLineNumber();
            this.tickCount = count.getTickCount();

            this.cpuTime = count.getCpuTime();
            this.userTime = count.getUserTime();
        }

        public String getClassName()
        {
            return className;
        }

        public int compareTo(final LineAndCount other)
        {
            return (tickCount < other.tickCount) ? -1 : ((tickCount > other.tickCount) ? 1 : 0);
        }

        public String toString()
        {
            return String.format("%5d %s.%s:%d %d %d", tickCount, className, methodName, lineNo, cpuTime, userTime);
        }
    }
}
