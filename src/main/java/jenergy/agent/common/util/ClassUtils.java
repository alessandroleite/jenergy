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
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
     * 
     * Returns an array of {@link Field} object reflecting all fields declared by the given class. This includes public, protected, package (default)
     * and private fields including the inherited fields. The elements in the array returned are <strong>not sorted</strong> and they are in order of
     * subclass to superclass. This method returns an array of length 0 if the class or interface declares no fields, or if this {@link Class} object
     * represents a primitive type, an array class, or void.
     * 
     * @param clazz
     *            The class to return its declared fields.
     * @return the array of {@link Field} objects representing all the declared fields of given class.
     */
    public static Field[] fields(Class<?> clazz)
    {
        List<Field> declaredClassFields = new ArrayList<Field>();
        allFields(clazz, declaredClassFields);

        return declaredClassFields.toArray(new Field[declaredClassFields.size()]);
    }

    /**
     * Returns a reference to a {@link Field} declared in the given {@link Class}.
     * 
     * @param name
     *            The name of the {@link Field} to be returned. Might not be <code>null</code>.
     * @param clazz
     *            The class to search the {@link Field}.
     * @return The {@link Field} found or <code>null</code> if there is not any {@link Field} with the given name.
     */
    public static Field getField(String name, Class<?> clazz)
    {
        for (Field f : fields(clazz))
        {
            if (f.getName().equals(name))
            {
                return f;
            }
        }
        return null;
    }

    /**
     * Returns the value represented by the given {@link Field}.
     * 
     * @param f
     *            The field to extract its value.
     * @param obj
     *            The object from which the given field's value is to be extracted.
     * @param <T>
     *            The type of the value represented by f.
     * @return The value of the represented field in given object.
     * @throws IllegalArgumentException
     *             If the specified object is not an instance of the class or interface declaring the underlying field (or a subclass or implementor
     *             thereof).
     * @throws IllegalAccessException
     *             If this Field object is enforcing Java language access control and the underlying field is inaccessible.
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(Field f, Object obj) throws IllegalArgumentException, IllegalAccessException
    {
        f.setAccessible(true);
        return (T) f.get(obj);
    }

    /**
     * Returns the value represented by the field declared in obj.
     * 
     * @param name
     *            The name of the field. Might not be <code>null</code>.
     * @param obj
     *            The from which the field's value is to be extracted.
     * @param <T>
     *            The type of the value represented by field.
     * @return The value represented by the field. A <code>null</code> value can be either: the field's value is <code>null</code>; there is any field
     *         in the class hierarchy with the given name; the JVM thrown  {@link IllegalAccessException} or {@link IllegalArgumentException}. 
     */
    public static <T> T fieldValue(String name, Object obj)
    {
        Field f = getField(name, obj.getClass());
        if (f != null)
        {
            try
            {
                return get(f, obj);
            }
            catch (IllegalArgumentException e)
            {
                throw new RuntimeException(e.getMessage(), e);
            }
            catch (IllegalAccessException e)
            {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        return null;
    }

    /**
     * Returns all declared fields of the given class.
     * 
     * @param clazz
     *            The class to get its fields.
     * 
     * @param output
     *            The {@link List} to put the fields.
     */
    private static void allFields(Class<?> clazz, List<Field> output)
    {
        if (clazz != null && !Object.class.equals(clazz))
        {
            allFields(clazz.getSuperclass(), output);
        }

        if (clazz != null)
        {
            output.addAll(Arrays.asList(clazz.getDeclaredFields()));
        }
    }

    // /**
    // * <p>
    // * Copy property values from the origin bean to the destination bean for all cases where the property names are the same.
    // * </p>
    // *
    // * <p>
    // * For more details see <code>BeanUtilsBean</code>.
    // * </p>
    // *
    // * @param src
    // * Origin bean whose properties are retrieved
    // * @param target
    // * Destination bean whose properties are modified
    // */
    // public static void copyProperties(Object src, Object target)
    // {
    // try
    // {
    // BeanUtils.copyProperties(target, src);
    // }
    // catch (IllegalAccessException exception)
    // {
    // throw new RuntimeException(exception.getMessage(), exception);
    // }
    // catch (InvocationTargetException exception)
    // {
    // throw new RuntimeException(exception.getMessage(), exception);
    // }
    // }
}
