package next.service;

import core.annotation.Inject;
import core.annotation.Service;
import next.CannotDeleteException;
import next.dto.AnswerCreatedDto;
import next.dto.QuestionCreatedDto;
import next.dto.QuestionUpdatedDto;
import next.model.Answer;
import next.model.Question;
import next.model.User;
import next.repository.JdbcAnswerRepository;
import next.repository.JdbcQuestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class QnaService {

    private static final Logger log = LoggerFactory.getLogger(QnaService.class);

    private final JdbcAnswerRepository jdbcAnswerRepository;
    private final JdbcQuestionRepository jdbcQuestionRepository;

    @Inject
    public QnaService(JdbcAnswerRepository jdbcAnswerRepository, JdbcQuestionRepository jdbcQuestionRepository) {
        this.jdbcAnswerRepository = jdbcAnswerRepository;
        this.jdbcQuestionRepository = jdbcQuestionRepository;
    }

    public Question findQuestionById(long questionId) {
        return jdbcQuestionRepository.findById(questionId);
    }

    public List<Answer> findAllByQuestionId(long questionId) {
        return jdbcAnswerRepository.findAllByQuestionId(questionId);
    }

    public void deleteQuestion(long questionId, User user) throws CannotDeleteException {
        Question question = jdbcQuestionRepository.findById(questionId);
        if (question == null) {
            throw new CannotDeleteException("존재하지 않는 질문입니다.");
        }

        if (!question.isSameUser(user)) {
            throw new CannotDeleteException("다른 사용자가 쓴 글을 삭제할 수 없습니다.");
        }

        List<Answer> answers = jdbcAnswerRepository.findAllByQuestionId(questionId);
        if (answers.isEmpty()) {
            jdbcQuestionRepository.delete(questionId);
            return;
        }

        boolean canDelete = true;
        for (Answer answer : answers) {
            String writer = question.getWriter();
            if (!writer.equals(answer.getWriter())) {
                canDelete = false;
                break;
            }
        }

        if (!canDelete) {
            throw new CannotDeleteException("다른 사용자가 추가한 댓글이 존재해 삭제할 수 없습니다.");
        }

        jdbcQuestionRepository.delete(questionId);
    }

    public List<Question> findAllQuestions() {
        return jdbcQuestionRepository.findAll();
    }

    public void saveQuestion(User user, QuestionCreatedDto questionCreatedDto) {
        Question question = new Question(user.getUserId(), questionCreatedDto.getTitle(), questionCreatedDto.getContents());
        jdbcQuestionRepository.insert(question);
    }

    public void updateQuestion(Question question, QuestionUpdatedDto questionUpdatedDto) {
        Question newQuestion = new Question(question.getWriter(), questionUpdatedDto.getTitle(), questionUpdatedDto.getContents());
        question.update(newQuestion);
        jdbcQuestionRepository.update(question);
    }

    public void deleteAnswer(Long answerId) {
        jdbcAnswerRepository.delete(answerId);
    }

    public Answer saveAnswer(String userId, AnswerCreatedDto answerCreatedDto) {
        Answer answer = new Answer(userId, answerCreatedDto.getContents(), answerCreatedDto.getQuestionId());
        log.debug("answer : {}", answer);

        Answer savedAnswer = jdbcAnswerRepository.insert(answer);
        jdbcQuestionRepository.updateCountOfAnswer(savedAnswer.getQuestionId());

        return savedAnswer;
    }
}
