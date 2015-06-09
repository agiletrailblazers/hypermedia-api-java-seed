package com.atb.hypermedia.api.utilities;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FuturesUtilsTest {

    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Mock
    private Future<String> future;

    @Test
    public void getWithFallBackSuccessTest() throws ExecutionException, InterruptedException {
        when(future.get()).thenReturn(new String("success"));
        assertEquals(FuturesUtils.getWithFallback(future, new String("success")), new String("success"));
    }

    @Test
    public void getWithFallBackThrowsInterruptedExceptionTest() throws Exception {
        when(future.get()).thenThrow(new InterruptedException("blah blah blah"));
        assertEquals(FuturesUtils.getWithFallback(future, new String("success")), new String("success"));
    }

    @Test
    public void getWithFallBackThrowsExecutionExceptionTest() throws Exception {
        when(future.get()).thenThrow(new ExecutionException(new Exception("blah blah blah")));
        assertEquals(FuturesUtils.getWithFallback(future, new String("success")), new String("success"));
    }

    @Test
    public void testConstructorToMakeCoberturaHappy() {
        assertNotNull(new FuturesUtils());
    }

    @Test
    public void getWithPropagationError() throws Exception {
        when(future.get()).thenReturn("foo");
        assertEquals("foo", FuturesUtils.getWithPropagatedException(future));
    }

    @Test
    public void getWithPropagatedErrorThrowsExecutionCause() throws Exception {
        when(future.get()).thenThrow(new ExecutionException(new Exception("foo")));

        expected.expect(Exception.class);
        expected.expectMessage("foo");

        FuturesUtils.getWithPropagatedException(future);
    }

    @Test
    public void getWithPropagatedErrorThrowsWrappedCheckedExecutionCause() throws Exception {
        when(future.get()).thenThrow(new ExecutionException(new Exception("foo")));

        expected.expect(RuntimeException.class);
        expected.expectMessage("foo");

        FuturesUtils.getWithPropagatedException(future);
    }

    @Test
    public void getWithPropagatedErrorWrapsCheckedException() throws Exception {
        when(future.get()).thenThrow(new InterruptedException("checked"));

        expected.expect(RuntimeException.class);
        FuturesUtils.getWithPropagatedException(future);
    }

}
