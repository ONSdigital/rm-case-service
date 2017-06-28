package uk.gov.ons.ctp.response.casesvc.utility;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.context.RetryContextSupport;
import org.springframework.util.ClassUtils;

/**
 * A Stateless RetryPolicy
 *
 * We actually need a Stateful Retry as per
 * https://github.com/spring-projects/spring-retry/blob/master/README.md
 */
@Slf4j
public class CTPRetryPolicy implements RetryPolicy {
    @Getter @Setter private volatile int maxAttempts;

    public boolean canRetry(RetryContext context) {
        Throwable lastThrowable = context.getLastThrowable();
        return (lastThrowable == null || this.retryForException(lastThrowable)) && context.getRetryCount() < this.maxAttempts;
    }

    public void close(RetryContext status) {
    }

    public void registerThrowable(RetryContext context, Throwable throwable) {
        CTPRetryPolicy.SimpleRetryContext simpleContext = (CTPRetryPolicy.SimpleRetryContext)context;
        simpleContext.registerThrowable(throwable);
    }

    public RetryContext open(RetryContext parent) {
        return new CTPRetryPolicy.SimpleRetryContext(parent);
    }

    private boolean retryForException(Throwable ex) {
        return ex.getCause() instanceof RuntimeException;
    }

    public String toString() {
        return ClassUtils.getShortName(this.getClass()) + "[maxAttempts=" + this.maxAttempts + "]";
    }

    private static class SimpleRetryContext extends RetryContextSupport {
        public SimpleRetryContext(RetryContext parent) {
            super(parent);
        }
    }
}
