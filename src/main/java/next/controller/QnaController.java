package next.controller;

import core.annotation.Inject;
import core.annotation.web.Controller;
import core.annotation.web.PathVariable;
import core.annotation.web.RequestMapping;
import core.annotation.web.RequestMethod;
import core.mvc.tobe.AbstractNewController;
import core.mvc.view.ModelAndView;
import next.CannotDeleteException;
import next.dto.QuestionCreatedDto;
import next.dto.QuestionUpdatedDto;
import next.model.Answer;
import next.model.Question;
import next.model.User;
import next.repository.JdbcAnswerRepository;
import next.repository.JdbcQuestionRepository;
import next.service.QnaService;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/qna")
public class QnaController extends AbstractNewController {

    private final QnaService qnaService;
    private final JdbcQuestionRepository jdbcQuestionRepository;
    private final JdbcAnswerRepository jdbcAnswerRepository;

    @Inject
    public QnaController(QnaService qnaService, JdbcQuestionRepository jdbcQuestionRepository, JdbcAnswerRepository jdbcAnswerRepository) {
        this.qnaService = qnaService;
        this.jdbcQuestionRepository = jdbcQuestionRepository;
        this.jdbcAnswerRepository = jdbcAnswerRepository;
    }

    @RequestMapping(value = "/form", method = RequestMethod.GET)
    public ModelAndView createForm(HttpSession session) throws Exception {
        if (!UserSessionUtils.isLogined(session)) {
            return redirectView("redirect:/users/loginForm");
        }
        return jspView("/qna/form");
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ModelAndView create(QuestionCreatedDto questionCreatedDto, HttpSession session) throws Exception {
        if (!UserSessionUtils.isLogined(session)) {
            return redirectView("redirect:/users/loginForm");
        }

        User user = UserSessionUtils.getUserFromSession(session);
        qnaService.saveQuestion(user, questionCreatedDto);

        return redirectView("redirect:/");
    }

    @RequestMapping(value = "/{questionId}", method = RequestMethod.GET)
    public ModelAndView show(@PathVariable long questionId) throws Exception {

        Question question = qnaService.findQuestionById(questionId);
        List<Answer> answers = qnaService.findAllByQuestionId(questionId);

        ModelAndView mav = jspView("/qna/show");
        mav.addObject("question", question);
        mav.addObject("answers", answers);
        return mav;
    }

    @RequestMapping(value = "/{questionId}/update", method = RequestMethod.GET)
    public ModelAndView updateForm(@PathVariable long questionId, HttpSession session) throws Exception {
        if (!UserSessionUtils.isLogined(session)) {
            return redirectView("redirect:/users/loginForm");
        }

        Question question = qnaService.findQuestionById(questionId);

        if (!question.isSameUser(UserSessionUtils.getUserFromSession(session))) {
            throw new IllegalStateException("다른 사용자가 쓴 글을 수정할 수 없습니다.");
        }
        return jspView("/qna/update").addObject("question", question);
    }

    @RequestMapping(value = "/{questionId}/update", method = RequestMethod.POST)
    public ModelAndView update(@PathVariable long questionId, QuestionUpdatedDto questionUpdatedDto, HttpSession session) throws Exception {
        if (!UserSessionUtils.isLogined(session)) {
            return redirectView("redirect:/users/loginForm");
        }

        Question question = qnaService.findQuestionById(questionId);

        if (!question.isSameUser(UserSessionUtils.getUserFromSession(session))) {
            throw new IllegalStateException("다른 사용자가 쓴 글을 수정할 수 없습니다.");
        }

        qnaService.updateQuestion(question, questionUpdatedDto);
        return redirectView("redirect:/");
    }

    @RequestMapping(value = "/{questionId}/delete")
    public ModelAndView delete(@PathVariable long questionId, HttpSession session) throws Exception {
        if (!UserSessionUtils.isLogined(session)) {
            return redirectView("redirect:/users/loginForm");
        }

        try {
            qnaService.deleteQuestion(questionId, UserSessionUtils.getUserFromSession(session));
            return redirectView("redirect:/");
        } catch (CannotDeleteException e) {
            return jspView("show").addObject("question", qnaService.findQuestionById(questionId))
                    .addObject("answers", qnaService.findAllByQuestionId(questionId))
                    .addObject("errorMessage", e.getMessage());
        }
    }
}
