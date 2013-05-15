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
 *
 *    Contributors:
 *          Alessandro Ferreira Leite - the initial implementation.
 */
package jenergy.profile.data;

import java.io.Serializable;

import jenergy.util.Observer;
import jenergy.util.Subject;

public class IOInfo implements Serializable, Observer
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -8633571924453299948L;

    /**
     * Enumeration with the type of I/O operations.
     */
    public static enum IOActivityType
    {
        /**
         * Read operation.
         */
        READ,

        /**
         * Write operation.
         */
        WRITE;
    }

    /**
     * The method that called (executed) the I/O activity.
     */
    private MethodInfo method;

    /**
     * The activity type.
     */
    private final IOActivityType activityType;

    /**
     * Number of bytes that has been read/written.
     */
    private volatile long bytes;

    /**
     * 
     * @param type
     *            The I/O activity type. Might not be <code>null</code>.
     */
    public IOInfo(IOActivityType type)
    {
        this.activityType = type;
    }

    /**
     * 
     * @param type
     *            The I/O activity type. Might not be <code>null</code>.
     * @param methodInfo
     *            The callee of the I/O operation.
     */
    public IOInfo(IOActivityType type, MethodInfo methodInfo)
    {
        this(type);
        this.method = methodInfo;
    }

    /**
     * Increments the number of bytes read/write.
     * 
     * @param value
     *            The number of bytes that has been read/written.
     */
    protected synchronized void increment(long value)
    {
        if (value > 0)
        {
            this.bytes += value;
        }
    }

    /**
     * @return the method
     */
    public MethodInfo getMethod()
    {
        return method;
    }

    /**
     * @param methodInfo
     *            the method to set
     */
    public void setMethod(MethodInfo methodInfo)
    {
        this.method = methodInfo;
    }

    /**
     * @return the activityType
     */
    public IOActivityType getActivityType()
    {
        return activityType;
    }

    /**
     * @return the bytes
     */
    public long getBytes()
    {
        return bytes;
    }

    @Override
    public void update(Subject observable, Object... args)
    {
        this.increment(((Integer) args[0]).longValue());
    }
}
