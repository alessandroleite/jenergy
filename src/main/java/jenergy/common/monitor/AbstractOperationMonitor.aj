package jenergy.common.monitor;

import jenergy.common.track.PerfStats;
import jenergy.common.track.operation.OperationStats;

@SuppressWarnings("serial")
public abstract aspect AbstractOperationMonitor extends AbstractExecutionMonitor
{

    protected abstract class OperationExecutionContext extends ExecutionContext
    {
        @Override
        public PerfStats lookupStats()
        {
            if (getParent() != null)
            {
                OperationStats stats = (OperationStats) getParent().getStats();
                return stats.getOperationStats(getKey());
            }
            return getTopLevelStats(getKey());
        }

        protected PerfStats getTopLevelStats(Object key)
        {
            return null;
        }

        /**
         * Returns an object that identifies uniquely the operation that is being performed.
         * 
         * @return An object that identifies uniquely the operation that is being performed.
         */
        protected abstract Object getKey();

        /** The current controller object executing, if any. */
        protected Object controller;
    }

    /**
     * This advice stores the controller instance whenever we construct a operation context.
     * 
     * @param controller
     *            The reference of the execution controller.
     * @param context
     *            The reference to the {@link OperationExecutionContext}.
     */
    after(Object controller) returning (OperationExecutionContext context) : 
        cflow(adviceexecution() && args(controller, ..)) && this(AbstractOperationMonitor) && call(OperationExecutionContext+.new(..))
    {
        context.controller = controller;
    }

    /**
     * This defaults to no join points. If a concrete aspect overrides <code>classControllerExec</code> with a concrete definition, then the monitor
     * will track operations at matching join points based on the <em>class</em> of the controller object.
     */
    protected pointcut classControllerExec(Object controller);

    Object around(final Object controller) : classControllerExec(controller) && monitorEnabled()
    {
        ExecutionContext ec = new OperationExecutionContext()
        {
            @Override
            protected Object getKey()
            {
                return controller.getClass();
            }

            @SuppressWarnings("unchecked")
            @Override
            protected <T> T invoke()
            {
                final Object result = proceed(controller);
                return (T) result;
            }
        };
        return ec.execute();
    }
    
    
    /** 
     * This defaults to no join points. If a concrete monitor overrides <code>methodSignatureControllerExec</code> with a concrete definition,
     * then it will track operations at matching join points based on the runtime class of the executing controller instance and the method
     * signature at the join point.
     */ 
    protected pointcut methodSignatureControllerExec(Object controller);   
    
    Object around(final Object controller) : methodSignatureControllerExec(controller) && monitorEnabled() {
        ExecutionContext rc = new OperationExecutionContext() {
            
            @SuppressWarnings("unchecked")
            @Override
            protected <T> T invoke()
            {
                final Object result = proceed(controller);
                return (T) result;
            }
                       
            protected Object getKey() {
                return concatenatedKey(controller.getClass(), thisJoinPointStaticPart.getSignature().getName());
            }
            
        };
        return rc.execute();        
    }   
    
    /** Concatenates keys by creating a dotted string, e.g., className.methodName. */
    public <E> String concatenatedKey(Class<E> aClass, String methodName)
    {
        return (aClass.getName() + "." + methodName).intern();
    }
}
