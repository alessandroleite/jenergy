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

import java.io.FileOutputStream;
import java.lang.reflect.Constructor;

import jenergy.agent.common.Cpu;
import jenergy.agent.common.io.FileOutputStreamDelegate;
import jenergy.profile.data.MethodInfo;
import jenergy.util.Observer;
import jenergy.util.Subject;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclareParents;
import org.aspectj.lang.reflect.ConstructorSignature;

@Aspect
public class FileOutputStreamAspect
{

    /**
     * Declare the {@link FileOutputStreamDelegate} as a {@link Subject}.
     */
    @DeclareParents(value = "jenergy.agent.common.io.FileOutputStreamDelegate")
    private Subject subject;

    /**
     * Around advice to replace all instances of {@link FileOutputStream} by {@link FileOutputStreamDelegate}. This is a runtime advice.
     * 
     * @param thisJoinPoint
     *            The joint point reference.
     * @return An instance of {@link FileOutputStreamDelegate}.
     * @throws Throwable
     *             May throw any exceptions declared by the joinpoint itself. If this exception is not declared and is not a runtime exception, it
     *             will be encapsulated in a {@link RuntimeException} before being thrown to the basis system.
     */
    @Around("call(java.io.FileOutputStream+.new(..)) && !within(jenergy..*) && !within(org.aspectj..*)")
    public Object invoke(final ProceedingJoinPoint thisJoinPoint) throws Throwable
    {
        ConstructorSignature signature = (ConstructorSignature) thisJoinPoint.getSignature();

        Class<?>[] parameterTypes = new Class[signature.getParameterTypes().length + 2];
        Object[] args = new Object[parameterTypes.length];

        for (int i = 0; i < signature.getParameterTypes().length; i++)
        {
            parameterTypes[i] = signature.getParameterTypes()[i];
            args[i] = thisJoinPoint.getArgs()[i];
        }

        parameterTypes[parameterTypes.length - 2] = FileOutputStream.class;
        parameterTypes[parameterTypes.length - 1] = MethodInfo.class;

        args[args.length - 2] = thisJoinPoint.proceed();
        args[args.length - 1] = Cpu.getInstance().currentThread().peekMethodInfo();

        Constructor<FileOutputStreamDelegate> constructor = FileOutputStreamDelegate.class.getConstructor(parameterTypes);
        FileOutputStreamDelegate newInstance = constructor.newInstance(args);

        newInstance.getClass().getDeclaredMethod("attach", jenergy.util.Observer.class).invoke(newInstance, newInstance.getInfo());

        return newInstance;
    }

    // /**
    // *
    // * @param observable
    // * The reference to the observable object.
    // * @param b
    // * The bytes to be write.
    // * @param thisJoinPoint
    // * The joint point reference.
    // * @return Returns <code>null</code> since the return type of the advice method is {@link Void}.
    // * @throws Throwable
    // * May throw any exceptions declared by the joinpoint itself. If this exception is not declared and is not a runtime exception, it
    // * will be encapsulated in a {@link RuntimeException} before being thrown to the basis system.
    // */
    // @Around(argNames = "observable, b", value = "execution(void jenergy.agent.common.io.FileOutputStreamDelegate.write*(..)) && " +
    // "target(observable) && args(b)")
    // public Object aroundFileOutputStreamWriteOperation(Subject observable, int b, final ProceedingJoinPoint thisJoinPoint) throws Throwable
    // {
    // Object methodReturn = thisJoinPoint.proceed(new Integer[] {b});
    //
    // for (Observer obs : observable.getObservers())
    // {
    // obs.update(observable, b);
    // }
    //
    // return methodReturn;
    // }

    /**
     * 
     * @param thisJoinPoint
     *            The joint point reference.
     * @return Returns <code>null</code> since the return type of the advice method is {@link Void}.
     * @throws Throwable
     *             May throw any exceptions declared by the joinpoint itself. If this exception is not declared and is not a runtime exception, it
     *             will be encapsulated in a {@link RuntimeException} before being thrown to the basis system.
     */
    @Around(value = "execution(void jenergy.agent.common.io.FileOutputStreamDelegate.write(..))")
    public Object aroundFileOutputStreamWriteOperation(final ProceedingJoinPoint thisJoinPoint) throws Throwable
    {
        Object[] args = thisJoinPoint.getArgs();
        Subject observable = (Subject) thisJoinPoint.getTarget();

        Object methodReturn = thisJoinPoint.proceed(args);

        for (Observer obs : observable.getObservers())
        {
            int value;

            if (args.length == 3)
            {
                value = (Integer) args[2] - (Integer) args[1];
            }
            else
            {
                if (args[0].getClass().isArray())
                {
                    value = ((byte[]) args[0]).length * 4;
                }
                else
                {
                    value = (Integer) args[0];
                }
            }

            obs.update(observable, value);
        }
        return methodReturn;
    }
}
