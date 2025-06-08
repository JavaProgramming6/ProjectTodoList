package com.java6.todolist.format;

import java.util.Set;

public class ScheduleJson {

    private String title;                   // 일정 제목
    private String start;           		// 시작 시간
    private String end;             		// 종료 시간
    private Set<String> assigned;       	// 할당된 유저 ID 목록

    public ScheduleJson(String title,
    				String start,
    				String end,
                    Set<String> assignedTo) {
        this.title = title;
        this.start = start;
        this.end = end;
        this.assigned = assignedTo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public Set<String> getAssigned() {
        return assigned;
    }

    public void setAssigned(Set<String> assignedTo) {
        this.assigned = assignedTo;
    }
}
