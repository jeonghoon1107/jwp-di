package next.controller;

import core.annotation.Inject;
import core.annotation.web.Controller;
import core.annotation.web.RequestMapping;
import core.annotation.web.RequestMethod;
import core.jdbc.DataAccessException;
import core.mvc.tobe.AbstractNewController;
import core.mvc.view.ModelAndView;
import next.dto.AnswerCreatedDto;
import next.model.Answer;
import next.model.Result;
import next.model.User;
import next.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value = "/api/qna")
public class ApiQnaController extends AbstractNewController {
    private static final Logger log = LoggerFactory.getLogger(ApiQnaController.class);

    private final QnaService qnaService;

    @Inject
    public ApiQnaController(QnaService qnaService) {
        this.qnaService = qnaService;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ModelAndView questions() throws Exception {
        return jsonView().addObject("questions", qnaService.findAllQuestions());
    }

    @RequestMapping(value = "/addAnswer", method = RequestMethod.POST)
    public ModelAndView addAnswer(AnswerCreatedDto answerCreatedDto, HttpSession session) throws Exception {
        if (!UserSessionUtils.isLogined(session)) {
            return jsonView().addObject("result", Result.fail("Login is required"));
        }

        User user = UserSessionUtils.getUserFromSession(session);

        Answer savedAnswer = qnaService.saveAnswer(user.getUserId(), answerCreatedDto);

        return jsonView().addObject("answer", savedAnswer).addObject("result", Result.ok());
    }

    @RequestMapping(value = "/deleteAnswer", method = RequestMethod.POST)
    public ModelAndView deleteAnswer(Long answerId) throws Exception {

        ModelAndView mav = jsonView();
        try {
            qnaService.deleteAnswer(answerId);
            mav.addObject("result", Result.ok());
        } catch (DataAccessException e) {
            mav.addObject("result", Result.fail(e.getMessage()));
        }
        return mav;
    }
}
