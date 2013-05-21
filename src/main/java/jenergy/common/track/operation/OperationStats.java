package jenergy.common.track.operation;

import jenergy.common.track.PerfStats;

public interface OperationStats extends PerfStats
{

    /**
     * 
     * @param key
     *            The id of the {@link OperationStats} to be returned.
     * @return The reference to the {@link OperationStats} of the given key.
     */
    OperationStats getOperationStats(Object key);
}
