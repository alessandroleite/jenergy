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

import jenergy.agent.common.io.FileInputStreamDelegate;
import jenergy.util.Subject;

public aspect FileInputStreamDelegateAspect extends ObserverPatternAspect
{
    declare parents: FileInputStreamDelegate implements Subject;

    /**
     * This pointcut intercepts the execution of the methods read of the class {@link FileInputStreamDelegate}.
     */
    public pointcut notifyObservers(Subject observable):  
        target (observable)  && (execution (int FileInputStreamDelegate.read(..)));

    after() returning(FileInputStreamDelegate input): call(FileInputStreamDelegate.new())
    {
        try
        {
            input.getClass().getMethod("attach", jenergy.util.Observer.class).invoke(input, input.getInfo());
        }
        catch (Exception exception)
        {
            throw new RuntimeException(exception);
        }
    }
}
