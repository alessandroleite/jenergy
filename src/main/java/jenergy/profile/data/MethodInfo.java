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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import jenergy.agent.common.util.time.Timer;

public final class MethodInfo implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -9103595975148819796L;

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
     * The estimated CPU power consumption.
     */
    private BigDecimal cpuPowerConsumption;

    /**
     * The reference to the caller method.
     */
    private final MethodInfo caller;

    /**
     * The reference to the monitored method.
     */
    private Method methodRef;

    /**
     * The {@link List} of activities realized by the monitored method. For instance, network activity, disk activity, etc.
     */
    private final List<Activity<?>> activities = new CopyOnWriteArrayList<Activity<?>>();

    /**
     * @param name
     *            The name of the method to be analyzed.
     * @param methodTimer
     *            The {@link Timer} with the information about the execution time of the method.
     * @param callerOfThisMethod
     *            The reference to method that called this method.
     */
    public MethodInfo(String name, Timer methodTimer, MethodInfo callerOfThisMethod)
    {
        this.methodName = name;
        this.timer = methodTimer;
        this.caller = callerOfThisMethod;
    }

    /**
     * 
     * @param name
     *            The name of the method to be analyzed.
     * @param methodTimer
     *            The start time of the method execution.
     * @param tid
     *            The thread id.
     * @param callerOfThisMethod
     *            The reference to method that called this method.
     */
    public MethodInfo(String name, Timer methodTimer, long tid, MethodInfo callerOfThisMethod)
    {
        this(name, methodTimer, callerOfThisMethod);
        this.threadId = tid;
    }

    /**
     * 
     * @param method
     *            The method that is monitored.
     * @param methodTimer
     *            The timer to measure the method execution.
     * @param callerOfThisMethod
     *            The reference to method that called this method.
     */
    public MethodInfo(Method method, Timer methodTimer, MethodInfo callerOfThisMethod)
    {
        this(formatMethodName(method.getDeclaringClass(), method.getName()), methodTimer, Thread.currentThread().getId(), callerOfThisMethod);
        this.methodRef = method;
    }
    
    /**
     * @param activity
     *            The activity to be add. Might not be <code>null</code>.
     * @param <T>
     *            The type of the data of the given activity.
     */
    public <T> void addActivity(Activity<T> activity)
    {
        if (activity != null)
        {
            this.activities.add(activity);
        }
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
    public BigDecimal getCpuPowerConsumption()
    {
        return cpuPowerConsumption;
    }

    /**
     * @param newCpuPowerValue
     *            the cpuPower to set
     */
    public void setCpuPowerConsumption(BigDecimal newCpuPowerValue)
    {
        this.cpuPowerConsumption = newCpuPowerValue;
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
     * @return the methodRef
     */
    public Method getMethodRef()
    {
        return methodRef;
    }

    /**
     * @return the threadId
     */
    public long getThreadId()
    {
        return threadId;
    }

    /**
     * @return the caller
     */
    public MethodInfo getCaller()
    {
        return caller;
    }
    
    /**
     * Returns a non <code>null</code> and read-only {@link List} with the activities realized by this method.
     * 
     * @return A non <code>null</code> and read-only {@link List} with the activities realized by this method.
     */
    public List<Activity<?>> getActivities()
    {
        return Collections.unmodifiableList(activities);
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
        sb.append("Power......:").append(this.getCpuPowerConsumption()).append(line);

        return sb.toString();
    }

    /**
     * Formats and returns the name of the method. The format is: <class name>.<method name>#<thread id>.
     * 
     * @param methodClass
     *            The class where the method was declared.
     * @param methodName
     *            The method name.
     * @return The name of the method. The format is: <class name>.<method name>#<thread id>.
     * @see #formatMethodName(String, String)
     */
    public static String formatMethodName(Class<?> methodClass, String methodName)
    {
        return formatMethodName(methodClass.getName(), methodName);
    }

    /**
     * Formats and returns the name of the method. The format is: <class name>.<method name>#<thread id>.
     * 
     * @param className
     *            The name of the {@link Class} where the method was declared.
     * @param methodName
     *            The method name.
     * @return The name of the method. The format is: <class name>.<method name>#<thread id>.
     */
    public static String formatMethodName(String className, String methodName)
    {
        return new StringBuilder(className).append(".").append(methodName).append("#").append(Thread.currentThread().getId()).toString();
    }

}
