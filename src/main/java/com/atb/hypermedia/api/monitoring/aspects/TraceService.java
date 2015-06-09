package com.atb.hypermedia.api.monitoring.aspects;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to indicate that a service call should be traced.  This annotation
 * is picked up by the {@link com.atb.hypermedia.api.monitoring.aspects.TraceAspect} aspect.
 * <p/>
 * This annotation can be used on ANY PUBLIC method on ANY SPRING BEAN.
 * <p/>
 * Before the method is called, a timing measurement will be added for the method
 * After the method completes, even on exception, another measurement will be added
 * <p/>
 * <pre>
 *     {@code
 *     {@literal @}TraceService
 *      public void measureMePlease() {
 *         // do something useful here
 *      }
 *     }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TraceService {

    /**
     * Allows the developer to override the name that is used as the measurement key
     * for request tracing.  This must be specified.
     * <pre>
     *     {@code
     *     {@literal @}TraceService("my.custom.trace.key")
     *      public void measureMePlease() {
     *         // do something useful here
     *      }
     *     }
     * </pre>
     *
     * @return The value that is to be the measurement key for the trace
     */
    String value();
}
