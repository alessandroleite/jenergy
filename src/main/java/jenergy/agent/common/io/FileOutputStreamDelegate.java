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

import java.io.FileOutputStream;
import java.io.IOException;

public final class FileOutputStreamDelegate extends FileOutputStream
{
    /**
     * The real {@link FileOutputStream} instance to write the data.
     */
    private final FileOutputStream delegator;

    /**
     * The total number of bytes wrote into the buffer.
     */
    private volatile int size;

    /**
     * 
     * @param output
     *            The {@link FileOutputStream} instance to write the data. Might not be <code>null</code>.
     * @throws IOException
     *             If it's impossible to get the file descriptor of the output.
     */
    public FileOutputStreamDelegate(FileOutputStream output) throws IOException
    {
        super(output.getFD());
        this.delegator = output;
    }

    @Override
    public void write(int b) throws IOException
    {
        delegator.write(b);
        size = 4;
    }

    @Override
    public void write(byte[] b) throws IOException
    {
        super.write(b);
        size = b.length;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException
    {
        super.write(b, off, len);
        size = len - off;
    }

    /**
     * Returns the total number of bytes wrote into the buffer.
     * 
     * @return the total number of bytes wrote into the buffer.
     */
    public int getSize()
    {
        return size;
    }
}
