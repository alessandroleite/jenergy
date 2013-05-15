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

import java.io.FileInputStream;
import java.io.IOException;

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
     * The reference to the I/O activity to notify every time that an write operation had been called.
     */
    private final IOInfo info;

    /**
     * 
     * @param input
     *            The instance of the monitored {@link FileInputStream}.
     * @param ioInfo
     *            The reference to the {@link IOInfo} to be used by this stream.
     * @throws IOException
     *             If an I/O error occurs during the read of the file descriptor.
     */
    private FileInputStreamDelegate(FileInputStream input, IOInfo ioInfo) throws IOException
    {
        super(input.getFD());
        this.delegator = input;
        this.info = ioInfo;
    }

    /**
     * 
     * @param input
     *            The instance of the monitored {@link FileInputStream}.
     * @param method
     *            The method that called the read operation.
     * @throws IOException
     *             If an I/O error occurs during the read of the file descriptor.
     */
    public FileInputStreamDelegate(FileInputStream input, MethodInfo method) throws IOException
    {
        this(input, new IOInfo(IOActivityType.READ, method));
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException
    {
        return delegator.read(b, off, len);
    }

    @Override
    public int read() throws IOException
    {
        return super.read();
    }

    @Override
    public int read(byte[] b) throws IOException
    {
        return super.read(b);
    }

    /**
     * @return the info
     */
    public IOInfo getInfo()
    {
        return info;
    }
}
