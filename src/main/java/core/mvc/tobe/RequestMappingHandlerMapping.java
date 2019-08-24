package core.mvc.tobe;

import com.google.common.collect.Maps;
import core.annotation.web.Controller;
import core.annotation.web.RequestMapping;
import core.annotation.web.RequestMethod;
import core.mvc.HandlerMapping;
import core.mvc.resolver.*;
import core.web.context.WebApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RequestMappingHandlerMapping implements HandlerMapping {
    private static final Logger logger = LoggerFactory.getLogger(RequestMappingHandlerMapping.class);

    private Map<HandlerKey, HandlerExecution> handlerExecutions = Maps.newHashMap();
    private WebApplicationContext webApplicationContext;
    private List<MethodArgumentResolver> argumentResolvers;

    public RequestMappingHandlerMapping(WebApplicationContext webApplicationContext) {
        this.webApplicationContext = webApplicationContext;
    }

    @Override
    public void initialize() {
        argumentResolvers = Arrays.asList(new PathVariableArgumentResolver(), new RequestParamMethodArgumentResolver(),
                new RequestResponseBodyProcessor(), new ServletRequestMethodArgumentResolver(), new ServletResponseArgumentResolver(),
                new PrimitiveWrapperArgumentResolver(), new ModelArgumentResolver());

        Set<Class<?>> controllers = getControllers();

        controllers.forEach(clazz -> {
            Method[] methods = core.utils.ReflectionUtils.getMethodsWithAnnotation(clazz, RequestMapping.class);

            Arrays.stream(methods)
                    .forEach(method -> {
                        RequestMapping requestMapping = core.utils.ReflectionUtils.getAnnotationInMethod(method, RequestMapping.class);
                        RequestMethod[] requestMethods = getRequestMethods(requestMapping);
                        List<HandlerKey> handlerKeys = createHandlerKeys(clazz, requestMapping, requestMethods);
                        registerMappings(webApplicationContext.getBean(clazz), method, handlerKeys);
                    });

        });

        logger.info("Initialized requestMappingHandlerMapping!");
    }

    private Set<Class<?>> getControllers() {
        return webApplicationContext.getBeans().entrySet().stream()
                .filter(entry -> entry.getKey().isAnnotationPresent(Controller.class))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    /**
     * RequestMethod가 없으면 현재 정의된 모든 RequestMethod 형식을 지원한다
     *
     * @param requestMapping
     * @return
     */
    private RequestMethod[] getRequestMethods(RequestMapping requestMapping) {
        RequestMethod[] requestMethods = requestMapping.method();

        if (requestMethods.length == 0) {
            return RequestMethod.values();
        }

        return requestMethods;
    }

    private List<HandlerKey> createHandlerKeys(Class<?> controller, RequestMapping requestMapping, RequestMethod[] requestMethods) {
        return Arrays.stream(requestMethods)
                .map(method -> createHandlerKey(controller, requestMapping.value(), method))
                .collect(Collectors.toList());
    }

    private HandlerKey createHandlerKey(Class<?> controller, String path, RequestMethod method) {
        if (controller.isAnnotationPresent(RequestMapping.class)) {
            RequestMapping annotation = controller.getAnnotation(RequestMapping.class);
            String prefix = annotation.value();
            return new HandlerKey(prefix + path, method);
        }

        return new HandlerKey(path, method);
    }

    private void registerMappings(Object controller, Method method, List<HandlerKey> handlerKeys) {
        LocalVariableTableParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
        Parameter[] parameters = method.getParameters();

        List<MethodParameter> methodParameters = IntStream.range(0, parameterNames.length)
                .mapToObj(i -> new MethodParameter(parameterNames[i], parameters[i].getType(), parameters[i].getAnnotations(), method))
                .collect(Collectors.toList());

        handlerKeys.forEach(handlerKey -> handlerExecutions.put(handlerKey, new HandlerExecution(controller, method, methodParameters, argumentResolvers)));
    }


    public Object getHandler(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        RequestMethod rm = RequestMethod.valueOf(request.getMethod().toUpperCase());
        logger.debug("requestUri : {}, requestMethod : {}", requestUri, rm);

        Set<HandlerKey> handlerKeys = handlerExecutions.keySet();
        HandlerKey key = handlerKeys.stream()
                .filter(handlerKey -> handlerKey.isUrlMatch(request.getRequestURI(), rm))
                .findFirst()
                .orElse(new HandlerKey(requestUri, rm));

        return handlerExecutions.get(key);
    }
}
