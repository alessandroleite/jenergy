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
package jenergy.agent.aop.jboss;

import java.lang.instrument.Instrumentation;

import jenergy.agent.common.Cpu;
import jenergy.agent.common.config.ProfileConfig;
import jenergy.agent.common.util.ClassUtils;

public final class Agent
{

    /**
     * Private constructor to avoid instance of this class.
     */
    private Agent()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * The method to start the instrumentation of the program.
     * 
     * @param agentArgs
     *            The agent's arguments. This agent does not require any argument.
     * @param inst
     *            The instrumentation mechanism provides by the JVM.
     */
    public static void premain(String agentArgs, Instrumentation inst)
    {
        ProfileConfig.start();

        //String exclude = System.getProperty("jboss.aop.exclude");
        //System.setProperty("jboss.aop.exclude", "jenergy" + (exclude != null ? "," + exclude : ""));
        
        System.setProperty("jboss.aop.class.path", ClassUtils.getClassLocation(Agent.class));

        if (agentArgs != null && (agentArgs.indexOf(".jar") != -1 || (agentArgs.indexOf("/") != -1)))
        {
            System.setProperty("app.class.path", agentArgs);
            System.setProperty("jboss.aop.path", agentArgs);
        }
        
        Cpu.getInstance().activate();
        org.jboss.aop.standalone.Agent.premain(agentArgs + " -hotSwap", inst);
    }
}
