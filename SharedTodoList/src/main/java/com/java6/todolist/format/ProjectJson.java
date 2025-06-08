package com.java6.todolist.format;

public class ProjectJson {	
	public String title;	//프로젝트 이름
	public String deadline;	//프로젝트 마감기한
	
	public ProjectJson() {};
	
	public ProjectJson(String title, String deadline) {
		this.title = title;
		this.deadline = deadline;
	};
	
	public String getTitle() {
		return title;
	}
	
	public String getDeadline() {
		return deadline;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}
}
