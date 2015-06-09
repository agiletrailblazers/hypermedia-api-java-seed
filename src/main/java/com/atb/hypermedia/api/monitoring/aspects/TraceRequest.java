package com.atb.hypermedia.api.monitoring.aspects;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to indicate that a Controller method should be traced.  This annotation
 * is picked up by the {@link com.atb.hypermedia.api.monitoring.aspects.TraceAspect} aspect.
 * <p/>
 * This method should be used ONLY on Controllers.  The annotation will be
 * disregarded if it is on anything another than a Controller.
 * <p/>
 * Before the method is called, the request tracer will be called to start tracing.
 * After the method completes, even on exception, the request tracer will stop tracing.
 * <p/>
 * <pre>
 *     {@code
 *     {@literal @}TraceRequest
 *      public void measureMePlease() {
 *         // do something useful here
 *      }
 *     }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TraceRequest {

    /**
     * Allows the developer to override the name that is used as the measurement key
     * for request tracing.  This must be provided.
     * <pre>
     *     {@code
     *     {@literal @}TraceRequest("my.custom.trace.key")
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
