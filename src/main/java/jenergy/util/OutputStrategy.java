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
package jenergy.util;

public enum OutputStrategy
{
    /**
     * The data are only added to the console.
     */
    CONSOLE,

    /**
     * The data are only saved to file.
     */
    FILE,

    /**
     * The data are added both to the console and to file.
     */
    FILE_AND_CONSOLE,

    /**
     * No output is produced.
     */
    NONE;
}
