package next.controller;

import core.annotation.Inject;
import core.annotation.web.*;
import core.mvc.view.JsonView;
import core.mvc.view.ModelAndView;
import next.dto.UserCreatedDto;
import next.dto.UserUpdatedDto;
import next.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value = "/api/users")
public class ApiUserController {
    private static final Logger log = LoggerFactory.getLogger(ApiUserController.class);

    private final UserService userService;

    @Inject
    public ApiUserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView create(@RequestBody UserCreatedDto createdDto, HttpServletResponse response) throws Exception {
        log.debug("Created User : {}", createdDto);

        userService.save(createdDto);

        response.setHeader("Location", "/api/users/" + createdDto.getUserId());
        response.setStatus(HttpStatus.CREATED.value());

        return new ModelAndView(new JsonView());
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ModelAndView show(@PathVariable String userId) throws Exception {
        log.debug("userId : {}", userId);

        ModelAndView mav = new ModelAndView(new JsonView());
        mav.addObject("user", userService.findById(userId));
        return mav;
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.PUT)
    public ModelAndView update(@PathVariable String userId, @RequestBody UserUpdatedDto updateDto) throws Exception {
        log.debug("userId : {}", userId);
        log.debug("Updated User : {}", updateDto);

        userService.update(userId, updateDto);

        return new ModelAndView(new JsonView());
    }
}
