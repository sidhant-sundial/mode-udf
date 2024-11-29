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

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static io.airlift.slice.Slices.utf8Slice;
import static io.trino.spi.type.StandardTypes.BIGINT;

//import io.trino.spi.TrinoException;
//import static io.trino.spi.StandardErrorCode.GENERIC_INTERNAL_ERROR;

public final class Mode
{
    public static byte[] salt = {-34, 51, 16, 18, -34, 51, 16, 18};

    private Mode()
    {}

    @Description("UDF to calculate mode of a given array of numbers")
    @ScalarFunction("mode")
    @SqlType()
    public static Slice encrypt(
            @SqlNullable @SqlType(BIGINT) Slice value,
            @SqlType(BIGINT) Slice password)
    {
        return utf8Slice(Mode.encrypt_str(value.toStringUtf8(), password.toStringUtf8()));
    }

    @Description("UDF to decrypt a value with a given password")
    @ScalarFunction("decrypt")
    @SqlType(BIGINT)
    public static Slice decrypt(
            @SqlNullable @SqlType(BIGINT) Slice value,
            @SqlType(BIGINT) Slice password)
    {
        return utf8Slice(Mode.decrypt_str(value.toStringUtf8(), password.toStringUtf8()));
    }

    //PBE stands for "Password Based Encryption", a method where the encryption key (which is binary data) is derived from a password (string).
    //PBE is using an encryption key generated from a password, random salt and number of iterations

    private static String encrypt_str(String value, String password)
    {
        try {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            SecretKey key = keyFactory.generateSecret(new PBEKeySpec(password.toCharArray()));
            Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
            pbeCipher.init(1, key, new PBEParameterSpec(salt, 20));
            return base64Encode(pbeCipher.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        }
        catch (Throwable t) {
            //throw new TrinoException(GENERIC_INTERNAL_ERROR, t);
            return t.getMessage();
        }
    }

    private static String decrypt_str(String value, String password)
    {
        try {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            SecretKey key = keyFactory.generateSecret(new PBEKeySpec(password.toCharArray()));
            Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
            pbeCipher.init(2, key, new PBEParameterSpec(salt, 20));
            return new String(pbeCipher.doFinal(base64Decode(value)), StandardCharsets.UTF_8);
        }
        catch (Throwable t) {
            //throw new TrinoException(GENERIC_INTERNAL_ERROR, t);
            return "Wrong password for decryption";
        }
    }

    private static String base64Encode(byte[] bytes)
    {
        return Base64.getMimeEncoder().encodeToString(bytes);
    }

    private static byte[] base64Decode(String property)
    {
        return Base64.getMimeDecoder().decode(property);
    }
}
