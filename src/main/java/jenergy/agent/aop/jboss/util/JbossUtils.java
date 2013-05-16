package jenergy.agent.aop.jboss.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.jboss.aop.joinpoint.ConstructorInvocation;
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
    public static <T> T newInstance(Invocation thisJoinPoint, Class<T> clazz, Object... newArgs) throws NoSuchMethodException,
            SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        ConstructorInvocation invoker = (ConstructorInvocation) thisJoinPoint;

        Class<?>[] parameterTypes = new Class[invoker.getConstructor().getParameterTypes().length + (newArgs != null ? newArgs.length : 0)];
        Object[] newConstructorArgs = new Object[parameterTypes.length];

        for (int i = 0; i < invoker.getConstructor().getParameterTypes().length; i++)
        {
            parameterTypes[i] = invoker.getConstructor().getParameterTypes()[i];
            newConstructorArgs[i] = invoker.getArguments()[i];
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

}
