package com.java6.todolist;

public class Client {
    private String id;
    private String project;
    private int scheduleIndex;

    public String getId() {
        return id;
    }

    public int getScheduleIndex() {
        return scheduleIndex;
    }

    
    public String getProject() {
        return project;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setProject(String project) {
        this.project = project;
    }
    
    public void setScheduleIndex(int index) {
        this.scheduleIndex = index;
    }
}
