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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javassist.bytecode.ClassFile;

@SuppressWarnings("rawtypes")
public final class ClassUtils
{

    private static final String CLASS_SUFFIX = ".class";

    private static final String REGEX_DOT = "\\.";

    private static final String FILE_SEPARATOR = File.separatorChar + "";

    /**
     * Private constructor to avoid instance of this class.
     */
    private ClassUtils()
    {
        throw new UnsupportedOperationException();
    }

    public static class ClassFinderException extends RuntimeException
    {

        private static final long serialVersionUID = 5309532665686994276L;

        public ClassFinderException(String msg, Throwable cause)
        {
            super(msg, cause);
        }
    }

    public static boolean isApplicationJar()
    {
        try
        {
            File f = new File(ClassUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            return f.isFile() && f.getAbsolutePath().endsWith(".jar");
        }
        catch (URISyntaxException e)
        {
            throw new ClassFinderException(null, e);
        }
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
        }
        if (cl == null)
        {
            cl = ClassUtils.class.getClassLoader();
        }
        return cl;
    }

    /**
     * Returns the current application root path.
     * 
     * @return
     */
    public static String getAppPath()
    {
        try
        {
            File f = new File(ClassUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            return f.getAbsolutePath();
        }
        catch (URISyntaxException e)
        {
            throw new ClassFinderException(null, e);
        }
    }

    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     * 
     * @param packageName
     *            The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws URISyntaxException
     */
    public static List<Class> getApplicationClasses(String packageName)
    {
        try
        {
            File f = new File(ClassUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            List<Class> classes = null;
            if (f.isDirectory())
            {
                classes = findClassesInsideDir(f, packageName);
            }
            else if (f.isFile())
            {
                classes = findClassesInsideJar(f, packageName);
            }
            else
            {
                throw new ClassFinderException("Cannot find classes", null);
            }
            return classes;
        }
        catch (ClassNotFoundException e)
        {
            throw new ClassFinderException(e.getMessage(), e);
        }
        catch (ZipException e)
        {
            throw new ClassFinderException(e.getMessage(), e);
        }
        catch (URISyntaxException e)
        {
            throw new ClassFinderException(e.getMessage(), e);
        }
        catch (IOException e)
        {
            throw new ClassFinderException(e.getMessage(), e);
        }
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     * 
     * @param directory
     *            The base directory
     * @param packageName
     *            The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static List<Class> findClassesInsideDir(File directory, String packageName) throws ClassNotFoundException
    {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists())
        {
            return classes;
        }

        List<File> files = new ArrayList<File>(Arrays.asList(directory.listFiles()));
        while (!files.isEmpty())
        {
            File file = files.get(0);
            files.remove(file);
            if (file.isDirectory())
            {
                files.addAll(Arrays.asList(file.listFiles()));
            }
            else if (file.isFile())
            {
                Class clazz = fileNameToClass(file.getAbsolutePath(), packageName);
                if (clazz != null)
                {
                    classes.add(clazz);
                }
            }
        }
        return classes;
    }

    public static List<Class> findClassesInsideJar(File jar, String packageName) throws URISyntaxException, ZipException, IOException,
            ClassNotFoundException
    {
        ZipFile zf = new ZipFile(jar.getAbsoluteFile());
        Enumeration e = zf.entries();

        List<Class> classes = new ArrayList<Class>();
        while (e.hasMoreElements())
        {
            ZipEntry ze = (ZipEntry) e.nextElement();
            Class clazz = null;
            try
            {
                clazz = fileNameToClass(ze.getName(), packageName);
            }
            catch (ClassNotFoundException exp)
            {
                // do nothing
            }
            catch (NoClassDefFoundError exp)
            {
                // do nothing
            }
            if (clazz != null)
            {
                classes.add(clazz);
            }
        }
        zf.close();
        return classes;
    }

    private static Class fileNameToClass(String fileName, String packageName) throws ClassNotFoundException
    {
        String path = packageName.replaceAll(REGEX_DOT, FILE_SEPARATOR);
        if (fileName.contains(path) && fileName.endsWith(CLASS_SUFFIX))
        {
            return Class.forName(fileName.substring(fileName.indexOf(path), fileName.length() - CLASS_SUFFIX.length()).replaceAll(FILE_SEPARATOR,
                    REGEX_DOT));
        }
        else
        {
            return null;
        }
    }

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
                    s = s.substring("jar:file:/".length(), pos + ".jar".length());
                else
                    s = s.substring("jar:file:".length(), pos + ".jar".length());
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
                s = s.substring("file:/".length());
            else
                s = s.substring("file:".length());
            s = s.substring(0, s.lastIndexOf(className)).replaceAll("%20", " ");
        }
        else
        {
            s = "?";
        }
        return s;
    }

    public static Class<?>[] getClassesWith(String packageName, Class<? extends Annotation> annotation) throws IOException, ClassNotFoundException
    {

        if (annotation == null)
            throw new NullPointerException();

        final List<Class<?>> result = new ArrayList<Class<?>>();
        Class<?>[] classes = getClasses(getDefaultClassLoader(), packageName);

        if (classes != null)
        {
            for (Class<?> clazz : classes)
            {
                if (clazz.isAnnotationPresent(annotation))
                    result.add(clazz);
            }
        }
        return result.toArray(new Class<?>[result.size()]);

    }

    public static ClassFile getClassFile(Class<?> clazz) throws IOException
    {
        String className = clazz.getSimpleName() + ".class";

        if (clazz.getPackage() != null)
        {
            String tPackageName = clazz.getPackage().getName();
            String tClassName = clazz.getSimpleName();
            tPackageName = tPackageName.replace('.', '/');
            className = tPackageName + '/' + tClassName + ".class";
        }

        InputStream input = null;
        BufferedInputStream bis = null;
        try
        {
            input = clazz.getClassLoader().getResourceAsStream(className);
            bis = new BufferedInputStream(input);
            ClassFile cf = new ClassFile(new DataInputStream(bis));
            return cf;
        }
        finally
        {
            if (input != null)
                input.close();

            if (bis != null)
                bis.close();
        }
    }

    public static Class<?>[] getClasses(ClassLoader classLoader, String packageName) throws IOException, ClassNotFoundException
    {

        if (classLoader != null)
        {

            String path = packageName.replace('.', '/');
            Enumeration<URL> resources = classLoader.getResources(path);

            List<File> dirs = new ArrayList<File>();

            while (resources.hasMoreElements())
            {
                URL resource = resources.nextElement();
                dirs.add(new File(resource.getFile()));
            }

            ArrayList<Class<?>> classes = new ArrayList<Class<?>>();

            for (File directory : dirs)
            {
                classes.addAll(findClasses(directory, packageName));
            }

            return classes.toArray(new Class[classes.size()]);
        }
        return null;
    }

    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException
    {
        List<Class<?>> classes = new ArrayList<Class<?>>();

        if (!directory.exists())
        {
            return classes;
        }

        File[] files = directory.listFiles();
        for (File file : files)
        {
            if (file.isDirectory())
            {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            }
            else if (file.getName().endsWith(".class"))
            {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

    public static Class<?>[] getClasses(String packageName) throws IOException, ClassNotFoundException
    {
        return getClasses(getDefaultClassLoader(), packageName);
    }

}
