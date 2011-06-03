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
package org.ikasan.sample.jmsDrivenPriceSrc.flow;

import java.util.Hashtable;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.ikasan.consumer.jms.GenericJmsConsumer;
import org.ikasan.consumer.jms.GenericJmsConsumerConfiguration;
import org.ikasan.consumer.jms.GenericJmsProducer;
import org.ikasan.consumer.jms.GenericJmsProducerConfiguration;
import org.ikasan.flow.event.DefaultReplicationFactory;
import org.ikasan.flow.event.FlowEventFactory;
import org.ikasan.flow.visitorPattern.DefaultFlowConfiguration;
import org.ikasan.flow.visitorPattern.FlowConfiguration;
import org.ikasan.flow.visitorPattern.FlowElementImpl;
import org.ikasan.flow.visitorPattern.VisitingFlowElementInvoker;
import org.ikasan.flow.visitorPattern.VisitingInvokerFlow;
import org.ikasan.recovery.RecoveryManagerFactory;
import org.ikasan.sample.jmsDrivenPriceSrc.component.converter.PriceConverter;
import org.ikasan.sample.jmsDrivenPriceSrc.component.endpoint.PriceLoggerProducer;
import org.ikasan.scheduler.SchedulerFactory;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowElementInvoker;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.flow.FlowEventListener;
import org.ikasan.spec.recovery.RecoveryManager;
import org.ikasan.spec.configuration.ConfigurationService;

/**
 * 
 * @author Ikasan Development Team
 */
public class PriceFlowFactory
{
    protected static String INITIAL_CONTEXT_FACTORY = "java.naming.factory.initial";

    protected static String PROVIDER_URL = "java.naming.provider.url";

    protected static String FACTORY_URL_PKGS = "java.naming.factory.url.pkgs";

    private String initialContextFactory = "org.apache.activemq.jndi.ActiveMQInitialContextFactory";
    
    private String providerUrl = "vm://localhost?broker.persistent=false";

    private String factoryUrl = "org.apache.activemq.jndi.ActiveMQInitialContextFactory";

    private String destinationName = "dynamicTopics/testTopic";

    private String flowName;

    private String moduleName;

    private ConfigurationService configurationService;

    private ConfigurationManagement configurationManagement;

    private EventFactory<FlowEvent<?, ?>> flowEventFactory = new FlowEventFactory();

    private RecoveryManagerFactory recoveryManagerFactory;

    private FlowEventListener flowEventListener;
    
    public PriceFlowFactory(String flowName, String moduleName, ConfigurationService configurationService, 
            ConfigurationManagement configurationManagement, FlowEventListener flowEventListener)
    {
        this.flowName = flowName;
        this.moduleName = moduleName;
        this.configurationService = configurationService;
        this.configurationManagement = configurationManagement;
        this.recoveryManagerFactory = new RecoveryManagerFactory(SchedulerFactory.getInstance().getScheduler());
        this.flowEventListener = flowEventListener;
    }

    public Flow createJmsDrivenFlow()
    {
        //Producer producer = createProducer("jmsDrivenProducerId");
        Producer<?> producer = new PriceLoggerProducer();

        FlowElement producerFlowElement = new FlowElementImpl("priceProducer", producer);

        FlowElement converterFlowElement = new FlowElementImpl("priceConverter", new PriceConverter(), producerFlowElement);

        Consumer consumer = createConsumer("jmsDrivenConsumerId");
        FlowElement<Consumer> consumerFlowElement = new FlowElementImpl("jmsDrivenConsumer", consumer, converterFlowElement);

        // flow configuration wiring
        FlowConfiguration flowConfiguration = new DefaultFlowConfiguration(consumerFlowElement, this.configurationService);

        // iterator over each flow element
        FlowElementInvoker flowElementInvoker = new VisitingFlowElementInvoker(DefaultReplicationFactory.getInstance());
        flowElementInvoker.setFlowEventListener(flowEventListener);
        
        RecoveryManager recoveryManager = recoveryManagerFactory.getRecoveryManager(flowName, moduleName, consumer);
        // container for the complete flow
        return new VisitingInvokerFlow(flowName, moduleName, flowConfiguration, flowElementInvoker, recoveryManager);
    }

    protected Consumer<?> createConsumer(String configuredResourceId)
    {
        GenericJmsConsumerConfiguration configuration = new GenericJmsConsumerConfiguration();
        // create consumer component
        InitialContext context = createContext();
        ConnectionFactory connectionFactory = getConnectionFactory(context);
        Object destination = getDestination(context, destinationName);
        
        Consumer<?> consumer = new GenericJmsConsumer((ConnectionFactory)connectionFactory, (Destination)destination, flowEventFactory);
        ConfiguredResource configuredResource = (ConfiguredResource) consumer;
        configuredResource.setConfiguredResourceId(configuredResourceId);
        configuredResource.setConfiguration(configuration);
        configurationManagement.saveConfiguration(configurationManagement.createConfiguration(consumer));
        return consumer;
    }

    public Producer<?> createProducer(String configuredResourceId)
    {
        GenericJmsProducerConfiguration configuration = new GenericJmsProducerConfiguration();
        // create producer component
        InitialContext context = createContext();
        ConnectionFactory connectionFactory = getConnectionFactory(context);
        Object destination = getDestination(context, destinationName);
        
        Producer<?> producer = new GenericJmsProducer((ConnectionFactory)connectionFactory, (Destination)destination);
        ConfiguredResource configuredResource = (ConfiguredResource) producer;
        configuredResource.setConfiguredResourceId(configuredResourceId);
        configuredResource.setConfiguration(configuration);
        configurationManagement.saveConfiguration(configurationManagement.createConfiguration(producer));
        return producer;
    }

    public Message createTextMessage(String content) throws JMSException
    {
        InitialContext context = createContext();
        ConnectionFactory connectionFactory = getConnectionFactory(context);
        Session session = connectionFactory.createConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
        return session.createTextMessage(content);
    }

    private InitialContext createContext()
    {
        Hashtable<String,String> hashTable = new Hashtable<String,String>();
        hashTable.put(INITIAL_CONTEXT_FACTORY, initialContextFactory);
        hashTable.put(PROVIDER_URL, providerUrl);
        hashTable.put(FACTORY_URL_PKGS, factoryUrl);

        try
        {
            return new InitialContext(hashTable);
        }
        catch(NamingException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    private ConnectionFactory getConnectionFactory(InitialContext context)
    {
        try
        {
            return (ConnectionFactory)context.lookup("ConnectionFactory");
        }
        catch(NamingException e)
        {
            throw new RuntimeException(e);
        }
    }

    private Destination getDestination(InitialContext context, String name)
    {
        try
        {
            return (Destination)context.lookup(name);
        }
        catch(NamingException e)
        {
            throw new RuntimeException(e);
        }
    }
}
