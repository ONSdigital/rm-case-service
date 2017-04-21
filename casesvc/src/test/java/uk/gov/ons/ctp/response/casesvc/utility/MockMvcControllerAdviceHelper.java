package uk.gov.ons.ctp.response.casesvc.utility;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

import java.lang.reflect.Method;

/**
 * Created by philippebrossier on 21/04/2017.
 */
public class MockMvcControllerAdviceHelper extends ExceptionHandlerExceptionResolver {

    private final Class exceptionHandlerClass;

    public MockMvcControllerAdviceHelper(Class exceptionHandlerClass) {
        super();
        getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        getMessageConverters().add(new Jaxb2RootElementHttpMessageConverter());
        this.exceptionHandlerClass = exceptionHandlerClass;
        afterPropertiesSet();
    }

    public static MockMvcControllerAdviceHelper mockAdviceFor(Class exceptionHandlerClass) {
        return new MockMvcControllerAdviceHelper(exceptionHandlerClass);
    }

    protected ServletInvocableHandlerMethod getExceptionHandlerMethod(HandlerMethod handlerMethod, Exception exception) {
        Object exceptionHandler = null;
        try {
            exceptionHandler = exceptionHandlerClass.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(String.format("Unable to instantiate exception handler %s", exceptionHandlerClass.getCanonicalName()), e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(String.format("Unable to instantiate exception handler %s", exceptionHandlerClass.getCanonicalName()), e);
        }
        Method method = new ExceptionHandlerMethodResolver(exceptionHandlerClass).resolveMethod(exception);
        return new ServletInvocableHandlerMethod(exceptionHandler, method);
    }
}
