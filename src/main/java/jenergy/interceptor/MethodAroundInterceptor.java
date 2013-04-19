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

import jenergy.agent.ProfileManager;
import jenergy.profile.JEnergyProfiler;

import org.jboss.aop.InterceptorDef;
import org.jboss.aop.advice.Interceptor;
import org.jboss.aop.advice.Scope;
import org.jboss.aop.joinpoint.Invocation;
import org.jboss.aop.joinpoint.MethodInvocation;

@org.jboss.aop.Bind(pointcut = "execution(* *->*(..)) AND !execution(* jenergy.*->*(..)) AND !execution(* sun.*->*(..)) "
        + "AND !execution(* java.*->*(..)) AND !execution(* org.jboss.*->*(..)) AND !execution(* javassist.*->*(..))")
@InterceptorDef(scope = Scope.PER_VM)
public final class MethodAroundInterceptor implements Interceptor
{
    @Override
    public String getName()
    {
        return this.getClass().getName();
    }

    @Override
    public Object invoke(Invocation invocation) throws Throwable
    {
        Object returnValue = null;
        JEnergyProfiler prof = null;

        try
        {
            if (invocation instanceof MethodInvocation)
            {
                MethodInvocation mi = (MethodInvocation) invocation;
                prof = new JEnergyProfiler(mi.getMethod());
                ProfileManager.monitor(prof);

                returnValue = invocation.invokeNext();
            }
            else
            {
                returnValue = invocation.invokeNext();
            }
            return returnValue;

        }
        finally
        {
            if (prof != null)
            {
                long end = System.currentTimeMillis();
                prof.endMethodExecution(end);
            }
        }
    }
}
