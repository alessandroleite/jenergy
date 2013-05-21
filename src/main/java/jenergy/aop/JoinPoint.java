package jenergy.aop;

/**
 * AOP join point implementation agnostic.
 * 
 * An AOP implementation agnostic interface which offers the normally information about a join point such as the target object, method arguments,
 * target.
 */
public interface JoinPoint
{
    /**
     * Proceed with the next advice or target method invocation.
     * 
     * @param advice
     *            Represents the instance of the AOP advice.
     * @return The result value. This value will be returned to the advice as a result of the specific method of the AOP library.
     * @throws Throwable
     *             May throw any exceptions declared by the join point itself. If this exception is not declared and is not a runtime exception, it
     *             will be encapsulated in a {@link RuntimeException} before being thrown to the basis system.
     */
    Object proceed(Advice advice) throws Throwable;

    /**
     * Proceed with the next advice or target method invocation.
     * 
     * @param advice
     *            Represents the instance of the AOP advice.
     * @param arguments
     *            The arguments of the around advice. The number of arguments must be the same and type as the parameters of the advice.
     * @return The result value. This value will be returned to the advice as a result of the specific method of the AOP library.
     * @throws Throwable
     *             May throw any exceptions declared by the join point itself. If this exception is not declared and is not a runtime exception, it
     *             will be encapsulated in a {@link RuntimeException} before being thrown to the basis system.
     */
    Object proceed(Advice advice, Object [] arguments) throws Throwable;

    /**
     * Returns the target object. This will always be the same object as that matched by the <code>target</code> pointcut designator.
     * 
     * @return The <code>target</code> object or <code>null</code> when there is no target object.
     */
    Object getTarget();

    /**
     * Returns the arguments at this join point.
     * 
     * @return The arguments at this join point.
     */
    Object[] getArguments();

}
