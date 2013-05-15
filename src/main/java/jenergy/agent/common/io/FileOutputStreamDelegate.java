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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import jenergy.profile.data.IOInfo;
import jenergy.profile.data.IOInfo.IOActivityType;
import jenergy.profile.data.MethodInfo;

public final class FileOutputStreamDelegate extends FileOutputStream
{
    /**
     * The real {@link FileOutputStream} instance to write the data.
     */
    private final FileOutputStream delegator;

    /**
     * The reference to the I/O activity to notify every time that an write operation had been called.
     */
    private final IOInfo info;

    /**
     * Creates a file output stream to write to the file with the specified name. A new FileDescriptor object is created to represent this file
     * connection. First, if there is a security manager, its checkWrite method is called with name as its argument.
     * 
     * If the file exists but is a directory rather than a regular file, does not exist but cannot be created, or cannot be opened for any other
     * reason then a FileNotFoundException is thrown.
     * 
     * @param name
     *            the system-dependent filename.
     * @param output
     *            The {@link FileOutputStream} instance to write the data. Might not be <code>null</code>.
     * @param methodInfo
     *            The method that called the output operation.
     * @throws FileNotFoundException
     *             If the file exists but is a directory rather than a regular file, does not exist but cannot be created, or cannot be opened for any
     *             other reason.
     */
    public FileOutputStreamDelegate(String name, FileOutputStream output, MethodInfo methodInfo) throws FileNotFoundException
    {
        super(name);
        this.delegator = output;
        this.info = new IOInfo(IOActivityType.WRITE, methodInfo);
        //ClassUtils.copyProperties(this.delegator, this);
    }

    /**
     * Creates a file output stream to write to the file represented by the specified File object. A new FileDescriptor object is created to represent
     * this file connection. First, if there is a security manager, its checkWrite method is called with the path represented by the file argument as
     * its argument.
     * 
     * If the file exists but is a directory rather than a regular file, does not exist but cannot be created, or cannot be opened for any other
     * reason then a FileNotFoundException is thrown.
     * 
     * @param file
     *            the file to be opened for writing.
     * @param output
     *            The {@link FileOutputStream} instance to write the data. Might not be <code>null</code>.
     * @param methodInfo
     *            The method that called the output operation.
     * @throws FileNotFoundException
     *             If the file exists but is a directory rather than a regular file, does not exist but cannot be created, or cannot be opened for any
     *             other reason.
     */
    public FileOutputStreamDelegate(File file, FileOutputStream output, MethodInfo methodInfo) throws FileNotFoundException
    {
        super(file);
        this.delegator = output;
        this.info = new IOInfo(IOActivityType.WRITE, methodInfo);
        //ClassUtils.copyProperties(this.delegator, this);
    }

    /**
     * Creates a file output stream to write to the specified file descriptor, which represents an existing connection to an actual file in the file
     * system. First, if there is a security manager, its checkWrite method is called with the file descriptor fdObj argument as its argument.
     * 
     * If fdObj is null then a NullPointerException is thrown.
     * 
     * This constructor does not throw an exception if fdObj is invalid. However, if the methods are invoked on the resulting stream to attempt I/O on
     * the stream, an IOException is thrown.
     * 
     * @param fd
     *            the file descriptor to be opened for writing
     * 
     * @param output
     *            The {@link FileOutputStream} instance to write the data. Might not be <code>null</code>.
     * @param methodInfo
     *            The method that called the output operation.
     */
    public FileOutputStreamDelegate(FileDescriptor fd, FileOutputStream output, MethodInfo methodInfo)
    {
        super(fd);
        this.delegator = output;
        this.info = new IOInfo(IOActivityType.WRITE, methodInfo);
        //ClassUtils.copyProperties(this.delegator, this);
    }

    /**
     * 
     * Creates a file output stream to write to the file with the specified name. If the second argument is true, then bytes will be written to the
     * end of the file rather than the beginning. A new FileDescriptor object is created to represent this file connection. First, if there is a
     * security manager, its checkWrite method is called with name as its argument.
     * 
     * If the file exists but is a directory rather than a regular file, does not exist but cannot be created, or cannot be opened for any other
     * reason then a FileNotFoundException is thrown.
     * 
     * 
     * @param name
     *            the system-dependent file name
     * @param append
     *            if <code>true</code>, then bytes will be written to the end of the file rather than the beginning
     * @param output
     *            The {@link FileOutputStream} instance to write the data. Might not be <code>null</code>.
     * @param methodInfo
     *            The method that called the output operation.
     * @throws FileNotFoundException
     *             if the file exists but is a directory rather than a regular file, does not exist but cannot be created, or cannot be opened for any
     *             other reason.
     */
    public FileOutputStreamDelegate(String name, boolean append, FileOutputStream output, MethodInfo methodInfo) throws FileNotFoundException
    {
        super(name, append);
        this.delegator = output;
        this.info = new IOInfo(IOActivityType.WRITE, methodInfo);
        //ClassUtils.copyProperties(this.delegator, this);
    }

    /**
     * Creates a file output stream to write to the file represented by the specified File object. If the second argument is true, then bytes will be
     * written to the end of the file rather than the beginning. A new FileDescriptor object is created to represent this file connection. First, if
     * there is a security manager, its checkWrite method is called with the path represented by the file argument as its argument.
     * 
     * If the file exists but is a directory rather than a regular file, does not exist but cannot be created, or cannot be opened for any other
     * reason then a FileNotFoundException is thrown.
     * 
     * @param file
     *            the file to be opened for writing.
     * @param append
     *            if <code>true</code>, then bytes will be written to the end of the file rather than the beginning
     * @param output
     *            The {@link FileOutputStream} instance to write the data. Might not be <code>null</code>.
     * @param methodInfo
     *            The method that called the output operation.
     * @throws FileNotFoundException
     *             if the file exists but is a directory rather than a regular file, does not exist but cannot be created, or cannot be opened for any
     *             other reason.
     */
    public FileOutputStreamDelegate(File file, boolean append, FileOutputStream output, MethodInfo methodInfo) throws FileNotFoundException
    {
        super(file, append);
        this.delegator = output;
        this.info = new IOInfo(IOActivityType.WRITE, methodInfo);
        //ClassUtils.copyProperties(this.delegator, this);
    }

    @Override
    public void write(int b) throws IOException
    {
        delegator.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException
    {
        delegator.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException
    {
        delegator.write(b, off, len);
    }

    /**
     * Returns the total number of bytes write into the buffer.
     * 
     * @return the total number of bytes write into the buffer.
     */
    public synchronized long getSize()
    {
        return getInfo().getBytes();
    }

    @Override
    public void close() throws IOException
    {
        super.close();
        this.delegator.close();
    }

    @Override
    public void flush() throws IOException
    {
        this.delegator.flush();
        super.flush();
    }

    @Override
    public FileChannel getChannel()
    {
        return this.delegator.getChannel();
    }

    /**
     * @return the info
     */
    public IOInfo getInfo()
    {
        return info;
    }
}
