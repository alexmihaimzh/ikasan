/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * 
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing 
 * of individual contributors are as shown in the packaged copyright.txt 
 * file. 
 * 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.serialiser.service;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoPool;
import org.ikasan.spec.serialiser.Serialiser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


/**
 * Implementation of an Ikasan Serialiser for the serialisation/deserialisation of object to and from byte[].
 * 
 * @author Ikasan Development Team
 * 
 */
public class GenericKryoToBytesSerialiser<T> implements Serialiser<T,byte[]>
{
    /** pool of kryo instanances */
    private KryoPool pool;

    /**
     * Constructor
     * @param pool
     */
    public GenericKryoToBytesSerialiser(KryoPool pool)
    {
        this.pool = pool;
        if(pool == null)
        {
            throw new IllegalArgumentException("pool cannot be 'null'");
        }
    }

    /**
     * Serialise the incoming object to a byte[]
     * @param source
     * @return
     */
    @Override
    public byte[] serialise(T source)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
        Kryo kryo = pool.borrow();

        try
        {
            kryo.writeClassAndObject(output, source);
            output.flush();
            return byteArrayOutputStream.toByteArray();
        }
        finally
        {
            pool.release(kryo);
        }
    }

    /**
     * Deserialise the byte[] back to its instantiated object.
     * @param source
     * @return
     */
    @Override
    public T deserialise(byte[] source)
    {
        Input input = new Input(new ByteArrayInputStream(source));
        Kryo kryo=pool.borrow();

        try
        {
            return (T)kryo.readClassAndObject(input);
        }
        finally
        {
            pool.release(kryo);
        }
    }

}
