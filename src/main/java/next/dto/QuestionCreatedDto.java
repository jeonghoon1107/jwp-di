package next.dto;

public class QuestionCreatedDto {
    private String title;
    private String contents;

    private QuestionCreatedDto() {
    }

    public QuestionCreatedDto(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }

    @Override
    public String toString() {
        return "QuestionCreatedDto{" +
                "title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                '}';
    }
}
