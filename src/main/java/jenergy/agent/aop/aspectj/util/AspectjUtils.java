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
package jenergy.agent.aop.aspectj.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.MethodSignature;

public final class AspectjUtils
{

    /**
     * Private constructor to avoid instance of this utility class.
     */
    private AspectjUtils()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * 
     * @param thisJoinPoint
     *            The reference to the constructor joinpoint.
     * @param clazz
     *            The class to invoke the same constructor of the joinpoint.
     * @param newArgs
     *            The arguments to be passed to the constructor call. In this case, the constructor arguments will be: the arguments of the original
     *            constructor defined by the joinpoint, and the newArgs.
     * @param <T>
     *            The type returned by the constructor call.
     * @return A new object created by calling the constructor of the given class.
     * @throws NoSuchMethodException
     *             If a matching method is not found.
     * @throws SecurityException
     *             If a security manager, <em>s</em>, is present and any of the following conditions is met:
     *             <ul>
     *             <li>invocation of s.checkMemberAccess(this, Member.PUBLIC) denies access to the constructor</li>
     *             <li>the caller's class loader is not the same as or an ancestor of the class loader for the current class and invocation of
     *             s.checkPackageAccess() denies access to the package of this class.</li>
     *             </ul>
     * @throws InstantiationException
     *             If the class that declares the underlying constructor represents an abstract class.
     * @throws IllegalAccessException
     *             If the Constructor object is enforcing Java language access control and the underlying constructor is inaccessible.
     * @throws IllegalArgumentException
     *             If the number of actual and formal parameters differ; if an unwrapping conversion for primitive arguments fails; or if, after
     *             possible unwrapping, a parameter value cannot be converted to the corresponding formal parameter type by a method invocation
     *             conversion; if this constructor pertains to an {@link Enum} type.
     * @throws InvocationTargetException
     *             If the underlying constructor throws an exception.
     * @throws ClassCastException
     *             If it is not a constructor joinpoint.
     * @see ConstructorSignature
     */
    public static <T> T newInstance(ProceedingJoinPoint thisJoinPoint, Class<T> clazz, Object... newArgs) throws NoSuchMethodException,
            SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        ConstructorSignature signature = (ConstructorSignature) thisJoinPoint.getSignature();

        Class<?>[] parameterTypes = new Class[signature.getParameterTypes().length + (newArgs != null ? newArgs.length : 0)];
        Object[] newConstructorArgs = new Object[parameterTypes.length];

        for (int i = 0; i < signature.getParameterTypes().length; i++)
        {
            parameterTypes[i] = signature.getParameterTypes()[i];
            newConstructorArgs[i] = thisJoinPoint.getArgs()[i];
        }

        for (int i = 0, j = newConstructorArgs.length - newArgs.length; i < newArgs.length; i++, j++)
        {
            parameterTypes[j] = newArgs[i].getClass();
            newConstructorArgs[j] = newArgs[i];
        }

        Constructor<T> constructor = clazz.getConstructor(parameterTypes);
        constructor.setAccessible(true);
        return constructor.newInstance(newConstructorArgs);
    }

    /**
     * Returns the reference of the {@link Method} of the advice.
     * 
     * @param signature
     *            The advice signature.
     * @return The method of the advice. It will be <code>null</code> if the joinpoint's {@link Signature} is not a {@link MethodSignature}.
     * @throws Exception
     *             If it's not possible to invoke the private method in the JVM.
     */
    public static Method getMethod(Signature signature) throws Exception
    {
        if (signature instanceof MethodSignature)
        {
            Method m = signature.getClass().getDeclaredMethod("getMethod");
            m.setAccessible(true);
            return (Method) m.invoke(signature);
        }
        return null;
    }

    /**
     * Returns the reference of the {@link Method} of the advice.
     * 
     * @param joinPoint
     *            The joinpoint reference.
     * @return The method of the advice. It's never <code>null</code>.
     * @throws Exception
     *             If it's not possible to invoke the private method in the JVM.
     */
    public static Method getMethod(ProceedingJoinPoint joinPoint) throws Exception
    {
        return getMethod(joinPoint.getSignature());
    }

    /**
     * Returns the first method of the stack that is neither a method of this profiler, nor a method of the instrumentation strategy, and nor in the
     * given exclude type.
     * 
     * @param joinPoint
     *            The joinpoint reference.
     * @param excludes
     *            The set with the class that be excluded from the analyze.
     * @return The name of the caller method.
     * @throws Exception
     *             If the method does not exists.
     */
    // public static Method caller(ProceedingJoinPoint joinPoint, Class<?>... excludes) throws Exception
    // {
    // StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
    //
    // for (int i = 2; i < stackTrace.length; i++)
    // {
    // if (!isMethodOfThisProfile(stackTrace[i], excludes))
    // {
    // //Class.forName(stackTrace[i].getClassName()).getDeclaredMethods()
    // return getMethod(joinPoint, stackTrace[i]);
    // }
    // }
    //
    // return null;
    // }

    // private static Method getMethod(ProceedingJoinPoint joinPoint, StackTraceElement element) throws ClassNotFoundException
    // {
    // Class<?> clazz = Class.forName(element.getClassName());
    //
    // ManagementFactory.getThreadMXBean().getThreadInfo(1).getStackTrace();
    // Signature s = null;
    // Method method = clazz.getDeclaredMethod(element.getMethodName(), element.get);
    //
    // return null;
    // }

    // /**
    // *
    // * @param element
    // * The reference to the {@link StackTraceElement} to be analyzed.
    // * @param excludes
    // * The set with the class that be excluded from the analyze.
    // * @return <code>true</code> if the given {@link StackTraceElement} is not a method of this profiler or a method of the instrumentation
    // strategy.
    // */
    // private static boolean isMethodOfThisProfile(StackTraceElement element, Class<?>... excludes)
    // {
    // boolean result = false;
    //
    // if (element.getClassName().indexOf(AspectjUtils.class.getPackage().getName().split("\\.")[0]) >= 0)
    // {
    // result = true;
    // }
    // else if (element.getClassName().indexOf("org.aspectj") >= 0 || element.getClassName().indexOf("$Ajc") >= 0
    // || element.getClassName().indexOf("ajc") >= 0 || element.getMethodName().indexOf("Closure") >= 0
    // || element.getMethodName().indexOf("around") >= 0)
    // {
    // result = true;
    // }
    //
    // if (!result && excludes != null)
    // {
    // for (int i = 0; i < excludes.length; i++)
    // {
    // if (element.getClassName().indexOf(excludes[i].getName()) >= 0)
    // {
    // result = true;
    // break;
    // }
    // }
    // }
    // return result;
    // }
}
