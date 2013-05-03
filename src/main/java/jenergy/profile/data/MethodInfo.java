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
import java.lang.reflect.Method;
import java.math.BigDecimal;

import jenergy.utils.Timer;

public final class MethodInfo implements Serializable
{

    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 6690005530794954253L;

    /**
     * The method timer.
     */
    private final Timer timer;

    /**
     * The method's times.
     */
    private Times times;

    /**
     * The method name.
     */
    private final String methodName;

    /**
     * The thread id.
     */
    private long threadId;

    /**
     * The cpu power consumption.
     */
    private BigDecimal cpuPower;

    /**
     * @param name
     *            The name of the method to be analyzed.
     * @param methodTimer
     *            The {@link Timer} with the information about the execution time of the method.
     */
    public MethodInfo(String name, Timer methodTimer)
    {
        this.methodName = name;
        this.timer = methodTimer;
    }

    /**
     * 
     * @param name
     *            The name of the method to be analyzed.
     * @param methodTimer
     *            The start time of the method execution.
     * @param tid
     *            The thread id.
     */
    public MethodInfo(String name, Timer methodTimer, long tid)
    {
        this(name, methodTimer);
        this.threadId = tid;
    }

    /**
     * 
     * @param method
     *            The method that is monitored.
     * @param methodTimer
     *            The timer to measure the method execution.
     */
    public MethodInfo(Method method, Timer methodTimer)
    {
        this(new StringBuilder(method.getDeclaringClass().getName()).append(".").append(method.getName()).append("#")
                .append(Thread.currentThread().getId()).toString(), methodTimer, Thread.currentThread().getId());
    }


    /**
     * Returns the method's execution duration.
     * 
     * @return the method's execution duration.
     */
    public long getMethodDuration()
    {
        return this.timer.time();
    }

    /**
     * @return the times
     */
    public Times getTimes()
    {
        return times;
    }

    /**
     * @param newTimesInstance
     *            the times to set
     */
    public void setTimes(Times newTimesInstance)
    {
        this.times = newTimesInstance;
    }

    /**
     * @return the cpuPower
     */
    public BigDecimal getCpuPower()
    {
        return cpuPower;
    }

    /**
     * @param newCpuPowerValue
     *            the cpuPower to set
     */
    public void setCpuPower(BigDecimal newCpuPowerValue)
    {
        this.cpuPower = newCpuPowerValue;
    }

    /**
     * @return the timer
     */
    public Timer getTimer()
    {
        return timer;
    }

    /**
     * @return the methodName
     */
    public String getMethodName()
    {
        return methodName;
    }

    /**
     * @return the threadId
     */
    public long getThreadId()
    {
        return threadId;
    }

    @Override
    public String toString()
    {
        // return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);

        StringBuilder sb = new StringBuilder();

        final String line = "\n";

        sb.append("Method.....:").append(this.getMethodName()).append(line);
        sb.append("Timer......:").append(this.getTimer()).append(line);
        // sb.append("CPU time...:").append(this.getTimes().getCpuTime()).append(line);
        // sb.append("User time..:").append(this.getTimes().getUserTime()).append(line);
        sb.append("Power......:").append(this.getCpuPower()).append(line);

        return sb.toString();

    }
}
