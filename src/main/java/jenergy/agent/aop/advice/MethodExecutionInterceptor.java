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
package jenergy.agent.aop.advice;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.Method;

import jenergy.agent.common.Cpu;
import jenergy.profile.data.MethodInfo;
import jenergy.profile.data.Period;
import jenergy.profile.data.Times;

public abstract class MethodExecutionInterceptor
{
    /**
     * @param method
     *            Represents the join point to be intercepted.
     * @param invoker
     *            Represents the instance of the AOP advice that has the information about the joinpoint to be used in the {@link #proceed(Object)}
     *            method.
     * @return The result value. This value will be returned as result of the call to method {@link #proceed(Object)}.
     * @throws Throwable
     *             May throw any exceptions declared by the join point itself. If this exception is not declared and is not a runtime exception, it
     *             will be encapsulated in a {@link RuntimeException} before being thrown to the basis system.
     */
    public Object invokeAdvice(Method method, Object invoker) throws Throwable
    {
        Object result;

        final long tid = Thread.currentThread().getId();
        final ThreadMXBean bean = ManagementFactory.getThreadMXBean();

        MethodInfo caller = null;
        
        if (Cpu.getInstance().getThreadProfiler(tid) != null)
        {
            caller = Cpu.getInstance().getThreadProfiler(tid).peekMethodInfo();
        } 
        
        final MethodInfo called = Cpu.getInstance().monitor(method, caller);
        
        called.setTimes(new Times(tid));

        try
        {
            if (bean != null)
            {
                called.getTimes().setCpuTime(new Period(bean.getCurrentThreadCpuTime()));
                called.getTimes().setUserTime(new Period(bean.getCurrentThreadUserTime()));
            }

            if (caller != null)
            {
                caller.getTimer().suspend();
            }
            
            called.getTimer().restart();
            result = proceed(invoker);
        }
        finally
        {
            called.getTimer().stop();
            Cpu.getInstance().getThreadProfiler(tid).popStack();
            
            if (bean != null)
            {
                long cpuTime = bean.getCurrentThreadCpuTime();
                long userTime = bean.getCurrentThreadUserTime();

                called.getTimes().getCpuTime().setEndTime(cpuTime);
                called.getTimes().getUserTime().setEndTime(userTime);
            }

            if ("main".equalsIgnoreCase(method.getName()))
            {
                Cpu.getInstance().getThreadProfiler(tid).stop(called.getTimes().getUserTime().time());
                Cpu.getInstance().getThread(tid).getTimer().stop();
            }
            
            if (caller != null)
            {
                caller.getTimer().resume();
            }
        }
        return result;
    }

    /**
     * 
     * @param invoker
     *            Represents the instance of the AOP advice.
     * @return The result value. This value will be returned to the interceptor/advice as a result of the specific method of the AOP library.
     * @throws Throwable
     *             May throw any exceptions declared by the join point itself. If this exception is not declared and is not a runtime exception, it
     *             will be encapsulated in a {@link RuntimeException} before being thrown to the basis system.
     */
    protected abstract Object proceed(Object invoker) throws Throwable;
}
