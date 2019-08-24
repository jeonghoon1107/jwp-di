package next.dto;

public class QuestionUpdatedDto {
    private String title;
    private String contents;

    private QuestionUpdatedDto() {
    }

    public QuestionUpdatedDto(String title, String contents) {
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
        return "QuestionUpdatedDto{" +
                "title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                '}';
    }
}
