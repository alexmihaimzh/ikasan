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
package org.ikasan.testharness.flow.expectation.service;

import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.component.transformation.Translator;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.testharness.flow.Capture;
import org.ikasan.testharness.flow.comparator.ExpectationComparator;
import org.ikasan.testharness.flow.comparator.service.ComparatorService;
import org.ikasan.testharness.flow.expectation.model.TranslatorComponent;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

/**
 * Tests for the <code>OrderedExpectation</code> class.
 *
 * @author Ikasan Development Team
 */
public class OrderedExpectationTest
{
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
            setThreadingPolicy(new Synchroniser());
        }
    };

    /**
     * mocked capture
     */
    private final Capture<?> capture = mockery.mock(Capture.class, "capture");

    private final Capture<?> capture2 = mockery.mock(Capture.class, "capture2");

    /**
     * mocked flowElement
     */
    private final FlowElement flowElement = mockery.mock(FlowElement.class, "flowElement");

    private final FlowElement flowElement2 = mockery.mock(FlowElement.class, "flowElement2");

    /**
     * mocked comparatorService
     */
    @SuppressWarnings("unchecked") private final ComparatorService comparatorService = mockery
            .mock(ComparatorService.class, "ComparatorService");

    /**
     * mocked expectationComparator
     **/
    @SuppressWarnings("unchecked") private final ExpectationComparator expectationComparator = mockery
            .mock(ExpectationComparator.class, "expectationComparator");

    /**
     * mocked expectation
     */
    private final Object expectation = mockery.mock(Object.class, "ExpectationObject");

    /**
     * Sanity test of a default OrderedExpectation instance with a single
     * expectation to be matched using the default description.
     */
    @Test
    public void test_successfulDefaultOrderedExpectationWithSingleExpectationDefaultDescription()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the mocked actual flow element
                exactly(2).of(capture).getActual();
                will(returnValue(flowElement));

                // expected name
                exactly(2).of(flowElement).getComponentName();
                will(returnValue("one"));

                // expected implementation class
                exactly(4).of(flowElement).getFlowComponent();
                will(returnValue(new TestTranslator()));
            }
        });

        FlowExpectation flowExpectation = new OrderedExpectation();
        flowExpectation.expectation(new TranslatorComponent("one"));

        // test expectations satisfied
        flowExpectation.allSatisfied(singletonList(capture));

        mockery.assertIsSatisfied();
    }

    /**
     * Sanity test of a default OrderedExpectation instance with a single
     * expectation to be matched with a user defined description.
     */
    @Test
    public void test_successfulDefaultOrderedExpectationWithSingleExpectationUserDescription()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the mocked actual flow element
                exactly(2).of(capture).getActual();
                will(returnValue(flowElement));

                // expected name
                exactly(2).of(flowElement).getComponentName();
                will(returnValue("one"));

                // expected implementation class
                exactly(4).of(flowElement).getFlowComponent();
                will(returnValue(new TestTranslator()));
            }
        });

        FlowExpectation flowExpectation = new OrderedExpectation();
        flowExpectation.expectation(new TranslatorComponent("one"), "my test expectation description");

        // test expectations satisfied
        flowExpectation.allSatisfied(singletonList(capture));

        mockery.assertIsSatisfied();
    }

    /**
     * Sanity test of a default OrderedExpectation instance with a single
     * expectation to be ignored with default description.
     */
    @Test
    public void test_successfulDefaultOrderedExpectationWithSingleIgnoreExpectationDefaultDescription()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the mocked actual flow element
                exactly(2).of(capture).getActual();
                will(returnValue(flowElement));
            }
        });

        FlowExpectation flowExpectation = new OrderedExpectation();
        flowExpectation.ignore(new TranslatorComponent("one"));

        // test expectations satisfied
        flowExpectation.allSatisfied(singletonList(capture));

        mockery.assertIsSatisfied();
    }

    /**
     * Sanity test of a default OrderedExpectation instance with a single
     * expectation to be ignored with user description.
     */
    @Test
    public void test_successfulDefaultOrderedExpectationWithSingleIgnoreExpectationUserDescription()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the mocked actual flow element
                exactly(2).of(capture).getActual();
                will(returnValue(flowElement));
            }
        });

        FlowExpectation flowExpectation = new OrderedExpectation();
        flowExpectation.ignore(new TranslatorComponent("one"), "another description");

        // test expectations satisfied
        flowExpectation.allSatisfied(singletonList(capture));

        mockery.assertIsSatisfied();
    }

    /**
     * Sanity test of a default OrderedExpectation instance with a single
     * expectation and a user specified comparator passed explicitly for that
     * expectation. Use default expectation description.
     */
    @Test
    public void test_successfulDefaultOrderedExpectationWithSingleExpectationAndUserComparatorDefaultDescription()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the mocked actual flow element
                exactly(2).of(capture).getActual();
                will(returnValue("one"));
            }
        });

        FlowExpectation flowExpectation = new OrderedExpectation();
        flowExpectation.expectation("one", new TestComparator());

        // test expectations satisfied
        flowExpectation.allSatisfied(singletonList(capture));

        mockery.assertIsSatisfied();
    }

    /**
     * Sanity test of a default OrderedExpectation instance with a single
     * expectation and a user specified comparator passed explicitly for that
     * expectation. Use User description.
     */
    @Test
    public void test_successfulDefaultOrderedExpectationWithSingleExpectationAndUserComparatorUserDescription()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the mocked actual flow element
                exactly(2).of(capture).getActual();
                will(returnValue("one"));
            }
        });

        FlowExpectation flowExpectation = new OrderedExpectation();
        flowExpectation.expectation("one", new TestComparator(), "another expectation description");

        // test expectations satisfied
        flowExpectation.allSatisfied(singletonList(capture));

        mockery.assertIsSatisfied();
    }

    /**
     * Sanity test of an OrderedExpectation instance with an alternate ComparatorService.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void test_successfulOrderedExpectationWithAlternateComparatorService()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the mocked actual flow element
                exactly(2).of(capture).getActual();
                will(returnValue("one"));

                exactly(1).of(comparatorService).getComparator(with(any(Object.class)));
                will(returnValue(expectationComparator));

                exactly(2).of(expectationComparator).compare(with(any(Object.class)), with(any(Object.class)));
            }
        });

        FlowExpectation flowExpectation = new OrderedExpectation(comparatorService);
        flowExpectation.expectation(expectation);

        // test expectations satisfied
        flowExpectation.allSatisfied(singletonList(capture));

        mockery.assertIsSatisfied();
    }

    @Test
    public void test_passWhenMultipleInOrder()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the mocked actual flow element
                exactly(2).of(capture).getActual();
                will(returnValue(flowElement));
                exactly(2).of(capture2).getActual();
                will(returnValue(flowElement2));
                // expected name
                exactly(2).of(flowElement2).getComponentName();
                will(returnValue("two"));
                exactly(2).of(flowElement).getComponentName();
                will(returnValue("one"));
                // expected implementation class
                exactly(4).of(flowElement).getFlowComponent();
                will(returnValue(new OrderedExpectationTest.TestTranslator()));
                // expected implementation class
                exactly(4).of(flowElement2).getFlowComponent();
                will(returnValue(new OrderedExpectationTest.TestTranslator()));
            }
        });

        FlowExpectation flowExpectation = new OrderedExpectation();
        flowExpectation.expectation(new TranslatorComponent("one"), "one");
        flowExpectation.expectation(new TranslatorComponent("two"), "two");

        // test expectations satisfied
        flowExpectation.allSatisfied(asList(capture, capture2));

        mockery.assertIsSatisfied();
    }

    @Test
    public void test_successWhenNoCapturesOrExpectations()
    {
        FlowExpectation flowExpectation = new OrderedExpectation();

        // test expectations satisfied
        flowExpectation.allSatisfied(emptyList());

        mockery.assertIsSatisfied();
    }

    @Test(expected = AssertionError.class)
    public void test_failWhenNoExpectationsButCaptures()
    {
        mockery.checking(new Expectations()
        {
            {
                // get the mocked actual flow element
                exactly(1).of(capture).getActual();
                will(returnValue(flowElement));
            }
        });

        FlowExpectation flowExpectation = new OrderedExpectation();

        // test expectations satisfied
        flowExpectation.allSatisfied(singletonList(capture));

        mockery.assertIsSatisfied();
    }

    @Test(expected = AssertionError.class)
    public void test_failsWhenNoCapturesButExpectations()
    {
        FlowExpectation flowExpectation = new OrderedExpectation();
        flowExpectation.ignore(new TranslatorComponent("one"));

        // test expectations satisfied
        flowExpectation.allSatisfied(emptyList());

        mockery.assertIsSatisfied();
    }

    @Test(expected = AssertionError.class)
    public void test_failsWhenOutOfOrder()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the mocked actual flow element
                exactly(1).of(capture).getActual();
                will(returnValue(flowElement));
                exactly(3).of(capture2).getActual();
                will(returnValue(flowElement2));
                // expected name
                exactly(3).of(flowElement2).getComponentName();
                will(returnValue("two"));
                exactly(1).of(flowElement).getComponentName();
                will(returnValue("one"));
                // expected implementation class
                exactly(2).of(flowElement).getFlowComponent();
                will(returnValue(new OrderedExpectationTest.TestTranslator()));
                // expected implementation class
                exactly(2).of(flowElement2).getFlowComponent();
                will(returnValue(new OrderedExpectationTest.TestTranslator()));
            }
        });

        FlowExpectation flowExpectation = new OrderedExpectation();
        flowExpectation.expectation(new TranslatorComponent("one"), "one");
        flowExpectation.expectation(new TranslatorComponent("two"), "two");

        // test expectations satisfied
        flowExpectation.allSatisfied(asList(capture2, capture));

        mockery.assertIsSatisfied();
    }

    @Test(expected = AssertionError.class)
    public void test_failWhenMissingInvocation()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the mocked actual flow element
                exactly(2).of(capture).getActual();
                will(returnValue(flowElement));
                exactly(1).of(flowElement).getComponentName();
                will(returnValue("one"));
                // expected implementation class
                exactly(2).of(flowElement).getFlowComponent();
                will(returnValue(new OrderedExpectationTest.TestTranslator()));
            }
        });

        FlowExpectation flowExpectation = new OrderedExpectation();
        flowExpectation.expectation(new TranslatorComponent("one"), "one");
        flowExpectation.expectation(new TranslatorComponent("two"), "two");

        // test expectations satisfied
        flowExpectation.allSatisfied(singletonList(capture));

        mockery.assertIsSatisfied();
    }

    @Test(expected = AssertionError.class)
    public void test_failWhenMissingExpectation()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the mocked actual flow element
                exactly(2).of(capture).getActual();
                will(returnValue(flowElement));
                exactly(3).of(capture2).getActual();
                will(returnValue(flowElement2));
                // expected name
                exactly(2).of(flowElement2).getComponentName();
                will(returnValue("two"));
                exactly(1).of(flowElement).getComponentName();
                will(returnValue("one"));
                // expected implementation class
                exactly(2).of(flowElement).getFlowComponent();
                will(returnValue(new OrderedExpectationTest.TestTranslator()));
                // expected implementation class
                exactly(2).of(flowElement2).getFlowComponent();
                will(returnValue(new OrderedExpectationTest.TestTranslator()));
            }
        });

        FlowExpectation flowExpectation = new OrderedExpectation();
        flowExpectation.expectation(new TranslatorComponent("one"), "one");

        // test expectations satisfied
        flowExpectation.allSatisfied(asList(capture, capture2));

        mockery.assertIsSatisfied();
    }

    @Test(expected = AssertionError.class)
    public void test_failWhenMissingExpectationAndInvocation()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the mocked actual flow element
                exactly(2).of(capture).getActual();
                will(returnValue(flowElement));
                exactly(3).of(capture2).getActual();
                will(returnValue(flowElement2));
                // expected name
                exactly(2).of(flowElement2).getComponentName();
                will(returnValue("two"));
                exactly(1).of(flowElement).getComponentName();
                will(returnValue("one"));
                // expected implementation class
                exactly(2).of(flowElement).getFlowComponent();
                will(returnValue(new OrderedExpectationTest.TestTranslator()));
                // expected implementation class
                exactly(2).of(flowElement2).getFlowComponent();
                will(returnValue(new OrderedExpectationTest.TestTranslator()));
            }
        });

        FlowExpectation flowExpectation = new OrderedExpectation();
        flowExpectation.expectation(new TranslatorComponent("one"), "one");
        flowExpectation.expectation(new TranslatorComponent("three"), "three");

        // test expectations satisfied
        flowExpectation.allSatisfied(asList(capture, capture2));

        mockery.assertIsSatisfied();
    }

    /**
     * Sanity test of a default OrderedExpectation instance with a single
     * expectation and a user specified comparator, but based on an incorrect
     * class comparator parameter type resulting in a ClassCastException.
     */
    @Test(expected = ClassCastException.class)
    public void test_failedDefaultOrderedExpectationWithClassCastException()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the mocked actual flow element
                exactly(3).of(capture).getActual();
                will(returnValue(flowElement));
            }
        });

        FlowExpectation flowExpectation = new OrderedExpectation();
        flowExpectation.expectation(new TranslatorComponent("one"), new TestComparator());

        // test expectations satisfied
        flowExpectation.allSatisfied(singletonList(capture));

        mockery.assertIsSatisfied();
    }

    /**
     * Simple implementation of a Transformer component for testing.
     *
     * @author Ikasan Development Team
     */
    private class TestTranslator implements Translator<StringBuilder>
    {
        public void translate(StringBuilder payload) throws TransformationException
        {
            // do nothing
        }
    }

    /**
     * Simple implementation of a TestComparator for testing.
     *
     * @author Ikasan Development Team
     */
    private class TestComparator implements ExpectationComparator<String, String>
    {
        public void compare(String expected, String actual)
        {
            Assert.assertEquals(expected, actual);
        }
    }
}    

