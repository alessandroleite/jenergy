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
 */
package jenergy.profile.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Stack;

public final class TimeHistory implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 6001757484059557041L;

    /**
     * The thread time histories.
     */
    private final Stack<Long> histories = new Stack<Long>();

    /**
     * The thread id of these times.
     */
    private final long threadId;

    /**
     * 
     * @param tid
     *            The thread id.
     */
    public TimeHistory(long tid)
    {
        this.threadId = tid;
    }

    /**
     * Add a new time history.
     * 
     * @param value
     *            The last time read to be add.
     */
    public void add(long value)
    {
        histories.push(value);
    }

    /**
     * Returns <code>true</code> if this history is empty of <code>false</code> otherwise.
     * 
     * @return <code>true</code> if this history is empty of <code>false</code> otherwise.
     */
    public boolean isEmpty()
    {
        return this.histories.isEmpty();
    }

    /**
     * @return the histories
     */
    public Collection<Long> getHistories()
    {
        return Collections.unmodifiableCollection(histories);
    }

    /**
     * @return the threadId
     */
    public long getThreadId()
    {
        return threadId;
    }
}
