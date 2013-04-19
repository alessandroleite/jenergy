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
package jenergy.utils;

/**
 * Utility class to work with threads.
 */
public final class Threads
{
    /**
     * Constant that represents one seconds.
     */
    private static final int ONE_SECOND = 1000;

    /**
     * Private constructor to avoid instance of this class.
     */
    private Threads()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Causes the currently executing thread to sleep during one second.
     */
    public static void sleepOneSecond()
    {
        sleep(ONE_SECOND);
    }

    /**
     * Causes the currently executing thread to sleep for the specific time in milliseconds.
     * 
     * @param millis
     *            The amount of time to the current executing thread to sleep. Might be greater than zero.
     */
    public static void sleep(long millis)
    {
        try
        {
            Thread.sleep(millis);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
