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
package jenergy.agent.common.io;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;

import jenergy.profile.data.IOInfo;
import jenergy.profile.data.IOInfo.IOActivityType;
import jenergy.profile.data.MethodInfo;

public final class FileInputStreamDelegate extends FileInputStream
{
    /**
     * The instance of the real {@link FileInputStream}.
     */
    private final FileInputStream delegator;

    /**
     * The reference to the I/O activity to notify every time that a write operation occur.
     */
    private final IOInfo info;

    /**
     * Creates a {@link FileInputStream} by opening a connection to an actual file, the file named by the path name name in the file system. A new
     * {@link FileDescriptor} object is created to represent this file connection. First, if there is a security manager, its checkRead method is
     * called with the name argument as its argument.
     * 
     * If the named file does not exist, is a directory rather than a regular file, or for some other reason cannot be opened for reading then a
     * {@link FileNotFoundException} is thrown.
     * 
     * @param name
     *            The system-dependent file name.
     * @param input
     *            The instance of the monitored {@link FileInputStream}.
     * @param method
     *            The method that called the read operation.
     * @throws FileNotFoundException
     *             if the file does not exist, is a directory rather than a regular file, or for some other reason cannot be opened for reading.
     * @throws SecurityException
     *             If a security manager exists and its checkRead method denies read access to the file.
     */
    public FileInputStreamDelegate(String name, FileInputStream input, MethodInfo method) throws FileNotFoundException
    {
        super(name);
        this.delegator = input;
        this.info = new IOInfo(IOActivityType.READ, method);
    }

    /**
     * 
     * Creates a {@link FileInputStream} by opening a connection to an actual file, the file named by the {@link File} object file in the file system.
     * A new {@link FileDescriptor} object is created to represent this file connection. First, if there is a security manager, its checkRead method
     * is called with the path represented by the file argument as its argument.
     * 
     * If the named file does not exist, is a directory rather than a regular file, or for some other reason cannot be opened for reading then a
     * {@link FileNotFoundException} is thrown.
     * 
     * @param file
     *            The file to be opened for reading.
     * @param input
     *            The instance of the monitored {@link FileInputStream}.
     * @param method
     *            The method that called the read operation.
     * @throws FileNotFoundException
     *             if the file does not exist, is a directory rather than a regular file, or for some other reason cannot be opened for reading.
     * @throws SecurityException
     *             If a security manager exists and its checkRead method denies read access to the file.
     */
    public FileInputStreamDelegate(File file, FileInputStream input, MethodInfo method) throws FileNotFoundException
    {
        super(file);
        this.delegator = input;
        this.info = new IOInfo(IOActivityType.READ, method);
    }

    /**
     * 
     * Creates a {@link FileInputStream} by using the given file descriptor, which represents an existing connection to an actual file in the file
     * system. If there is a security manager, its checkRead method is called with the file descriptor fdObj as its argument to see if it's ok to read
     * the file descriptor. If read access is denied to the file descriptor a {@link SecurityException} is thrown.
     * 
     * If fdObj is null then a {@link NullPointerException} is thrown.
     * 
     * @param fdObj The file descriptor to be opened for reading.
     * @param input
     *            The instance of the monitored {@link FileInputStream}.
     * @param method
     *            The method that called the read operation.
     * @throws SecurityException
     *             If a security manager exists and its checkRead method denies read access to the file.
     */
    public FileInputStreamDelegate(FileDescriptor fdObj, FileInputStream input, MethodInfo method)
    {
        super(fdObj);
        this.delegator = input;
        this.info = new IOInfo(IOActivityType.READ, method);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException
    {
        return delegator.read(b, off, len);
    }

    @Override
    public int read() throws IOException
    {
        return this.delegator.read();
    }

    @Override
    public int read(byte[] b) throws IOException
    {
        return this.delegator.read(b);
    }
    
    @Override
    public int available() throws IOException
    {
        return this.delegator.available();
    }

    @Override
    public void close() throws IOException
    {
        this.delegator.close();
    }
    
    @Override
    public FileChannel getChannel()
    {
        return super.getChannel();
    }

    @Override
    public long skip(long n) throws IOException
    {
        return this.delegator.skip(n);
    }

    @Override
    public synchronized void mark(int readlimit)
    {
        this.delegator.mark(readlimit);
    }
    
    @Override
    public boolean markSupported()
    {
        return this.delegator.markSupported();
    }

    @Override
    public synchronized void reset() throws IOException
    {
        this.delegator.reset();
    }

    /**
     * @return the info
     */
    public IOInfo getInfo()
    {
        return info;
    }
}
