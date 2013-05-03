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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jenergy.profile.data.MethodInfo;

public class Methods implements Iterable<MethodInfo>
{

    /**
     * The method statistics executed in this thread.
     */
    private final Map<String, List<MethodInfo>> threadMethods = new ConcurrentHashMap<String, List<MethodInfo>>();

    /**
     * 
     */
    public Methods()
    {
    }

    @Override
    public Iterator<MethodInfo> iterator()
    {
        return null;
    }

    /**
     * Returns the duration of all methods.
     * 
     * @return The duration of the execution of all methods.
     */
    public Long methodsDuration()
    {
        long duration = 0L;

        for (List<MethodInfo> methods : this.threadMethods.values())
        {
            for (MethodInfo method : methods)
            {
                duration += method.getTimer().time();
            }
        }
        return duration;
    }
}
