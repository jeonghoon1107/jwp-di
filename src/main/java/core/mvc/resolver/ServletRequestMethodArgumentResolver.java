package core.mvc.resolver;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ServletRequestMethodArgumentResolver implements MethodArgumentResolver {
    @Override
    public boolean supports(MethodParameter parameter) {
        return ServletRequest.class.isAssignableFrom(parameter.getParameterType()) || HttpSession.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, HttpServletRequest request, HttpServletResponse response) {
        if (HttpSession.class.isAssignableFrom(methodParameter.getParameterType())) {
            return request.getSession();
        }
        return request;
    }
}
