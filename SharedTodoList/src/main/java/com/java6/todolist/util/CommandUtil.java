package com.java6.todolist.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.java6.todolist.Client;

public class CommandUtil {

    public static void cmdIn(Client client, String cmd, BufferedReader userInput, PrintWriter out) {

        Gson gson = new Gson();

        try {
            Map<String, Object> request = new HashMap<>();
            Map<String, Object> data = new HashMap<>();
            request.put("command", cmd);

            if (cmd.equalsIgnoreCase("signup")) {
                String id, pw, name;
                while (true) {
                    System.out.print("아이디를 입력해주세요: ");
                    id = userInput.readLine();
                    if (!id.isBlank()) break;
                    System.out.println("다시 입력해주세요");
                }
                while (true) {
                    System.out.print("비밀번호를 입력해주세요: ");
                    pw = userInput.readLine();
                    if (!pw.isBlank()) break;
                    System.out.println("다시 입력해주세요");
                }
                while (true) {
                    System.out.print("이름을 입력해주세요: ");
                    name = userInput.readLine();
                    if (!name.isBlank()) break;
                    System.out.println("다시 입력해주세요");
                }

                data.put("id", id);
                data.put("password", pw);
                data.put("name", name);
            }

            if (cmd.equalsIgnoreCase("login")) {
                System.out.print("아이디를 입력해주세요: ");
                String id = userInput.readLine();
                System.out.print("비밀번호를 입력해주세요: ");
                String pw = userInput.readLine();

                data.put("id", id);
                data.put("password", pw);
            }

            if (cmd.equalsIgnoreCase("deleteProject")) {
                String id = client.getId();

                System.out.print("삭제할 프로젝트명을 입력해주세요: ");
                String title = userInput.readLine();

                data.put("id", id);
                data.put("project", title);
            }

            if (cmd.equalsIgnoreCase("fetchUserProjects")) {
                String id = client.getId();
                data.put("id", id);
            }

            if (cmd.equalsIgnoreCase("createProject")) {
                String id = client.getId();
                String title, deadline;
                while (true) {
                    System.out.print("프로젝트명을 입력해주세요: ");
                    title = userInput.readLine();
                    if (!title.isBlank()) break;
                    System.out.println("다시 입력해주세요");
                }

                System.out.print("형식에 맞춰 마감일을 입력해주세요[yyyy-MM-dd HH:mm]: ");
                deadline = userInput.readLine();

                data.put("id", id);
                data.put("title", title);
                data.put("deadline", deadline);
            }

            if (cmd.equalsIgnoreCase("selectProject")) {
                String id = client.getId();
                String title;

                while (true) {
                    System.out.print("프로젝트명을 입력해주세요: ");
                    title = userInput.readLine();
                    if (!title.isBlank()) break;
                    System.out.println("다시 입력해주세요");
                }

                data.put("id", id);
                data.put("project", title);
            }

            if (cmd.equalsIgnoreCase("addMember")) {
                String projectTitle = client.getProject();
                String ownerId = client.getId();
                String role, permission;
                System.out.print("추가할 멤버의 id를 입력해주세요: ");
                String addId = userInput.readLine();

                while (true) {
                    System.out.print("멤버의 역할을 입력해주세요: ");
                    role = userInput.readLine();
                    if (!role.isBlank()) break;
                    System.out.println("입력이 없습니다");
                }

                while (true) {
                    System.out.print("주어질 권한을 입력해주세요(owner|editor|viewer): ");
                    permission = userInput.readLine();
                    if (!permission.isBlank()) break;
                    System.out.println("입력이 없습니다");
                }

                data.put("id", ownerId);
                data.put("project", projectTitle);
                data.put("addId", addId);
                data.put("role", role);
                data.put("permission", permission);
            }

            if (cmd.equalsIgnoreCase("deleteMember")) {
                String projectTitle = client.getProject();
                String ownerId = client.getId();

                System.out.print("삭제할 멤버의 아이디를 입력해주세요: ");
                String deleteId = userInput.readLine();

                data.put("id", ownerId);
                data.put("project", projectTitle);
                data.put("deleteId", deleteId);
            }

            if (cmd.equalsIgnoreCase("fetchUser")) {
                String id = client.getId();
                data.put("id", id);
            }

            if (cmd.equalsIgnoreCase("deleteUser")) {
                String id = client.getId();

                System.out.print("비밀번호를 입력해주세요: ");
                String pw = userInput.readLine();

                data.put("id", id);
                data.put("password", pw);
            }

            if (cmd.equalsIgnoreCase("fetchMember")) {
                String id = client.getId();
                String projectTitle = client.getProject();
                data.put("id", id);
                data.put("project", projectTitle);
            }

            if (cmd.equalsIgnoreCase("editUserPassword")) {
                String id = client.getId();

                System.out.print("현재 비밀번호를 입력해주세요: ");
                String oldPw = userInput.readLine();
                String newPw;
                while (true) {
                    System.out.print("새롭게 지정할 비밀번호를 입력해주세요: ");
                    newPw = userInput.readLine();
                    if (!newPw.isBlank()) break;
                    System.out.println("입력이 없습니다");
                }

                data.put("id", id);
                data.put("oldPassword", oldPw);
                data.put("newPassword", newPw);
            }

            if (cmd.equalsIgnoreCase("editUserName")) {
                String id = client.getId();
                String name;
                while (true) {
                    System.out.print("변경할 이름을 입력해주세요: ");
                    name = userInput.readLine();
                    if (!name.isBlank()) break;
                    System.out.println("입력이 없습니다");
                }

                data.put("id", id);
                data.put("editName", name);
            }

            if (cmd.equalsIgnoreCase("editProjectTitle")) {
                String id = client.getId();
                String editTitle;
                String beforeTitle = client.getProject();
                while (true) {
                    System.out.print("변경할 프로젝트명을 입력해주세요: ");
                    editTitle = userInput.readLine();
                    if (!editTitle.isBlank()) break;
                    System.out.println("입력이 없습니다");
                }

                data.put("id", id);
                data.put("project", beforeTitle);
                data.put("editTitle", editTitle);
            }

            if (cmd.equalsIgnoreCase("editProjectDeadline")) {
                String id = client.getId();
                String title = client.getProject();

                System.out.print("변경될 마감일을 입력해주세요[yyyy-MM-dd HH:mm]: ");
                String editDate = userInput.readLine();

                data.put("id", id);
                data.put("project", title);
                data.put("editDeadline", editDate);
            }

            if (cmd.equalsIgnoreCase("editMemberRole")) {
                String projectTitle = client.getProject();
                String ownerId = client.getId();
                String editRole;
                System.out.print("역할 변경할 멤버의 ID를 입력해주세요: ");
                String editId = userInput.readLine();

                while (true) {
                    System.out.print("어떤 역할로 변경할지 입력해주세요: ");
                    editRole = userInput.readLine();
                    if (!editRole.isBlank()) break;
                    System.out.println("입력이 없습니다");
                }

                data.put("id", ownerId);
                data.put("project", projectTitle);
                data.put("editId", editId);
                data.put("editRole", editRole);
            }

            if (cmd.equalsIgnoreCase("editMemberPermission")) {
                String projectTitle = client.getProject();
                String ownerId = client.getId();
                String editPermission;
                System.out.print("역할 변경할 멤버의 ID를 입력해주세요: ");
                String editId = userInput.readLine();

                while (true) {
                    System.out.print("어떤 권한으로 변경할지 입력해주세요(owner|editor|viewer): ");
                    editPermission = userInput.readLine();
                    if (!editPermission.isBlank()) break;
                    System.out.println("입력이 없습니다");
                }

                data.put("id", ownerId);
                data.put("project", projectTitle);
                data.put("editId", editId);
                data.put("editPermission", editPermission);
            }

            if (cmd.equalsIgnoreCase("addSchedule")) {
                String id = client.getId();
                String project = client.getProject();
                String title;
                while (true) {
                    System.out.print("스케줄명을 입력해주세요: ");
                    title = userInput.readLine();
                    if (!title.isBlank()) break;
                    System.out.println("입력이 없습니다");
                }

                System.out.print("시작일을 입력해주세요[yyyy-MM-dd HH:mm]: ");
                String start = userInput.readLine();

                System.out.print("마감일을 입력해주세요[yyyy-MM-dd HH:mm]: ");
                String end = userInput.readLine();

                System.out.print("할당될 멤버를 입력해주세요: ");
                List<String> assigned = List.of(userInput.readLine().trim().split(" "));

                data.put("id", id);
                data.put("project", project);
                data.put("title", title);
                data.put("start", start);
                data.put("end", end);
                data.put("assigned", assigned);
            }

            if (cmd.equalsIgnoreCase("deleteSchedule")) {
                String id = client.getId();
                String project = client.getProject();
                int index = client.getScheduleIndex();

                data.put("id", id);
                data.put("project", project);
                data.put("index", index);
            }

            if (cmd.equalsIgnoreCase("editScheduleTitle")) {
                String id = client.getId();
                String project = client.getProject();
                String editTitle;
                int index = client.getScheduleIndex();

                while (true) {
                    System.out.print("변경할 스케줄명을 입력해주세요: ");
                    editTitle = userInput.readLine();
                    if (!editTitle.isBlank()) break;
                    System.out.println("입력이 없습니다");
                }

                data.put("id", id);
                data.put("project", project);
                data.put("index", index);
                data.put("editTitle", editTitle);
            }

            if (cmd.equalsIgnoreCase("editScheduleStartDate") || cmd.equalsIgnoreCase("editScheduleDeadline")) {
                String id = client.getId();
                String project = client.getProject();

                System.out.print("날짜를 입력해주세요[yyyy-MM-dd HH:mm]: ");
                String editDate = userInput.readLine();
                int index = client.getScheduleIndex();

                data.put("id", id);
                data.put("project", project);
                data.put("index", index);
                data.put("editDate", editDate);
            }

            if (cmd.equalsIgnoreCase("addScheduleAssigned") || cmd.equalsIgnoreCase("deleteScheduleAssigned")) {
                String id = client.getId();
                String project = client.getProject();
                System.out.print("멤버명을 입력해주세요: ");
                String assigned = userInput.readLine();
                int index = client.getScheduleIndex();

                data.put("id", id);
                data.put("project", project);
                data.put("index", index);
                data.put("assigned", assigned);
            }

            if (cmd.equalsIgnoreCase("fetchSchedule")) {
                String id = client.getId();
                String project = client.getProject();

                data.put("id", id);
                data.put("project", project);
            }

            request.put("data", data);
            out.println(gson.toJson(request));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
