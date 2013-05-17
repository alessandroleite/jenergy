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

import jenergy.agent.common.util.ClassUtils;

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

        final Constructor<?> constructor = ClassUtils.fieldValue("constructor", thisJoinPoint);
        final Object[] args = getArgs(thisJoinPoint);

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
     * Returns the arguments of the target method of the poincut. In this case, this method consider that each method argument is a field named
     * arg[0-9]*.
     * 
     * @param invocation
     *            The reference to the poincut.
     * @return The arguments of the target method of the poincut. In this case, this method consider that each method argument is a field named
     *         arg[0-9]*.
     * @throws IllegalArgumentException
     *             If the specified object is not an instance of the class or interface declaring the underlying field (or a subclass or implementor
     *             thereof).
     * @throws IllegalAccessException
     *             If this Field object is enforcing Java language access control and the underlying field is inaccessible.
     * 
     */
    private static Object[] getArgs(Invocation invocation) throws IllegalArgumentException, IllegalAccessException
    {
        final Object[] arguments = ClassUtils.fieldValue("arguments", invocation);

        if (arguments != null)
        {
            return arguments;
        }

        List<Object> values = new ArrayList<Object>();
        Field[] fields = ClassUtils.fields(invocation.getClass());

        for (int i = 0; i < fields.length; i++)
        {
            if (fields[i].getName().length() > 3 && fields[i].getName().startsWith("arg") && Character.isDigit(fields[i].getName().charAt(3)))
            {
                values.add(ClassUtils.get(fields[i], invocation));
            }
        }
        return values.toArray();
    }
}
