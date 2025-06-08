package com.java6.todolist.manager;

import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.java6.todolist.format.*;
import com.java6.todolist.util.*;

public class UserManager {

    // 회원가입
    public static void signup(JsonObject data, PrintWriter out) {
        SignupData dto = JsonUtil.parseToDto(data, SignupData.class);

        Type listType = new TypeToken<List<UserJson>>() {}.getType();
        List<UserJson> users = JsonUtil.loadFromJson(FilePathsUtil.USERS_JSON_PATH, listType);

        listType = new TypeToken<List<UserProjectJson>>() {}.getType();
        List<UserProjectJson> userProjects = JsonUtil.loadFromJson(FilePathsUtil.USER_PROJECTS_JSON_PATH, listType);

        // User.json이 없다면 <- 첫번째 회원가입
        if (users == null) users = new ArrayList<>();
        if (userProjects == null) userProjects = new ArrayList<>();

        String result;

        // ID 중복 검사
        if (users.stream().anyMatch(u -> u.getId().equals(dto.id))) {
            result = JsonUtil.success(false, "ID_DUPLICATE");
            out.println(result);
            JsonUtil.logWrite("WARN", "signup", dto.id, "ID 중복");
            return;
        }

        // 해시 후 추가
        String hashed = AuthUtil.hashPassword(dto.password);
        users.add(new UserJson(dto.id, hashed, dto.name));
        userProjects.add(new UserProjectJson(dto.id, new HashMap<>()));

        // 저장
        if (JsonUtil.saveToJson(FilePathsUtil.USERS_JSON_PATH, users)
                && JsonUtil.saveToJson(FilePathsUtil.USER_PROJECTS_JSON_PATH, userProjects)) {
            result = JsonUtil.success(true, dto.id);
            JsonUtil.logWrite("INFO", "signup", dto.id, "회원가입 성공");
        } else {
            result = JsonUtil.success(false, "SAVE_FAILED");
            JsonUtil.logWrite("ERROR", "signup", dto.id, "저장 실패");
        }
        out.println(result);
    }

