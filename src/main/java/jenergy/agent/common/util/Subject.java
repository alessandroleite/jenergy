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
package jenergy.agent.common.util;

import java.util.Collection;

public interface Subject
{

    /**
     * Add a new {@link Observer} to this observable ({@link Subject}) that will be notified when the state of the observable object has been changed.
     * 
     * @param obs
     *            The {@link Observable} to be add. Might not be <code>null</code>.
     */
    void attach(Observer obs);

    /**
     * Remove a given {@link Observer} of this {@link Subject}.
     * 
     * @param obs
     *            The {@link Observer} to be removed.
     */
    void detach(Observer obs);

    /**
     * Returns a read-only {@link Collection} with the {@link Observer}s of the {@link Subject}.
     * 
     * @return A read-only {@link Collection} with the {@link Observer}s of the {@link Subject}.
     */
    Collection<Observer> getObservers();
}
