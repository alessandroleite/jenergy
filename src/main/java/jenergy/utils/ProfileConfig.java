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
package jenergy.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ProfileConfig
{
    /**
     * Private constructor to avoid instance of this class.
     */
    private ProfileConfig()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * This method configure the profiler. Basic it loads the properties reading the configuration file and it defines the 
     */
    public static void start()
    {        
        Thread.currentThread().setName("JEnergy Profile");
        loadProperties();
        
        System.out.println("+-----------------------------------------------------+");
        System.out.println("|                JEnergy Profile 0.0.1                |");
        System.out.println("+-----------------------------------------------------+");
        
        
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                System.out.println("+-----------------------------------------------------+");
                System.out.println("|          JEnergy Profile 0.0.1 stopped              |");
                System.out.println("+-----------------------------------------------------+");
            }
        });
    }

    /**
     * Read the properties files with the configuration about the agent.
     */
    protected static void loadProperties()
    {
        try
        {
            Properties properties = new Properties();
            InputStream is = ClassUtils.getDefaultClassLoader().getResourceAsStream("config.properties");

            if (is == null)
            {
                if (System.getProperty("jenergy.config.file") != null)
                {
                    is = new FileInputStream(System.getProperty("jenergy.config.file"));
                }
                else
                {
                    is = new FileInputStream(System.getProperty("config.properties"));
                }
            }

            properties.load(is);

            for (Object property : properties.keySet())
            {
                System.setProperty((String) property, properties.getProperty((String) property));
            }

        }
        catch (IOException exception)
        {
            String path = ClassUtils.getClassLocation(ProfileConfig.class);
            System.setProperty("jenergy.dump.file.path", path);
        }
    }
}
