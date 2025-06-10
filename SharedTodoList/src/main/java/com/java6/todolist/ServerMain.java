package com.java6.todolist;

import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.BiConsumer;

import com.google.gson.JsonObject;
import com.java6.todolist.manager.*;
import com.java6.todolist.util.*;



public class ServerMain {
	private static final int PORT = 8800;
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(10); // 최대 10명

    public static void main(String[] args) {
        System.out.println("서버 시작. 포트 " + PORT + "에서 대기 중...");
        JsonUtil.logWrite("INFO", "main", "system" , "서버 실행");
        
        JsonUtil.createFile(FilePathsUtil.USERS_JSON_PATH); //users 폴더 및 users.json 생성(없으면 생성안됨)
        JsonUtil.createDirectory(FilePathsUtil.PROJECT_PATH); //projects 폴더 생성(없으면 생성안됨)
        JsonUtil.createFile(FilePathsUtil.USER_PROJECTS_JSON_PATH);
        
        //명령어 삽입
        Map<String, BiConsumer<JsonObject, PrintWriter>> dispatcher = new ConcurrentHashMap<String, BiConsumer<JsonObject, PrintWriter>>();
        dispatcher.put("signup", UserManager::signup);
        dispatcher.put("login", UserManager::login);
        dispatcher.put("fetchUser", UserManager::fetchUser);
        dispatcher.put("deleteUser", UserManager::deleteUser);
        dispatcher.put("fetchUserProjects", UserManager::fetchUserProjects);
        dispatcher.put("editUserPassword", UserManager::editUserPassword);
        dispatcher.put("editUserName", UserManager::editUserName);
        dispatcher.put("selectProject", ProjectManager :: selectProject);
        dispatcher.put("createProject", ProjectManager :: createProject);
        dispatcher.put("deleteProject", ProjectManager :: deleteProject);
        dispatcher.put("editProjectTitle", ProjectManager :: editProjectTitle);
        dispatcher.put("editProjectDeadline", ProjectManager :: editProjectDeadline);
        dispatcher.put("addMember", MemberManager :: addMember);
        dispatcher.put("deleteMember", MemberManager :: deleteMember);
        dispatcher.put("editMemberRole", MemberManager :: editMemberRole);
        dispatcher.put("editMemberPermission", MemberManager :: editMemberPermission);
        dispatcher.put("fetchMember", MemberManager :: fetchMember);
        dispatcher.put("addSchedule", ScheduleManager :: addSchedule);
        dispatcher.put("deleteSchedule", ScheduleManager :: deleteSchedule);
        dispatcher.put("editScheduleTitle", ScheduleManager :: editScheduleTitle);
        dispatcher.put("editScheduleStartDate", ScheduleManager :: editScheduleStartDate);
        dispatcher.put("editScheduleDeadline", ScheduleManager :: editScheduleDeadline);
        dispatcher.put("addScheduleAssigned", ScheduleManager :: addScheduleAssigned);
        dispatcher.put("deleteScheduleAssigned", ScheduleManager :: deleteScheduleAssigned);
        dispatcher.put("fetchSchedule", ScheduleManager :: fetchSchedule);
        
        //서버소켓 생성
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
            	//클라이언트 접속
                Socket clientSocket = serverSocket.accept();
                threadPool.execute(new ClientHandler(clientSocket, dispatcher));
            }
        } catch (IOException e) {
            System.err.println("서버 에러: " + e.getMessage());
            JsonUtil.logWrite("ERROR", "main", "system", "서버 에러 : " + e.getMessage());
        } finally {
        	JsonUtil.logWrite("INFO", "main", "system", "서버 종료");
            threadPool.shutdown();
        }
    }
}