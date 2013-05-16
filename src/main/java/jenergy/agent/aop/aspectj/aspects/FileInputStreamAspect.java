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
package jenergy.agent.aop.aspectj.aspects;

import jenergy.agent.aop.aspectj.util.AspectjUtils;
import jenergy.agent.common.Cpu;
import jenergy.agent.common.io.FileInputStreamDelegate;
import jenergy.profile.data.MethodInfo;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class FileInputStreamAspect
{
    /**
     * Around advice to replace all instances of {@link java.io.FileInputStream} by {@link FileInputStreamDelegate}. This is a runtime advice.
     * 
     * @param thisJoinPoint
     *            The joinpoint reference.
     * @return An instance of {@link FileInputStreamDelegate}.
     * @throws Throwable
     *             May throw any exceptions declared by the joinpoint itself. If this exception is not declared and is not a runtime exception, it
     *             will be encapsulated in a {@link RuntimeException} before being thrown to the basis system.
     */
    @Around("call(java.io.FileInputStream+.new(..)) && !within(jenergy..*)")
    public Object invoke(final ProceedingJoinPoint thisJoinPoint) throws Throwable
    {
        MethodInfo method = Cpu.getInstance().currentThread().peekMethodInfo();
        FileInputStreamDelegate instance = AspectjUtils.newInstance(thisJoinPoint, FileInputStreamDelegate.class, thisJoinPoint.proceed(), method);
        instance.getClass().getDeclaredMethod("attach", jenergy.agent.common.util.Observer.class).invoke(instance, instance.getInfo());
        return instance;
    }

}
