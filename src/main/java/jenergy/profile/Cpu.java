/**
 * Copyright 2013 Alessandro
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
 */
package jenergy.profile;

import java.io.Serializable;

public class Cpu implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -2055129644768076699L;

    private volatile long cycleDuration;

    private volatile long computationTime;

    public Cpu()
    {

    }

    /**
     * Returns the {@link Cpu} power consumption.
     * 
     * @return The {@link Cpu} power consumption.
     */
    public double power()
    {
        return 10;
    }

    /**
     * Returns the CPU cycle duration.
     * 
     * @return THe CPU cycle duration.
     */
    public long cycleDuration()
    {
        return cycleDuration;
    }

    /**
     * Update the information about the CPU cycle duration.
     */
    public void updateCycleDuration()
    {
        synchronized (this)
        {
            this.cycleDuration = System.currentTimeMillis() - computationTime();
            this.computationTime = System.currentTimeMillis();
        }
    }

    /**
     * Returns the CPU computation time.
     * 
     * @return the CPU computation time.
     */
    public long computationTime()
    {
        return computationTime;
    }

}
