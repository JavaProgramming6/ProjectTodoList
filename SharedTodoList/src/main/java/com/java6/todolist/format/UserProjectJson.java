package com.java6.todolist.format;
import java.util.Map;

public class UserProjectJson {
    private String id;							//유저 ID
    private Map<String, String> projects;		//UUID : TITLE형태의 유저의 참여 프로젝트 목록

    public UserProjectJson() {}

    public UserProjectJson(String id, Map<String, String> projects) {
        this.id = id;
        this.projects = projects;
    }
    
    public String getId() {
        return id;
    }

    public Map<String, String> getProjects() {
        return projects;
    }

    public void setProjects(Map<String, String> projects) {
        this.projects = projects;
    }
}
