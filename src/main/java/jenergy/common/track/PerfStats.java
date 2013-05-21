package jenergy.common.track;

import java.io.Serializable;

import jenergy.agent.common.util.time.Timer;

public interface PerfStats extends Serializable
{

    /**
     * Records the successful execution.
     * 
     * @param timer
     *            The time elapsed.
     */
    void recordExecution(Timer timer);

    /**
     * Records a failure execution.
     * 
     * @param timer
     *            The time elapsed.
     * @param failureContext
     *            The reference to the failure context.
     */
    void recordFailure(Timer timer, Object failureContext);

}
