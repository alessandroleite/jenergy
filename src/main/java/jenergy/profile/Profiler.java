/**
 * Copyright 2013 Contributors
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

public interface Profiler extends Runnable
{
    /**
     * Enum with the valid states of a profiler thread.
     */
    public static enum State
    {
        /**
         * This state indicates that the profiler was just created but not started yet.
         */
        CREATED,

        /**
         * This states means that the profiler is active collecting execution data about some method.
         */
        RUNNING,

        /**
         * This states means that the profiler finished and it can't start again.
         */
        FINISHED;
    }

//    /**
//     * Returns the {@link Profiler} state.
//     * 
//     * @return The {@link Profiler} state. It's a read only information because it's not possible to change the state directly.
//     */
//    State state();

    /**
     * Stops this profile.
     */
    void stop();

}
