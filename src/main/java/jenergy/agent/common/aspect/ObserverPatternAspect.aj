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
package jenergy.agent.common.aspect;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

import jenergy.util.Observer;
import jenergy.util.Subject;

public abstract aspect ObserverPatternAspect
{
    /**
     * Notify the observers about the new state of the subject.
     * 
     * @param observable
     *            The {@link Subject} that is observable and has a new state.
     */
    abstract pointcut notifyObservers(Subject observable);

    /**
     * This after advice notifies all registered observers about a new state of the observable ({@link Subject}) object.
     * 
     * @param observable
     *            The new state of the observable ({@link Subject}) object. It is never <code>null</code>.
     */
    Object around(Subject observable) : notifyObservers(observable)
    {
        Object advicedMethodReturn;
        
        synchronized (thisJoinPoint.getTarget()) 
        {
                advicedMethodReturn = proceed(observable);    
        }
        
        for (Observer obs : observable.getObservers()) 
        {
                obs.update(observable, advicedMethodReturn);
        }
        
        return advicedMethodReturn;
}

    // Subject inter-type declaration

    private final Collection<Observer> Subject.observers = new CopyOnWriteArrayList<Observer>();

    public void Subject.attach(Observer observer)
    {
        if (observer != null && !observers.contains(observer))
        {
            observers.add(observer);
        }
    }

    /**
     * Remove a {@link Observer}
     * 
     * @param observer
     *            The {@link Observer} to be removed
     */
    public void Subject.detach(Observer observer)
    {
        if (observer != null)
        {
            observers.remove(observer);
        }
    }

    /**
     * Returns the {@link Observer}s of a observable instance.
     * 
     * @return An unmodified {@link Collection} with the {@link Observer}s instances of this observable.
     */
    public Collection<Observer> Subject.getObservers()
    {
        return Collections.unmodifiableCollection(this.observers);
    }
}
