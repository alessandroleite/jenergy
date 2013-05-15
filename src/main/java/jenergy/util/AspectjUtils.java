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
package jenergy.util;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
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
     * Returns the reference of the {@link Method} of the advice.
     * 
     * @param signature
     *            The advice signature.
     * @return The method of the advice. It's never <code>null</code>.
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
     *  @param joinPoint
     *            The joinpoint reference.
     * @param excludes
     *            The set with the class that be excluded from the analyze.
     * @return The name of the caller method.
     * @throws Exception
     *             If the method does not exists.
     */
//    public static Method caller(ProceedingJoinPoint joinPoint, Class<?>... excludes) throws Exception
//    {
//        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
//
//        for (int i = 2; i < stackTrace.length; i++)
//        {
//            if (!isMethodOfThisProfile(stackTrace[i], excludes))
//            {
//                //Class.forName(stackTrace[i].getClassName()).getDeclaredMethods()
//                return getMethod(joinPoint, stackTrace[i]); 
//            }
//        }
//
//        return null;
//    }

//    private static Method getMethod(ProceedingJoinPoint joinPoint, StackTraceElement element) throws ClassNotFoundException
//    {
//        Class<?> clazz = Class.forName(element.getClassName());
//        
//        ManagementFactory.getThreadMXBean().getThreadInfo(1).getStackTrace();
//        Signature s = null;
//        Method method = clazz.getDeclaredMethod(element.getMethodName(), element.get);
//        
//        return null;
//    }

    /**
     * 
     * @param element
     *            The reference to the {@link StackTraceElement} to be analyzed.
     * @param excludes
     *            The set with the class that be excluded from the analyze.
     * @return <code>true</code> if the given {@link StackTraceElement} is not a method of this profiler or a method of the instrumentation strategy.
     */
    private static boolean isMethodOfThisProfile(StackTraceElement element, Class<?>... excludes)
    {
        boolean result = false;

        if (element.getClassName().indexOf(AspectjUtils.class.getPackage().getName().split("\\.")[0]) >= 0)
        {
            result = true;
        }
        else if (element.getClassName().indexOf("org.aspectj") >= 0 || element.getClassName().indexOf("$Ajc") >= 0 ||
                 element.getClassName().indexOf("ajc") >= 0 || element.getMethodName().indexOf("Closure") >= 0 || 
                 element.getMethodName().indexOf("around") >= 0)
        {
            result = true; 
        }

        if (!result && excludes != null)
        {
            for (int i = 0; i < excludes.length; i++)
            {
                if (element.getClassName().indexOf(excludes[i].getName()) >= 0)
                {
                    result = true;
                    break;
                }
            }
        }

        return result;
    }
}
