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

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import jenergy.profile.JEnergyProfiler;
import jenergy.profile.ProfileInfo;
import jenergy.utils.Threads;

public final class ProfileManager
{
    
    /**
     * The thread pool. The number of thread is equals the number of available processors.
     */
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    /**
     * The job(s) to be executed. One job is method to be monitored and it's queued by the method {@link #monitor(JEnergyProfiler)} and dequeue by the
     * method {@link #profile()}
     */
    private static final BlockingDeque<JEnergyProfiler> JOBS = new LinkedBlockingDeque<JEnergyProfiler>();

    
    /**
     * Private constructor to avoid instances of this class.
     */
    private ProfileManager()
    {
        throw new UnsupportedOperationException();
    }

    public static void start()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (true)
                {
                    profile();
                }
            }
        }).start();
    }

    // private static void profile()
    // {
    // ThreadProfiler profiler = new ThreadProfiler();
    // new Thread(profiler).start();
    //
    // while(true)
    // {
    // try
    // {
    // Thread.sleep(1000);
    //
    // } catch (InterruptedException e)
    // {
    // e.printStackTrace();
    // }
    //
    // StringBuilder sb = new StringBuilder();
    //
    // List<ThreadProfiler.LineAndCount> counts = profiler.getProfile();
    //
    // //int sz = counts.size();
    // //int noCounts = Math.min(40, sz);
    //
    // for (int i = 0; i < counts.size(); i++)
    // {
    // ThreadProfiler.LineAndCount lc = counts.get(0);
    // sb.append(lc.toString()).append("\n");
    // //System.out.println(lc.toString());
    // }
    //
    // appendToFile(sb.toString(), "/home/alessandro/workspace/jEnergy/dist/dump.txt");
    // }
    //
    // }

    // private static void appendToFile(String data, String path)
    // {
    // try
    // {
    // BufferedWriter out = new BufferedWriter(new FileWriter(path, true));
    // out.write(data);
    //
    // out.close();
    // } catch (IOException e)
    // {
    // e.printStackTrace();
    // }
    // }


    /**
     * Starts the profile of a given method.
     * 
     * @param prof
     *            The method to be monitored. Might not be <code>null</code>.
     */
    public static void monitor(JEnergyProfiler prof)
    {
        JOBS.add(prof);
        EXECUTOR.execute(prof);
    }

    /**
     * Dequeue the finished jobs.
     */
    public static void profile()
    {
        while (!JOBS.isEmpty())
        {
            JEnergyProfiler prof = JOBS.peek();
            if (prof.isFinished())
            {
                JOBS.remove(prof);
                ProfileInfo info = prof.getInfo();
                System.out.println(info);
            }
            Threads.sleep(1);
        }
    }
}
