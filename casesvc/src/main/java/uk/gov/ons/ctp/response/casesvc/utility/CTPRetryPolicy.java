package uk.gov.ons.ctp.response.casesvc.utility;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.context.RetryContextSupport;
import org.springframework.util.ClassUtils;

/**
 * A Stateless RetryPolicy that will rety only if an unchecked exception is thrown
 *
 * We actually need a Stateful Retry as per
 * https://github.com/spring-projects/spring-retry/blob/master/README.md
 */
@Slf4j
public class CTPRetryPolicy implements RetryPolicy {
    @Getter @Setter private volatile int maxAttempts;

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
     * @param parent the RetryContext
     * @return the RetryContext
     */
    public RetryContext open(RetryContext parent) {
        return new CTPRetryContext(parent);
    }

    private boolean retryForException(Throwable ex) {
        return ex.getCause() instanceof RuntimeException;
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
