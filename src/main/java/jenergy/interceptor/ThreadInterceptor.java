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
package jenergy.interceptor;

import jenergy.agent.Cpu;
import jenergy.profile.ThreadProfiler;

import org.jboss.aop.Bind;
import org.jboss.aop.InterceptorDef;
import org.jboss.aop.advice.Interceptor;
import org.jboss.aop.advice.Scope;
import org.jboss.aop.joinpoint.Invocation;

/**
 * This advice ({@link Interceptor}) intercepts the execution of the method <strong>run</strong> of every object that implements {@link Thread} or
 * {@link Runnable}. This is useful to collect the thread informations such as: the cpu and user time; the blocked count and time; and the whole
 * execution time.
 * 
 * References about pointcut definition in Jboss AOP:
 * http://docs.jboss.org/jbossaop/docs/2.0.0.GA/docs/aspect-framework/reference/en/html/pointcuts.html
 */
@Bind(pointcut = "execution(* $instanceof{java.lang.Thread}->run(..)) or execution(* $instanceof{java.lang.Runnable}->run(..)) " +
         " and !execution(* jenergy.*->*(..))")
@InterceptorDef(scope = Scope.PER_VM)
public class ThreadInterceptor implements Interceptor
{
    @Override
    public String getName()
    {
        return this.getClass().getName();
    }

    @Override
    public Object invoke(Invocation invocation) throws Throwable
    {
        final ThreadProfiler profiler = Cpu.getInstance().monitor(Thread.currentThread());
        Object result;
        try
        {
            profiler.getThreadInfo().getCpuInfo().updateCycleDuration();
            result = invocation.invokeNext();
        }
        finally
        {
            profiler.getThreadInfo().getCpuInfo().updateCycleDuration();
            profiler.stop();
        }

        return result;
    }
}
