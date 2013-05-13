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
package jenergy.agent.aop.aspectj.advice;

import java.lang.reflect.Method;

import jenergy.agent.aop.advice.MethodExecutionInterceptor;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;

@org.aspectj.lang.annotation.Aspect
public final class AspectTrace extends MethodExecutionInterceptor
{
    /**
     * Around advice to trace every method of application except the methods of the profiler. This advice is applied at runtime.
     * 
     * @param thisJoinPoint
     *            The joint point reference.
     * @return The result value. This value will be returned as result of the call to method {@link #proceed(Object)}.
     * @throws Throwable
     *             May throw any exceptions declared by the joinpoint itself. If this exception is not declared and is not a runtime exception, it
     *             will be encapsulated in a {@link RuntimeException} before being thrown to the basis system.
     */
    @org.aspectj.lang.annotation.Around("execution(* *(..)) && !within(jenergy..*)")
    public Object invoke(final ProceedingJoinPoint thisJoinPoint) throws Throwable
    {
        Method method = this.getMethod(thisJoinPoint.getSignature());
        return this.invokeAdvice(method, thisJoinPoint);
    }

    /**
     * Returns the reference of the {@link Method} of the advice.
     * 
     * @param signature
     *            The advice signature.
     * @return The method of the advice. It's never <code>null</code>.
     * @throws Exception
     *             If it's not possible to invoke the private method in the JVM.
     */
    private Method getMethod(Signature signature) throws Exception
    {
        Method m = signature.getClass().getDeclaredMethod("getMethod");
        m.setAccessible(true);
        return (Method) m.invoke(signature);
    }

    @Override
    protected Object proceed(Object invoker) throws Throwable
    {
        return ((ProceedingJoinPoint) invoker).proceed();
    }
}
