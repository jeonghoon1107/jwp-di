package core.mvc.tobe;

import core.mvc.view.JsonView;
import core.mvc.view.JspView;
import core.mvc.view.ModelAndView;
import core.mvc.view.RedirectView;

public abstract class AbstractNewController {
    protected ModelAndView jspView(String forwardUrl) {
        return new ModelAndView(new JspView(forwardUrl));
    }

    protected ModelAndView redirectView(String viewName) {
        return new ModelAndView(new RedirectView(viewName));
    }

    protected ModelAndView jsonView() {
        return new ModelAndView(new JsonView());
    }
}
