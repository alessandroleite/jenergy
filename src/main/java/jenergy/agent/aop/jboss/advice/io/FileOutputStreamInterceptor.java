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
package jenergy.agent.aop.jboss.advice.io;

import java.io.FileOutputStream;

import jenergy.profile.data.MethodInfo;

import org.jboss.aop.Bind;
import org.jboss.aop.InterceptorDef;
import org.jboss.aop.advice.Interceptor;
import org.jboss.aop.advice.Scope;
import org.jboss.aop.joinpoint.Invocation;

@InterceptorDef(scope = Scope.PER_VM)
@Bind(pointcut = "call($instanceof{java.io.FileOutputStream}->new(..)) AND !call(jenergy.agent.*->new(..))")
public final class FileOutputStreamInterceptor implements Interceptor
{
    @Override
    public String getName()
    {
        return this.getClass().getName();
    }

    @Override
    public Object invoke(Invocation invocation) throws Throwable
    {
        Object instance = invocation.invokeNext();

        if (instance != null && FileOutputStream.class.isAssignableFrom(instance.getClass()))
        {
            MethodInfo methodInfo = null;
            //instance = new FileOutputStreamDelegate((FileOutputStream) instance, methodInfo);
        }
        return instance;
    }
}
