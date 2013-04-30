/**
 * Copyright 2013 Alessandro
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
package jenergy.interceptor;

import jenergy.agent.Cpu;
import jenergy.profile.MethodProfiler;

import org.jboss.aop.InterceptorDef;
import org.jboss.aop.advice.Interceptor;
import org.jboss.aop.advice.Scope;
import org.jboss.aop.joinpoint.Invocation;
import org.jboss.aop.joinpoint.MethodInvocation;

// FIXME The pointcut can be defined by the user at runtime?

@org.jboss.aop.Bind(pointcut = "execution(* *->*(..)) AND !execution(* jenergy.*->*(..)) AND !execution(* sun.*->*(..)) " +
         "AND !execution(* java.*->*(..)) AND !execution(* org.jboss.*->*(..)) AND !execution(* javassist.*->*(..)) " +
         "AND !execution(* org.apache.*->*(..))")
@InterceptorDef(scope = Scope.PER_VM)
public final class MethodInterceptor implements Interceptor
{
    @Override
    public String getName()
    {
        return this.getClass().getName();
    }

    @Override
    public Object invoke(Invocation invocation) throws Throwable
    {
        Object result;

        if (!(invocation instanceof MethodInvocation))
        {
            throw new IllegalStateException();
        }

        MethodProfiler profile = Cpu.getInstance().monitor(((MethodInvocation) invocation).getMethod());
        // CpuInfo cpuInfo = profile.getInfo().getCpuInfo();

        try
        {
            // cpuInfo.updateCycleDuration();
            result = invocation.invokeNext();
        }
        finally
        {
            profile.stop();
            if ("main".equalsIgnoreCase(((MethodInvocation) invocation).getMethod().getName()))
            {
                Cpu.getInstance().getThread(Thread.currentThread().getId()).getTimer().stop();
                System.out.println("--- main ---");
            }
        }

        return result;
    }
}
