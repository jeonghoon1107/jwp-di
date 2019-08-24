package core.mvc.tobe;

import core.mvc.resolver.MethodParameter;
import core.mvc.view.ModelAndView;
import core.mvc.exception.TypeMismatchException;
import core.mvc.resolver.MethodArgumentResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class HandlerExecution {
    private static final Logger logger = LoggerFactory.getLogger(HandlerExecution.class);

    private Object declaredObject;
    private Method method;
    private List<MethodParameter> methodParameters;
    private List<MethodArgumentResolver> argumentResolvers;

    public HandlerExecution(Object declaredObject, Method method, List<MethodParameter> methodParameters, List<MethodArgumentResolver> argumentResolvers) {
        this.declaredObject = declaredObject;
        this.method = method;
        this.methodParameters = methodParameters;
        this.argumentResolvers = argumentResolvers;
    }

    public ModelAndView handle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<MethodParameter> methodParameters = this.methodParameters;

        Object[] parameters = methodParameters.stream()
                .map(methodParameter -> resolveArgument(request, response, methodParameter))
                .toArray();
        try {
            return (ModelAndView) method.invoke(declaredObject, parameters);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            logger.error("{} method invoke fail. error message : {}", method, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private Object resolveArgument(HttpServletRequest request, HttpServletResponse response, MethodParameter methodParameter) {
        return argumentResolvers.stream()
                .filter(argumentResolver -> argumentResolver.supports(methodParameter))
                .findFirst()
                .map(argumentResolver -> argumentResolver.resolveArgument(methodParameter, request, response))
                .orElseThrow(TypeMismatchException::new);
    }
}
