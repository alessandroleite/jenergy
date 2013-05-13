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

import java.io.InputStream;

import org.jboss.aop.InterceptorDef;
import org.jboss.aop.advice.Interceptor;
import org.jboss.aop.advice.Scope;
import org.jboss.aop.joinpoint.Invocation;

//execution(* $instanceof{java.io.Reader}->read*(..)) AND !execution(* jenergy.*->*(..))
@org.jboss.aop.Bind(pointcut = "call(java.io.InputStream->new(..)) AND !execution(* jenergy.*->*(..))")
@InterceptorDef(scope = Scope.PER_VM)
public class InputStreamInterceptor implements Interceptor
{
    @Override
    public String getName()
    {
        return this.getClass().getName();
    }

    @Override
    public Object invoke(Invocation invocation) throws Throwable
    {
        Object result = invocation.invokeNext();

        if (result != null && InputStream.class.isAssignableFrom(result.getClass()))
        {
            result = new InputStreamDelegate((InputStream) result);
        }
        return result;
    }
}
