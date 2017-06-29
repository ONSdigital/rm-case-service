package uk.gov.ons.ctp.response.casesvc.utility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.context.RetryContextSupport;
import org.springframework.util.ClassUtils;

import java.util.Collections;
import java.util.List;

/**
 * A RetryPolicy that will retry ONLY if an unchecked exception is thrown
 */
@Slf4j
public class CTPRetryPolicy implements RetryPolicy {

    private static final int DEFAULT_MAX_ATTEMPTS = 3;
    private static final String RUNTIME_EXCEPTION = "java.lang.RuntimeException";

    private volatile int maxAttempts;
    private volatile List<String> retryableExceptions;  // TODO Make it a List<Class<? extends Throwable>

    public CTPRetryPolicy() {
        this(DEFAULT_MAX_ATTEMPTS);
    }

    public CTPRetryPolicy(int maxAttempts) {
        this(maxAttempts, Collections.singletonList(RUNTIME_EXCEPTION));
    }

    public CTPRetryPolicy(int maxAttempts, List<String> retryableExceptions) {
        this.maxAttempts = maxAttempts;
        this.retryableExceptions = retryableExceptions;
    }

    /**
     * To decide if a retrial is required.
     *
     * @param context the RetryContext
     * @return true if retrial is required
     */
    public boolean canRetry(RetryContext context) {
        Throwable lastThrowable = context.getLastThrowable();
        return (lastThrowable == null || this.retryForException(lastThrowable)) && context.getRetryCount() <
                this.maxAttempts;
    }

    /**
     * Has to be there as per interface RetryPolicy
     * @param status the RetryContext
     */
    public void close(RetryContext status) {
    }

    /**
     * Identical implementation to SimpleRetryPolicy
     * @param context the RetryContext
     * @param throwable the Throwable
     */
    public void registerThrowable(RetryContext context, Throwable throwable) {
        CTPRetryContext simpleContext = (CTPRetryContext)context;
        simpleContext.registerThrowable(throwable);
    }

    /**
     * Identical implementation to SimpleRetryPolicy
     *
     * @param parent the RetryContext
     * @return the RetryContext
     */
    public RetryContext open(RetryContext parent) {
        return new CTPRetryContext(parent);
    }

    /**
     * To determine if a retry is required for the given Throwable
     *
     * @param ex the Throwable to check for retry
     * @return true if a retry is required
     */
    private boolean retryForException(Throwable ex) {
        try {
            for (String className : retryableExceptions) {
                if (Class.forName(className).isInstance(ex.getCause())) {
                    return true;
                }
            }
        } catch (ClassNotFoundException e) {
            log.error("msg {} - cause {}", e.getMessage(), e.getCause());
        }

        return false;
    }

    /**
     * Identical implementation to SimpleRetryPolicy
     * @return a representation string
     */
    public String toString() {
        return ClassUtils.getShortName(this.getClass()) + "[maxAttempts=" + this.maxAttempts + "]";
    }

    private static class CTPRetryContext extends RetryContextSupport {
        public CTPRetryContext(RetryContext parent) {
            super(parent);
        }
    }
}