    // 로그인 
    public static void login(JsonObject data, PrintWriter out) {
        LoginData dto = JsonUtil.parseToDto(data, LoginData.class);

        Type listType = new TypeToken<List<UserJson>>() {}.getType();
        List<UserJson> users = JsonUtil.loadFromJson(FilePathsUtil.USERS_JSON_PATH, listType);

        String result;

        // 회원가입된 유저가 없음
        if (users == null) {
            result = JsonUtil.success(false, "ID_NOT_FOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "login", dto.id, "ID가 존재하지 않음");
            return;
        }

        // ID 판별
        UserJson user = users.stream()
                .filter(u -> u.getId().equals(dto.id))
                .findFirst()
                .orElse(null);

        if (user != null) {
            // 패스워드 판별
            if (AuthUtil.isMatch(dto.password, user.getPassword())) {
                result = JsonUtil.success(true, dto.id); // 로그인 성공
                JsonUtil.logWrite("INFO", "login", dto.id, "로그인 성공");
            } else {
                result = JsonUtil.success(false, "WRONG_PASSWORD"); // 비밀번호 틀림
                JsonUtil.logWrite("WARN", "login", dto.id, "비밀번호가 틀림");
            }
        } else {
            result = JsonUtil.success(false, "ID_NOT_FOUND"); // 아이디 없음
            JsonUtil.logWrite("WARN", "login", dto.id, "ID가 존재하지 않음");
        }
        out.println(result);
    }

    // 유저 정보 조회
    public static void fetchUser(JsonObject data, PrintWriter out) {
        FetchUserData dto = JsonUtil.parseToDto(data, FetchUserData.class);

        Type listType = new TypeToken<List<UserJson>>() {}.getType();
        List<UserJson> users = JsonUtil.loadFromJson(FilePathsUtil.USERS_JSON_PATH, listType);

        // ID와 동일한 유저 선택
        UserJson user = users.stream()
                .filter(u -> u.getId().equals(dto.id))
                .findFirst()
                .orElse(null);

        String result;

        // 유저 데이터가 없다면
        if (user == null) {
            result = JsonUtil.success(false, "USER_NOT_FOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "fetchUser", dto.id, "유저를 찾을 수 없음");
            return;
        }

        // user 정보 list 형태(id, name)로 보냄
        List<String> userData = Arrays.asList(user.getId(), user.getName());
        result = JsonUtil.success(true, userData);
        out.println(result);
        JsonUtil.logWrite("INFO", "fetchUser", dto.id, "유저 정보 조회 성공");
    }

    // 탈퇴
    public static void deleteUser(JsonObject data, PrintWriter out) {
        DeleteUserData dto = JsonUtil.parseToDto(data, DeleteUserData.class);
        String result;

        Type listType = new TypeToken<List<UserJson>>() {}.getType();
        List<UserJson> users = JsonUtil.loadFromJson(FilePathsUtil.USERS_JSON_PATH, listType);

        // ID 판별
        UserJson user = users.stream()
                .filter(u -> u.getId().equals(dto.id))
                .findFirst()
                .orElse(null);

        if (user != null) {
            // 패스워드 판별
            if (!AuthUtil.isMatch(dto.password, user.getPassword())) {
                result = JsonUtil.success(false, "WRONG_PASSWORD"); // 비밀번호 틀림
                out.println(result);
                JsonUtil.logWrite("WARN", "deleteUser", dto.id, "비밀번호가 틀림");
                return;
            }
        }

        // 삭제 대상 찾기
        boolean removed = users.removeIf(u -> u.getId().equals(dto.id));

        // 유저를 찾을 수 없음
        if (!removed) {
            result = JsonUtil.success(false, "USER_NOT_FOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "deleteUser", dto.id, "유저를 찾을 수 없음");
            return;
        }

        // UserProjectJson에서 제거
        Type upListType = new TypeToken<List<UserProjectJson>>() {}.getType();
        List<UserProjectJson> userProjects = JsonUtil.loadFromJson(FilePathsUtil.USER_PROJECTS_JSON_PATH, upListType);
        if (userProjects != null) {
            userProjects.removeIf(p -> p.getId().equals(dto.id));
            JsonUtil.saveToJson(FilePathsUtil.USER_PROJECTS_JSON_PATH, userProjects);
            JsonUtil.logWrite("INFO", "deleteUser", dto.id, "UserProjectJson에서 제거됨");
        }

        // 모든 프로젝트 경로 순회
        File projectRoot = new File(FilePathsUtil.PROJECT_PATH);
        File[] projectDirs = projectRoot.listFiles(File::isDirectory);

        if (projectDirs != null) {
            for (File dir : projectDirs) {
                String projectPath = dir.getPath();

                // members.json 수정
                String membersPath = projectPath + FilePathsUtil.MEMBERS_JSON_PATH;
                Type mListType = new TypeToken<List<MemberJson>>() {}.getType();
                List<MemberJson> members = JsonUtil.loadFromJson(membersPath, mListType);
                if (members != null && members.removeIf(m -> m.getId().equals(dto.id))) {
                    JsonUtil.saveToJson(membersPath, members);
                    JsonUtil.logWrite("INFO", "deleteUser", dto.id, "프로젝트 멤버에서 제거됨: " + dir.getName());
                }

                // schedules.json 수정
                String schedulesPath = projectPath + FilePathsUtil.SCHEDULE_JSON_PATH;
                Type sListType = new TypeToken<List<ScheduleJson>>() {}.getType();
                List<ScheduleJson> schedules = JsonUtil.loadFromJson(schedulesPath, sListType);
                boolean changed = false;
                if (schedules != null) {
                    for (ScheduleJson s : schedules) {
                        if (s.getAssigned().remove(dto.id)) changed = true;
                    }
                    if (changed) {
                        JsonUtil.saveToJson(schedulesPath, schedules);
                        JsonUtil.logWrite("INFO", "deleteUser", dto.id, "프로젝트 일정에서 제거됨: " + dir.getName());
                    }
                }
            }
        }

        // 저장
        if (JsonUtil.saveToJson(FilePathsUtil.USERS_JSON_PATH, users)) {
            result = JsonUtil.success(true, dto.id);
            out.println(result);
            JsonUtil.logWrite("INFO", "deleteUser", dto.id, "회원 탈퇴 성공");
        } else {
            result = JsonUtil.success(false, "SAVE_FAILED");
            out.println(result);
            JsonUtil.logWrite("ERROR", "deleteUser", dto.id, "저장 실패");
        }
    }

    // 유저 참여 프로젝트 조회
    public static void fetchUserProjects(JsonObject data, PrintWriter out) {
        FetchUserProjectsData dto = JsonUtil.parseToDto(data, FetchUserProjectsData.class);

        Type listType = new TypeToken<List<UserProjectJson>>() {}.getType();
        List<UserProjectJson> userProjects = JsonUtil.loadFromJson(FilePathsUtil.USER_PROJECTS_JSON_PATH, listType);

        String result;

        // 만들어진 프로젝트가 없음
        if (userProjects == null) {
            result = JsonUtil.success(false, "NO_PROJECT");
            out.println(result);
            JsonUtil.logWrite("ERROR", "fetchUserProjects", dto.id, "사용자 프로젝트 없음");
            return;
        }

        // 있으면 유저 프로젝트 목록 list 형태로 보냄
        List<String> projects = userProjects.stream()
                .filter(u -> u.getId().equals(dto.id))
                .findFirst()
                .map(u -> new ArrayList<>(u.getProjects().values()))
                .orElse(new ArrayList<>());
        
        if(projects == null) {
        	result = JsonUtil.success(false, "NO_PROJECT");
            out.println(result);
            JsonUtil.logWrite("WARN", "fetchUserProjects", dto.id, "사용자 프로젝트 없음");
        }
        
        result = JsonUtil.success(true, projects);
        out.println(result);
        JsonUtil.logWrite("INFO", "fetchUserProjects", dto.id, "사용자 프로젝트 조회 성공");
    }

    // 유저 비밀번호 변경
    public static void editUserPassword(JsonObject data, PrintWriter out) {
        EditUserPasswordData dto = JsonUtil.parseToDto(data, EditUserPasswordData.class);

        Type listType = new TypeToken<List<UserJson>>() {}.getType();
        List<UserJson> users = JsonUtil.loadFromJson(FilePathsUtil.USERS_JSON_PATH, listType);

        // 사용자 찾기
        UserJson user = users.stream()
                .filter(u -> u.getId().equals(dto.id))
                .findFirst()
                .orElse(null);

        // 사용자 유무
        if (user == null) {
            String result = JsonUtil.success(false, "USER_NOT_FOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "editUserPassword", dto.id, "사용자를 찾을 수 없음");
            return;
        }

        // 기존 비밀번호 확인
        if (!AuthUtil.isMatch(dto.oldPassword, user.getPassword())) {
            String result = JsonUtil.success(false, "WRONG_PASSWORD");
            out.println(result);
            JsonUtil.logWrite("WARN", "editUserPassword", dto.id, "기존 비밀번호 불일치");
            return;
        }

        // 비밀번호 업데이트
        user.setPassword(AuthUtil.hashPassword(dto.newPassword));

        // 저장
        if (JsonUtil.saveToJson(FilePathsUtil.USERS_JSON_PATH, users)) {
            String result = JsonUtil.success(true, null);
            out.println(result);
            JsonUtil.logWrite("INFO", "editUserPassword", dto.id, "비밀번호 변경 성공");
        } else {
            String result = JsonUtil.success(false, "SAVE_FAILED");
            out.println(result);
            JsonUtil.logWrite("ERROR", "editUserPassword", dto.id, "저장 실패");
        }
    }

    // 유저 이름 변경
    public static void editUserName(JsonObject data, PrintWriter out) {
        EditUserNameData dto = JsonUtil.parseToDto(data, EditUserNameData.class);

        Type listType = new TypeToken<List<UserJson>>() {}.getType();
        List<UserJson> users = JsonUtil.loadFromJson(FilePathsUtil.USERS_JSON_PATH, listType);

        // 사용자 찾기
        UserJson user = users.stream()
                .filter(u -> u.getId().equals(dto.id))
                .findFirst()
                .orElse(null);

        // 사용자 유무
        if (user == null) {
            String result = JsonUtil.success(false, "USER_NOT_FOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "editUserName", dto.id, "사용자를 찾을 수 없음");
            return;
        }

        // 유저 이름 업데이트
        user.setName(dto.editName);

        // 저장
        if (JsonUtil.saveToJson(FilePathsUtil.USERS_JSON_PATH, users)) {
            String result = JsonUtil.success(true, dto.editName);
            out.println(result);
            JsonUtil.logWrite("INFO", "editUserName", dto.id, "이름 변경 성공");
        } else {
            String result = JsonUtil.success(false, "SAVE_FAILED");
            out.println(result);
            JsonUtil.logWrite("ERROR", "editUserName", dto.id, "저장 실패");
        }
    }

    // 유저 프로젝트 목록 추가
    public static void addUserProject(JsonObject data) {
        AddUserProjectData dto = JsonUtil.parseToDto(data, AddUserProjectData.class);

        Type listType = new TypeToken<List<UserProjectJson>>() {}.getType();
        List<UserProjectJson> userProjects = JsonUtil.loadFromJson(FilePathsUtil.USER_PROJECTS_JSON_PATH, listType);

        // 만들어진 프로젝트가 없으면 빈 리스트
        if (userProjects == null) {
            userProjects = new ArrayList<>();
        }

        // 사용자 정보 불러오기
        UserProjectJson user = userProjects.stream()
                .filter(p -> p.getId().equals(dto.id))
                .findFirst()
                .orElse(null);

        // 새 프로젝트 추가
        user.getProjects().put(dto.uuid, dto.title);

        // 저장
        JsonUtil.saveToJson(FilePathsUtil.USER_PROJECTS_JSON_PATH, userProjects);
        JsonUtil.logWrite("INFO", "addUserProject", dto.id, "프로젝트 추가: " + dto.title);
    }

    // 유저 프로젝트 목록 삭제
    public static void deleteAllUserProject(JsonObject data) {
        String uuid = data.get("uuid").getAsString();

        Type listType = new TypeToken<List<UserProjectJson>>() {}.getType();
        List<UserProjectJson> userProjects = JsonUtil.loadFromJson(FilePathsUtil.USER_PROJECTS_JSON_PATH, listType);
        if (userProjects == null) return;

        // 모든 유저에 대해 해당 uuid 제거
        for (UserProjectJson user : userProjects) {
            if (user.getProjects().containsKey(uuid)) {
                user.getProjects().remove(uuid);
                JsonUtil.logWrite("INFO", "deleteAllUserProject", user.getId(), "프로젝트 제거: " + uuid);
            }
        }

        // 저장
        JsonUtil.saveToJson(FilePathsUtil.USER_PROJECTS_JSON_PATH, userProjects);
    }
    
    public static void deleteUserProject(JsonObject data) {
        DeleteUserProjectData dto = JsonUtil.parseToDto(data, DeleteUserProjectData.class);

        Type listType = new TypeToken<List<UserProjectJson>>() {}.getType();
        List<UserProjectJson> userProjects = JsonUtil.loadFromJson(FilePathsUtil.USER_PROJECTS_JSON_PATH, listType);
        if (userProjects == null) return;

        // 특정 ID에 대해서만 해당 uuid 제거
        for (UserProjectJson user : userProjects) {
            if (user.getId().equals(dto.id)) {
                if (user.getProjects().containsKey(dto.uuid)) {
                    user.getProjects().remove(dto.uuid);
                    JsonUtil.logWrite("INFO", "deleteUserProject", user.getId(), "프로젝트 제거: " + dto.uuid);
                }
                break; // 해당 사용자 찾았으면 루프 종료
            }
        }

        // 저장
        JsonUtil.saveToJson(FilePathsUtil.USER_PROJECTS_JSON_PATH, userProjects);
    }
}
