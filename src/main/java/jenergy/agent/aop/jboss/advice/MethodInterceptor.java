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
package jenergy.agent.aop.jboss.advice;

import jenergy.agent.aop.advice.MethodExecutionInterceptor;

import org.jboss.aop.InterceptorDef;
import org.jboss.aop.advice.Interceptor;
import org.jboss.aop.advice.Scope;
import org.jboss.aop.joinpoint.Invocation;
import org.jboss.aop.joinpoint.MethodInvocation;

//!execution(* jenergy.agent.aop.advice.*->*(..))
@org.jboss.aop.Bind(pointcut = "execution(* *->*(..)) AND !execution(* jenergy.*->*(..)) AND !execution(* sun.*->*(..)) " +
         "AND !execution(* java.*->*(..)) AND !execution(* org.jboss.*->*(..)) AND !execution(* javassist.*->*(..)) " +
         "AND !execution(* org.apache.*->*(..))")
@InterceptorDef(scope = Scope.PER_VM)
public final class MethodInterceptor extends MethodExecutionInterceptor implements Interceptor
{
    @Override
    public String getName()
    {
        return this.getClass().getName();
    }

    @Override
    public Object invoke(Invocation invocation) throws Throwable
    {
        return this.invokeAdvice(((MethodInvocation) invocation).getMethod(), invocation);
    }

    @Override
    protected Object proceed(Object invoker) throws Throwable
    {
        return ((MethodInvocation) invoker).invokeNext();
    }
}
