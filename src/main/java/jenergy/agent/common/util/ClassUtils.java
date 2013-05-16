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
package jenergy.agent.common.util;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

public final class ClassUtils
{

    /**
     * Private constructor to avoid instance of this class.
     */
    private ClassUtils()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the location of the given class.
     * 
     * @param clazz
     *            The class to get its location.
     * @return A non null {@link String} with the location of the given class.
     */
    public static String getClassLocation(Class<?> clazz)
    {
        String className = "/" + clazz.getName().replace('.', '/') + ".class";
        URL u = clazz.getResource(className);
        String s = u.toString();

        if (s.startsWith("jar:file:/"))
        {
            int pos = s.indexOf(".jar!/");
            if (pos != -1)
            {
                if (File.separator.equals("\\"))
                {
                    s = s.substring("jar:file:/".length(), pos + ".jar".length());
                }
                else
                {
                    s = s.substring("jar:file:".length(), pos + ".jar".length());
                }
                s = s.replaceAll("%20", " ");
            }
            else
            {
                s = "?";
            }
        }
        else if (s.startsWith("file:/"))
        {
            if (File.separator.equals("\\"))
            {
                s = s.substring("file:/".length());
            }
            else
            {
                s = s.substring("file:".length());
            }
            s = s.substring(0, s.lastIndexOf(className)).replaceAll("%20", " ");
        }
        else
        {
            s = "?";
        }
        return s;
    }

    /**
     * Return the default ClassLoader to use: typically the thread context ClassLoader, if available; the ClassLoader that loaded the ClassUtils class
     * will be used as fallback.
     * 
     * @return the default ClassLoader (never <code>null</code>)
     * @see java.lang.Thread#getContextClassLoader()
     */
    public static ClassLoader getDefaultClassLoader()
    {
        ClassLoader cl = null;
        try
        {
            cl = Thread.currentThread().getContextClassLoader();
        }
        catch (Throwable ex)
        {
            if (Logger.getLogger(ClassUtils.class).isDebugEnabled())
            {
                Logger.getLogger(ClassUtils.class).debug(ex.getMessage(), ex);
            }
        }
        if (cl == null)
        {
            cl = ClassUtils.class.getClassLoader();
        }
        return cl;
    }

    /**
     * <p>
     * Copy property values from the origin bean to the destination bean for all cases where the property names are the same.
     * </p>
     * 
     * <p>
     * For more details see <code>BeanUtilsBean</code>.
     * </p>
     * 
     * @param src
     *            Origin bean whose properties are retrieved
     * @param target
     *            Destination bean whose properties are modified
     */
    public static void copyProperties(Object src, Object target)
    {
        try
        {
            BeanUtils.copyProperties(target, src);
        }
        catch (IllegalAccessException exception)
        {
            throw new RuntimeException(exception.getMessage(), exception);
        }
        catch (InvocationTargetException exception)
        {
            throw new RuntimeException(exception.getMessage(), exception);
        }
    }
}
