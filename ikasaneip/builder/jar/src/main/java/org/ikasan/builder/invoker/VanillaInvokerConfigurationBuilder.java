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
package org.ikasan.builder.invoker;

import org.ikasan.flow.visitorPattern.invoker.InvokerConfiguration;

/**
 * Vanilla invoker configuration builder.
 * @author Ikasan Development Team
 */
public class VanillaInvokerConfigurationBuilder
{
    /** invoker configuration */
    InvokerConfiguration invokerConfiguration;

    /**
     * Constructor
     * @param invokerConfiguration
     */
    public VanillaInvokerConfigurationBuilder(InvokerConfiguration invokerConfiguration)
    {
        this.invokerConfiguration = invokerConfiguration;
        if(invokerConfiguration == null)
        {
            throw new IllegalArgumentException("InvokerConfiguration cannot be 'null'");
        }
    }

    /**
     * Set whether invoker supports dynamic configuration.
     *
     * @param dynamicConfiguration
     * @return
     */
    public VanillaInvokerConfigurationBuilder withDynamicConfiguration(boolean dynamicConfiguration)
    {
        this.invokerConfiguration.setDynamicConfiguration(dynamicConfiguration);
        return this;
    }

    public VanillaInvokerConfigurationBuilder withCaptureMetrics(boolean captureMetrics)
    {
        this.invokerConfiguration.setCaptureMetrics(captureMetrics);
        return this;
    }

    public VanillaInvokerConfigurationBuilder withSnapMetricsEvent(boolean snapMetricsEvent)
    {
        this.invokerConfiguration.setSnapEvent(snapMetricsEvent);
        return this;
    }

    /**
     * Return an instance of the built InvokerConfiguration
     * @return
     */
    public InvokerConfiguration build()
    {
        return this.invokerConfiguration;
    }
}
