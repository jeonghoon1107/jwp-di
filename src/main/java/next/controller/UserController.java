package next.controller;

import core.annotation.Inject;
import core.annotation.web.*;
import core.mvc.tobe.AbstractNewController;
import core.mvc.view.ModelAndView;
import next.dto.UserCreatedDto;
import next.dto.UserUpdatedDto;
import next.model.User;
import next.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value = "/users")
public class UserController extends AbstractNewController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Inject
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView list(HttpSession session) throws Exception {
        if (!UserSessionUtils.isLogined(session)) {
            return redirectView("redirect:/users/loginForm");
        }

        ModelAndView mav = jspView("/user/list");
        mav.addObject("users", userService.findAll());
        return mav;
    }

    @RequestMapping(value = "/{userId}/profile", method = RequestMethod.GET)
    public ModelAndView profile(@PathVariable String userId) throws Exception {
        ModelAndView mav = jspView("/user/profile");
        mav.addObject("user", userService.findById(userId));
        return mav;
    }

    @RequestMapping(value = "/form", method = RequestMethod.GET)
    public ModelAndView form() throws Exception {
        return jspView("/user/form");
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ModelAndView create(UserCreatedDto userCreatedDto) throws Exception {
        userService.save(userCreatedDto);
        return redirectView("redirect:/");
    }

    @RequestMapping(value = "/updateForm", method = RequestMethod.GET)
    public ModelAndView updateForm(@RequestParam String userId, HttpSession session) throws Exception {
        User user = userService.findById(userId);

        if (!UserSessionUtils.isSameUser(session, user)) {
            throw new IllegalStateException("다른 사용자의 정보를 수정할 수 없습니다.");
        }
        ModelAndView mav = jspView("/user/updateForm");
        mav.addObject("user", user);
        return mav;
    }

    @RequestMapping(value = "/{userId}/update", method = RequestMethod.POST)
    public ModelAndView update(@PathVariable String userId, UserUpdatedDto userUpdatedDto, HttpSession session) throws Exception {
        User user = userService.findById(userId);

        if (!UserSessionUtils.isSameUser(session, user)) {
            throw new IllegalStateException("다른 사용자의 정보를 수정할 수 없습니다.");
        }

        log.debug("Update User : {}", userUpdatedDto);

        userService.update(userId, userUpdatedDto);
        return redirectView("redirect:/");
    }

    @RequestMapping(value = "/loginForm", method = RequestMethod.GET)
    public ModelAndView loginForm() throws Exception {
        return jspView("/user/login");
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ModelAndView login(String userId, String password, HttpSession session) throws Exception {
        User user = userService.findById(userId);

        if (user == null) {
            throw new NullPointerException("사용자를 찾을 수 없습니다.");
        }

        if (user.matchPassword(password)) {
            session.setAttribute("user", user);
            return redirectView("redirect:/");
        } else {
            throw new IllegalStateException("비밀번호가 틀립니다.");
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ModelAndView logout(HttpSession session) throws Exception {
        session.removeAttribute("user");
        return redirectView("redirect:/");
    }
}
