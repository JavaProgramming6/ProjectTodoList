package model;

public class Project {
    private String id;       // 유저 id
    private String title;    // 프로젝트 이름
    private String deadline; // 마감일

    public Project(String id, String title, String deadline) {
        this.id = id;
        this.title = title;
        this.deadline = deadline;
    }

    // getter, setter 작성
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDeadline() { return deadline; }

    public void setTitle(String title) { this.title = title; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
}
