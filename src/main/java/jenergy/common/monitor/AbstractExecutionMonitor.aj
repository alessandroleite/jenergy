package jenergy.common.monitor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;

import jenergy.aop.Advice;
import jenergy.common.track.PerfStats;

public abstract aspect AbstractExecutionMonitor
{

    /**
     * Matches execution of the worker object for a monitored request.
     * 
     * @param context
     */
    public pointcut monitoringExecution(ExecutionContext context) : execution(* ExecutionContext.execute(..)) && this(context);

    /** In the control flow of a monitored request, i.e., of the execution of a worker object for a monitored request. */
    public pointcut inExecution(ExecutionContext requestContext) : cflow(monitoringExecution(requestContext));

    protected pointcut scope() : within(*);

    protected pointcut monitorEnabled() : isMonitorEnabled() && scope();

    /**
     * The reference to the {@link FailureDetectionStrategy}. The default is <code>true</code> for all {@link Exception}.
     */
    private FailureDetectionStrategy failureDetectionStrategy = new FailureDetectionStrategy()
    {
        @Override
        public boolean isFailure(Throwable t, Advice advice)
        {
            return true;
        }
    };

    /**
     * 
     */
    protected abstract pointcut isMonitorEnabled();

    after(final ExecutionContext context) throwing (Throwable t) : monitoringExecution(context)
    {
        PerfStats stats = context.getStats();
        if (stats != null)
        {
            if (this.failureDetectionStrategy.isFailure(t, getAdvice(thisJoinPoint)))
            {
                context.recordFailure(t);
            }
            else
            {
                context.recordEnd();
            }
        }
    }

    protected Advice getAdvice(ProceedingJoinPoint joinPoint)
    {
        return null;
    }
    
    protected Advice getAdvice(final JoinPoint joinPoint)
    {
        return new Advice()
        {
            @Override
            public Method getTargetMethod()
            {
                return null;
            }
            
            @Override
            public <T> Constructor<T> getTargetConstructor()
            {
                return null;
            }
        };
    }

    static aspect TrackParents
    {
        after(ExecutionContext parent) returning (ExecutionContext child) : call(ExecutionContext+.new(..)) && inExecution(parent) 
        {
            child.setParent(parent);
        }
    }
}
