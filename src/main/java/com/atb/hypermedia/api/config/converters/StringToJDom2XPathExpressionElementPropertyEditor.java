package com.atb.hypermedia.api.config.converters;

import org.jdom2.Element;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.springframework.stereotype.Component;

import java.beans.PropertyEditorSupport;

/**
 * Property editor used to convert a string into an instance of {@link XPathExpression<Element>}.
 *
 * This is used to annotate a variable of type {@link XPathExpression} with @Value annotation and
 * have Spring magically convert the string from the property file into an {@link XPathExpression}
 * at runtime.
 */
@Component
public class StringToJDom2XPathExpressionElementPropertyEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        XPathExpression<Element> xpath = XPathFactory.instance().compile(text, Filters.element());
        setValue(xpath);
    }
}
