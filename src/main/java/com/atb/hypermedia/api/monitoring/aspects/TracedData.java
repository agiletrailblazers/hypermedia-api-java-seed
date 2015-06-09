package com.atb.hypermedia.api.monitoring.aspects;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * For use on method parameters where the method
 * is annotated with {@link TraceRequest}
 * <p/>
 * By including this annotation on a parameter, the parameter name
 * and value will be added to the request trace before the method is called.
 * <p/>
 * This is equivalent to calling requestTracer.addTracingData at the beginning
 * of a method
 * <pre>
 *     {@code
 *     {@literal @}TraceRequest
 *      public void measureMePlease(@TracedData String traceMe) {
 *         // do something useful here
 *      }
 *     }
 * </pre>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface TracedData {

}
