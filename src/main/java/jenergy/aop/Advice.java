package jenergy.aop;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public interface Advice
{
    /**
     * @return The instance of the advice {@link Method} or <code>null</code> if this advice is not applied in a {@link Method}.
     */
    Method getTargetMethod();

    /**
     * @param <T>
     *            The type instantiated by the constructor.
     * @return The reference to the advice constructor or <code>null</code> if it's not a constructor advice.
     */
    <T> Constructor<T> getTargetConstructor();
}
