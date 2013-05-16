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
package jenergy.agent.aop.jboss.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.jboss.aop.joinpoint.Invocation;

public final class JbossUtils
{

    /**
     * Private constructor to avoid instance of this class.
     */
    private JbossUtils()
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
     * @see ConstructorInvocation
     */
    public static <T> T newInstance(Invocation thisJoinPoint, Class<T> clazz, Object... newArgs) throws NoSuchMethodException, SecurityException,
            InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        Constructor<?> constructor;
        Object[] args;

        try
        {
            constructor = getConstructor(thisJoinPoint.getClass().getSuperclass().getSuperclass(), thisJoinPoint);
            args = getArgs(thisJoinPoint);
        }
        catch (Exception exception)
        {
            throw new RuntimeException(exception.getMessage(), exception);
        }

        Class<?>[] parameterTypes = new Class[constructor.getParameterTypes().length + (newArgs != null ? newArgs.length : 0)];
        Object[] newConstructorArgs = new Object[parameterTypes.length];

        for (int i = 0; i < constructor.getParameterTypes().length; i++)
        {
            parameterTypes[i] = constructor.getParameterTypes()[i];
            newConstructorArgs[i] = args[i];
        }

        for (int i = 0, j = newConstructorArgs.length - newArgs.length; i < newArgs.length; i++, j++)
        {
            parameterTypes[j] = newArgs[i].getClass();
            newConstructorArgs[j] = newArgs[i];
        }

        Constructor<T> newConstructor = clazz.getConstructor(parameterTypes);
        newConstructor.setAccessible(true);
        return newConstructor.newInstance(newConstructorArgs);
    }

    /**
     * Returns the reference of the {@link Method} of the advice.
     * 
     * @param clazz
     *            The class where the constructor is declared.
     * @param invocation
     *            The advice invocation.
     * @param <T>
     *            The class where the constructor was declared.
     * @return The method of the advice. It will be <code>null</code> if the joinpoint's {@link Signature} is not a {@link MethodSignature}.
     * @throws Exception
     *             If it's not possible to invoke the private method in the JVM.
     */
    @SuppressWarnings("unchecked")
    public static <T> Constructor<T> getConstructor(Class<?> clazz, Invocation invocation) throws Exception
    {
        return (Constructor<T>) getField(clazz, invocation, "constructor");
    }

    /**
     * Returns the reference of the {@link Method} of the advice.
     * 
     * @param invocation
     *            The advice invocation.
     * @param name
     *            The name of the field.
     * @param <T>
     *            The class where the constructor was declared.
     * @return The method of the advice. It will be <code>null</code> if the joinpoint's {@link Signature} is not a {@link MethodSignature}.
     * @throws Exception
     *             If it's not possible to invoke the private method in the JVM.
     */
    public static <T> T getField(Invocation invocation, String name) throws Exception
    {
        return getField(invocation.getClass(), invocation, name);
    }

    /**
     * Returns the reference of the {@link Method} of the advice.
     * 
     * @param clazz
     *            The {@link Class} where the field is declared.
     * @param invocation
     *            The advice invocation.
     * @param name
     *            The name of the field.
     * @param <T>
     *            The class where the constructor was declared.
     * @return The method of the advice. It will be <code>null</code> if the joinpoint's {@link Signature} is not a {@link MethodSignature}.
     * @throws Exception
     *             If it's not possible to invoke the private method in the JVM.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getField(Class<?> clazz, Invocation invocation, String name) throws Exception
    {
        Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);
        return (T) field.get(invocation);
    }

    /**
     * Returns the arguments of the target method of the poincut. In this case, this method consider that each method argument is a field named
     * arg[0-9]*.
     * 
     * @param invocation
     *            The reference to the poincut.
     * @return The arguments of the target method of the poincut. In this case, this method consider that each method argument is a field named
     *         arg[0-9]*.
     * @throws Exception If it's not possible to get the value of private fields in the JVM.
     */
    private static Object[] getArgs(Invocation invocation) throws Exception
    {
        List<Object> values = new ArrayList<Object>();
        Field[] fields = invocation.getClass().getSuperclass().getDeclaredFields();

        for (int i = 0; i < fields.length; i++)
        {
            if (fields[i].getName().length() > 3 && fields[i].getName().startsWith("arg") && Character.isDigit(fields[i].getName().charAt(3)))
            {
                fields[i].setAccessible(true);
                values.add(fields[i].get(invocation));
            }
        }
        return values.toArray();
    }
}
