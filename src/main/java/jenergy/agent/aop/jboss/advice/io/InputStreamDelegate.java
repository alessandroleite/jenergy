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
package jenergy.agent.aop.jboss.advice.io;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamDelegate extends InputStream
{
    /**
     * The instance of the real {@link InputStream}. Might not be <code>null</code>.
     */
    private final InputStream delegator;

    /**
     * Number of bytes read.
     */
    private int bytes;

    /**
     * 
     * @param input
     *            The instance of the {@link InputStream} that is monitored.
     */
    public InputStreamDelegate(InputStream input)
    {
        this.delegator = input;
    }

    @Override
    public int read() throws IOException
    {
        int result = delegator.read();
        notify(4);
        return result;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException
    {
        int length = delegator.read(b, off, len);
        notify(length);
        return length;
    }

    @Override
    public int read(byte[] b) throws IOException
    {
        int result = this.delegator.read(b);
        notify(result);
        return result;
    }

    /**
     * Simulates a publish/subscriber behavior.
     * 
     * @param result
     *            The number of bytes reader.
     */
    private void notify(int result)
    {
        if (result != -1)
        {
            bytes += result;
        }
        else
        {
            System.out.printf("Number of bytes read %s kB\n", (double) bytes / 1024);
        }
    }
}
