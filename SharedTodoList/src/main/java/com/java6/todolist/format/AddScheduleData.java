package com.java6.todolist.format;

import java.util.Set;

public class AddScheduleData {
	public String id;             //명령어 주체
	public String project;        //프로젝트 이름(선택된 프로젝트)
	public String title;          //추가할 스케줄 이름
	public String start;		  //추가할 스케줄 시작기간
	public String end; 			  //추가할 스케줄 데드라인
	public Set<String> assigned;  //추가할 스케줄 할당 인원(id)
}
