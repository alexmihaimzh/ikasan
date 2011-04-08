/*
 * $Id$
 * $URL$
 *
 * ====================================================================
 *
 * Copyright (c) 2000-20010 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.recoveryManager;

import junit.framework.Assert;

import org.ikasan.exceptionResolver.ExceptionResolver;
import org.ikasan.recovery.ScheduledRecoveryManager;
import org.ikasan.recovery.ScheduledRecoveryManagerFactory;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.recoveryManager.RecoveryManagerFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

/**
 * This test class supports the <code>ScheduledRecoveryManagerFactory</code> class.
 * 
 * @author Ikasan Development Team
 */
public class ScheduledRecoveryManagerFactoryTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    
    /** Mock consumer flowElement */
    final Consumer<?> consumer = mockery.mock(Consumer.class, "mockConsumer");

    /** Mock exception resolver */
    final ExceptionResolver exceptionResolver = mockery.mock(ExceptionResolver.class, "mockExceptionResolver");

    /** Mock scheduler */
    final Scheduler scheduler = mockery.mock(Scheduler.class, "mockScheduler");

    /**
     * Test failed constructor due to null exception resolver.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullExceptionResolver()
    {
        new ScheduledRecoveryManagerFactory(null, null);
    }

    /**
     * Test failed constructor due to null scheduler.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullScheduler()
    {
        new ScheduledRecoveryManagerFactory(exceptionResolver, null);
    }

    /**
     * Test successful get recovery instantiation.
     * @throws SchedulerException 
     */
    @Test
    public void test_successful_getRecovery_instance() throws SchedulerException
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // start the scheduler on instantiation of the recovery manager
                exactly(1).of(scheduler).start();
            }
        });

        RecoveryManagerFactory<Consumer<?>> recoveryManagerFactory = new ScheduledRecoveryManagerFactory(exceptionResolver, scheduler);
        Assert.assertTrue(recoveryManagerFactory.getRecoveryManager("flowName", "moduleName", consumer) instanceof ScheduledRecoveryManager);
    }

    /**
     * Test failed recovery manager instantiation
     * @throws SchedulerException 
     */
    @Test(expected = RuntimeException.class)
    public void test_failed_getRecovery_instance() throws SchedulerException
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // start the scheduler on instantiation of the recovery manager
                exactly(1).of(scheduler).start();
                will(throwException(new SchedulerException()));
            }
        });

        new ScheduledRecoveryManagerFactory(exceptionResolver, scheduler);
    }
}
