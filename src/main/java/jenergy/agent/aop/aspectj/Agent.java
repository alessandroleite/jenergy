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
package jenergy.agent.aop.aspectj;

import java.lang.instrument.Instrumentation;

import jenergy.agent.common.Cpu;
import jenergy.util.ProfileConfig;


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
        //Aspects.aspectOf(AspectTrace.class);
        Cpu.getInstance().activate();
        org.aspectj.weaver.loadtime.Agent.premain(agentArgs, inst);
    }
}
