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
package jenergy.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import jenergy.profile.ThreadProfiler;
import jenergy.profile.data.MethodStatistics;
import jenergy.profile.data.ThreadInfo;

public final class OutputFile
{
    /**
     * Private constructor to avoid instance of this class.
     */
    private OutputFile()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * 
     * @param statistics
     *            The {@link MethodStatistics} to be written in the output.
     * @param threadInfo
     *            The data about the {@link Thread} which the methods were executed.
     * @throws IOException
     *             If the output file does not exists.
     */
    public static void write(Map<String, MethodStatistics> statistics, ThreadProfiler threadInfo) throws IOException
    {
        String[] fileParts = System.getProperty("jenergy.dump.file.path").split("\\.");

        ThreadInfo info = threadInfo.getThreadInfo();

        File file = new File(String.format("%s-%d.%s", fileParts[0], info.getId(), fileParts[1]));
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

        for (MethodStatistics method : statistics.values())
        {
            writer.write(method.toString());
            writer.newLine();
        }

        writer.write("Thread info \n");
        writer.write(String.format("id:%d, cycle %dms, time:%dms, power: %s", info.getId(), Timer.nanoToMillis(info.getCpuInfo().cycleDuration()),
                info.getTimer().millis(), info.getPower().doubleValue()));

        writer.close();
    }
}
