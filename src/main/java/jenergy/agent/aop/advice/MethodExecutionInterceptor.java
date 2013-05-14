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
import java.math.BigDecimal;
import java.util.Map;

import jenergy.agent.common.Cpu;
import jenergy.profile.data.MethodInfo;
import jenergy.profile.data.MethodStatistics;
import jenergy.profile.data.Period;
import jenergy.profile.data.Times;
import jenergy.utils.OutputFile;

public abstract class MethodExecutionInterceptor
{
    /**
     * @param method
     *            Represents the joinpoint to be intercepted.
     * @param invoker
     *            Represents the instance of the AOP advice that has the information about the joinpoint to be used in the {@link #proceed(Object)}
     *            method.
     * @return The result value. This value will be returned as result of the call to method {@link #proceed(Object)}.
     * @throws Throwable
     *             May throw any exceptions declared by the joinpoint itself. If this exception is not declared and is not a runtime exception, it
     *             will be encapsulated in a {@link RuntimeException} before being thrown to the basis system.
     */
    public Object invokeAdvice(Method method, Object invoker) throws Throwable
    {
        Object result;

        final long tid = Thread.currentThread().getId();
        final ThreadMXBean bean = ManagementFactory.getThreadMXBean();

        final MethodInfo methodCalled = Cpu.getInstance().monitor(method);
        final MethodInfo caller = Cpu.getInstance().getThreadProfiler(tid).getCallerOf(methodCalled);
        
        methodCalled.setTimes(new Times(tid));

        try
        {
            if (bean != null)
            {
                methodCalled.getTimes().setCpuTime(new Period(bean.getCurrentThreadCpuTime()));
                methodCalled.getTimes().setUserTime(new Period(bean.getCurrentThreadUserTime()));
            }

            methodCalled.getTimer().restart();
            //caller.getTimer().suspend();
            
            result = proceed(invoker);
        }
        finally
        {
            methodCalled.getTimer().stop();
            if (bean != null)
            {
                long cpuTime = bean.getCurrentThreadCpuTime();
                long userTime = bean.getCurrentThreadUserTime();

                methodCalled.getTimes().getCpuTime().setEndTime(cpuTime);
                methodCalled.getTimes().getUserTime().setEndTime(userTime);
            }

            if ("main".equalsIgnoreCase(method.getName()))
            {
                Cpu.getInstance().getThreadProfiler(tid).stop();
                Cpu.getInstance().getThread(tid).getTimer().stop();

                double power = Cpu.getInstance().getThreadProfiler(tid).computeThreadPowerConsumption(methodCalled.getTimes().getUserTime().time());
                Cpu.getInstance().getThreadProfiler(tid).getThreadInfo().setPower(BigDecimal.valueOf(power));

                Map<String, MethodStatistics> methodsStatistics = Cpu.getInstance().getThreadProfiler(tid)
                        .computeCpuPowerConsumptionOfThreadMethods();

                OutputFile.write(methodsStatistics, Cpu.getInstance().getThreadProfiler(tid));

                System.out.println("--- main ---");
            }
            
            //caller.getTimer().resume();
        }

        return result;
    }

    /**
     * 
     * @param invoker
     *            Represents the instance of the AOP advice.
     * @return The result value. This value will be returned to the interceptor/advice as a result of the specific method of the AOP library.
     * @throws Throwable
     *             May throw any exceptions declared by the joinpoint itself. If this exception is not declared and is not a runtime exception, it
     *             will be encapsulated in a {@link RuntimeException} before being thrown to the basis system.
     */
    protected abstract Object proceed(Object invoker) throws Throwable;
}
