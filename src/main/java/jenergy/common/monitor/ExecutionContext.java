package jenergy.common.monitor;

import java.io.Serializable;

import jenergy.agent.common.util.time.Timer;
import jenergy.common.track.PerfStats;

/**
 * Worker object that holds context information for a monitored execution.
 */
public abstract class ExecutionContext implements Serializable
{
    /**
     * The reference to the parent {@link ExecutionContext}, if available.
     */
    private ExecutionContext parent;

    /**
     * Error context, if any error (e.g., an unrecoverable exception) was encountered.
     */
    private Object errorContext;

    /**
     * The time recording.
     */
    private Timer timer;

    /**
     * The execution statistics of the execution.
     */
    private PerfStats stats;

    /**
     * Returns a reference to the {@link PerfStats} associated with the context.
     * 
     * @return A reference to the {@link PerfStats} associated with the context.
     */
    public abstract PerfStats lookupStats();

    /**
     * Executes the monitored method and returns its value.
     * 
     * @param <T>
     *            The type of the returned value.
     * @return The return of the target method or <code>null</code> if it was {@link Void}.
     */
    protected abstract <T> T invoke();

    /**
     * Record execution and elapsed time for each monitored method. Relies on {@link #proceed()} to proceed with original execution.
     * @param <T>
     *            The type of the returned value.
     * @return The return of {@link #proceed()}
     */
    public final <T> T execute()
    {
        timer.start();
        T result = invoke();
        timer.stop();

        PerfStats executionStats = getStats();

        if (executionStats != null)
        {
            if (getErrorContext() == null)
            {
                executionStats.recordExecution(timer);
            }
            else
            {
                executionStats.recordFailure(timer, getErrorContext());
            }
        }
        return result;
    }

    /**
     * @return the parent
     */
    public ExecutionContext getParent()
    {
        return parent;
    }

    /**
     * @param theParent
     *            the parent to set
     */
    public void setParent(ExecutionContext theParent)
    {
        this.parent = theParent;
    }

    /**
     * @return the errorContext
     */
    public Object getErrorContext()
    {
        return errorContext;
    }

    /**
     * @param newErrorContext
     *            the errorContext to set
     */
    public void setErrorContext(Object newErrorContext)
    {
        this.errorContext = newErrorContext;
    }

    /**
     * @return the stats
     */
    public PerfStats getStats()
    {
        return stats;
    }

    /**
     * Records a failure execution.
     * 
     * @param failureContext
     *            The reference to the FailureContext.
     */
    public void recordFailure(Object failureContext)
    {
    }

    /**
     * Records this {@link ExecutionContext} is over.
     */
    public void recordEnd()
    {
        this.timer.stop();
    }
}
