/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.starburstdata.udfs.mode;

import io.airlift.slice.Slice;
import io.trino.spi.function.Description;
import io.trino.spi.function.ScalarFunction;
import io.trino.spi.function.SqlNullable;
import io.trino.spi.function.SqlType;

import java.util.HashMap;

import static io.trino.spi.type.StandardTypes.BIGINT;

//import io.trino.spi.TrinoException;
//import static io.trino.spi.StandardErrorCode.GENERIC_INTERNAL_ERROR;

public final class Mode
{

    private Mode()
    {}

    @Description("UDF to calculate mode of a given array of numbers")
    @ScalarFunction("mode")
    @SqlType()
    public static Slice mode(
            @SqlNullable @SqlType(BIGINT) Slice value,
            @SqlType(BIGINT) Slice password)
    {
         return null;
    }


    public static int mode(int []array)
    {
        HashMap<Integer,Integer> hm = new HashMap<Integer,Integer>();
        int max  = 1;
        int temp = 0;

        for (int j : array) {

            if (hm.get(j) != null) {

                int count = hm.get(j);
                count++;
                hm.put(j, count);

                if (count > max) {
                    max = count;
                    temp = j;
                }
            } else
                hm.put(j, 1);
        }
        return temp;
    }
}
