package com.atb.hypermedia.api.config.converters;

import org.hamcrest.CoreMatchers;
import org.jdom2.xpath.XPathExpression;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertThat;

public class StringToJDom2XPathExpressionElementPropertyEditorTest {

    StringToJDom2XPathExpressionElementPropertyEditor undertest;

    @Before
    public void setUp() throws Exception {
        undertest = new StringToJDom2XPathExpressionElementPropertyEditor();
    }

    @Test
    public void testSetAsTextString() {
        undertest.setAsText("/node/test");
        assertThat(undertest.getValue(), CoreMatchers.instanceOf(XPathExpression.class));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testSetAsTextString_Empty() {
        undertest.setAsText("");
    }
}
