package next.dto;

public class AnswerCreatedDto {
    private Long questionId;
    private String contents;

    private AnswerCreatedDto() {
    }

    public AnswerCreatedDto(Long questionId, String contents) {
        this.questionId = questionId;
        this.contents = contents;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public String getContents() {
        return contents;
    }

    @Override
    public String toString() {
        return "AnswerCreatedDto{" +
                "questionId=" + questionId +
                ", contents='" + contents + '\'' +
                '}';
    }
}
