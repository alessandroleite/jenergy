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
package jenergy.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jenergy.profile.ThreadProfiler;

public class ThreadProfilers implements Iterable<ThreadProfiler>
{
    /**
     * The {@link Map} delegate with the thread profilers.
     */
    private final Map<Long, ThreadProfiler> delegate = new ConcurrentHashMap<Long, ThreadProfiler>();

    /**
     * Creates an instance of this {@link ThreadProfilers}.
     */
    public ThreadProfilers()
    {
    }

    /**
     * Returns the {@link ThreadProfiler} of the given thread id.
     * 
     * @param tid
     *            The thread id. Might not be <code>null</code> of zero.
     * @return The {@link ThreadProfiler} of the given {@link Thread} or <code>null</code> if this {@link Map} does not contain the given thread.
     */
    public ThreadProfiler get(Long tid)
    {
        return this.delegate.get(tid);
    }

    /**
     * Insert the given thread associate it with the key in this {@link Map}. If the map previously contained a mapping for the key, the old value is
     * replaced by the specified value.
     * 
     * @param id
     *            The id of the thread. Might not be <code>null</code> and id.equals(thread.getThreadInfo().getId()) might be <code>true</code>.
     * @param thread
     *            The {@link ThreadProfiler} to be insert. Might not be <code>null</code>.
     * @return the previous value associated with id, or <code>null</code> if there was no mapping for id.
     */
    public ThreadProfiler put(Long id, ThreadProfiler thread)
    {
        if (id == null || thread == null)
        {
            throw new NullPointerException("The id or thread might not be null!");
        }

        if (thread.getThreadInfo().getId() != id.longValue())
        {
            throw new IllegalArgumentException(String.format("The key %s is different of the id (%s) of the given thread", id, thread.getThreadInfo()
                    .getId()));
        }

        return this.delegate.put(id, thread);
    }

    /**
     * Returns a read-only {@link Collection} with the {@link ThreadProfiler}s.
     * 
     * @return A read-only {@link Collection} with the {@link ThreadProfiler}s.
     */
    public Collection<ThreadProfiler> values()
    {
        return Collections.unmodifiableCollection(delegate.values());
    }

    @Override
    public Iterator<ThreadProfiler> iterator()
    {
        return values().iterator();
    }

    /**
     * Returns <code>true</code> if there is {@link ThreadProfiler} active, otherwise returns <code>false</code>.
     * 
     * @return <code>true</code> if there is {@link ThreadProfiler} active, otherwise returns <code>false</code>.
     */
    public boolean isActive()
    {
        for (ThreadProfiler thread : this.delegate.values())
        {
            if (thread.isActive())
            {
                return true;
            }
        }
        return false;
    }
}
