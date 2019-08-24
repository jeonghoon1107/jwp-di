package next.controller;

import core.annotation.Inject;
import core.annotation.web.Controller;
import core.annotation.web.RequestMapping;
import core.annotation.web.RequestMethod;
import core.mvc.tobe.AbstractNewController;
import core.mvc.view.ModelAndView;
import next.service.QnaService;

@Controller
@RequestMapping(value = "/")
public class HomeController extends AbstractNewController {

    private final QnaService qnaService;

    @Inject
    public HomeController(QnaService qnaService) {
        this.qnaService = qnaService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView execute() throws Exception {
        return jspView("home").addObject("questions", qnaService.findAllQuestions());
    }
}
