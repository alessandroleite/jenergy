package jenergy.common.monitor;

import jenergy.aop.Advice;

public interface FailureDetectionStrategy
{
    /**
     * Returns <code>true</code> if the given exception is a failure execution.
     * 
     * @param t      The exception to be checked.
     * @param advice The reference to the advice.
     * @return       Returns <code>true</code> if the given exception is a failure execution.
     */
    boolean isFailure(Throwable t, Advice advice);
}
