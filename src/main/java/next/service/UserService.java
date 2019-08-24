package next.service;

import core.annotation.Inject;
import core.annotation.Service;
import next.dto.UserCreatedDto;
import next.dto.UserUpdatedDto;
import next.model.User;
import next.repository.JdbcUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final JdbcUserRepository jdbcUserRepository;

    @Inject
    public UserService(JdbcUserRepository jdbcUserRepository) {
        this.jdbcUserRepository = jdbcUserRepository;
    }


    public List<User> findAll() {
        return jdbcUserRepository.findAll();
    }

    public User findById(String userId) {
        return jdbcUserRepository.findById(userId);
    }

    public void update(String userId, UserUpdatedDto updateUser) {
        User user = jdbcUserRepository.findById(userId);
        user.update(updateUser);
        jdbcUserRepository.update(user);
    }

    public void save(UserCreatedDto createdDto) {
        User user = new User(createdDto.getUserId(), createdDto.getPassword(), createdDto.getName(), createdDto.getEmail());
        log.debug("User : {}", user);
        jdbcUserRepository.insert(user);
    }
}
