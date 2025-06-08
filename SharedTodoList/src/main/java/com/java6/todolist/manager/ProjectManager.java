package com.java6.todolist.manager;

import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.java6.todolist.format.*;
import com.java6.todolist.util.*;

public class ProjectManager {
	
    // title->uuid 변환
    public static String findProjectUuidByTitle(String id, String title) {
        Type listType = new TypeToken<List<UserProjectJson>>() {}.getType();
        List<UserProjectJson> userProjects = JsonUtil.loadFromJson(FilePathsUtil.USER_PROJECTS_JSON_PATH, listType);
        
        // 참여 중인 프로젝트가 없으면
        if (userProjects == null) return null;

        // 사용자 찾기
        UserProjectJson user = userProjects.stream()
            .filter(p -> p.getId().equals(id))
            .findFirst()
            .orElse(null);
        
        // 사용자를 찾을 수 없다면
        if (user == null) return null;
        
        // 해당 title에 맞는 uuid 반환(없으면 null)
        return user.getProjects().entrySet().stream()
            .filter(entry -> entry.getValue().equals(title))
            .map(Map.Entry::getKey)
            .findFirst()
            .orElse(null);
    }
    
    // 해당 프로젝트가 존재하는지 확인
    public static void selectProject(JsonObject data, PrintWriter out) {
        SelectProjectData dto = JsonUtil.parseToDto(data, SelectProjectData.class);
        String result;
        
        // title-> uuid 
        String uuid = findProjectUuidByTitle(dto.id, dto.project);
        if (uuid == null) {
            result = JsonUtil.success(false, "PROJECT_NOT_FOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "selectProject", dto.id, "프로젝트를 찾을 수 없음: " + dto.project);
            return;
        }
        
        result = JsonUtil.success(true, dto.project);
        out.println(result);
        JsonUtil.logWrite("INFO", "selectProject", dto.id, "프로젝트 선택 성공: " + dto.project);
    }

    // 프로젝트 생성
    public static void createProject(JsonObject data, PrintWriter out) {
        CreateProjectData dto = JsonUtil.parseToDto(data, CreateProjectData.class);
        String result;
        
        // 유저 id 확인
        if (!ValidatorUtil.isUserExists(dto.id)) {
            result = JsonUtil.success(false, "USER_NOT_FOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "createProject", dto.id, "사용자를 찾을 수 없음");
            return;
        }
        
        Type listType = new TypeToken<List<UserProjectJson>>() {}.getType();
        List<UserProjectJson> userProjects = JsonUtil.loadFromJson(FilePathsUtil.USER_PROJECTS_JSON_PATH, listType);

        // 만들어진 프로젝트가 없으면 빈 리스트
        if (userProjects == null) {
            userProjects = new ArrayList<>();
        }

        // 사용자 존재 여부 확인
        UserProjectJson user = userProjects.stream()
            .filter(u -> u.getId().equals(dto.id))
            .findFirst()
            .orElse(null);
        
        // 제목 중복
        if (user != null && user.getProjects().containsValue(dto.title)) {
            result = JsonUtil.success(false, "PROJECT_ALREADY_EXISTS");
            out.println(result);
            JsonUtil.logWrite("WARN", "createProject", dto.id, "프로젝트 제목 중복: " + dto.title);
            return;
        }
        
        // UUID 생성
        String uuid = UUID.randomUUID().toString();

        // 디렉토리 경로를 UUID로
        String dirPath = FilePathsUtil.PROJECT_PATH + uuid;
        String membersPath = dirPath + FilePathsUtil.MEMBERS_JSON_PATH;
        String projectPath = dirPath + FilePathsUtil.PROJECT_JSON_PATH;

        // 날짜 포맷 확인
        ValidatorUtil.DateValidationResult validationResult = ValidatorUtil.validateDeadline(dto.deadline);

        switch (validationResult) {
            case INVALID_FORMAT:
                result = JsonUtil.success(false, "INVALID_DATE_FORMAT");
                out.println(result);
                JsonUtil.logWrite("WARN", "createProject", dto.id, "날짜 형식 오류: " + dto.deadline);
                return;
            case INVALID_VALUE:
                result = JsonUtil.success(false, "INVALID_DATE_VALUE");
                out.println(result);
                JsonUtil.logWrite("WARN", "createProject", dto.id, "날짜 값 오류: " + dto.deadline);
                return;
            default:
                break;
        }
        
        // 멤버 초기화
        List<MemberJson> members = new ArrayList<>();
        members.add(new MemberJson(dto.id, "PM", "owner"));

        // JSON 전송용 데이터 (uuid 포함)
        Gson gson = new Gson();
        Map<String, String> addUserProjectData = new HashMap<>();
        addUserProjectData.put("id", dto.id);
        addUserProjectData.put("uuid", uuid);
        addUserProjectData.put("title", dto.title);
        
        ProjectJson projectData = new ProjectJson(dto.title, dto.deadline);

        JsonObject jsonObject = JsonParser.parseString(gson.toJson(addUserProjectData)).getAsJsonObject();
        
        String scheduleJsonPath = FilePathsUtil.PROJECT_PATH + uuid + FilePathsUtil.SCHEDULE_JSON_PATH;
        JsonUtil.createFile(scheduleJsonPath);

        // 저장
        if (JsonUtil.saveToJson(membersPath, members) && JsonUtil.saveToJson(projectPath, projectData)) {
            UserManager.addUserProject(jsonObject);
            result = JsonUtil.success(true, dto.title);
            out.println(result);
            JsonUtil.logWrite("INFO", "createProject", dto.id, "프로젝트 생성 성공: " + dto.title);
        } else {
            result = JsonUtil.success(false, "SAVE_FAILED");
            out.println(result);
            JsonUtil.logWrite("ERROR", "createProject", dto.id, "프로젝트 저장 실패");
        }
    }

    public static void deleteProject(JsonObject data, PrintWriter out) {
        DeleteProjectData dto = JsonUtil.parseToDto(data, DeleteProjectData.class);
        String result;
        
        // 유저 id 확인
        if (!ValidatorUtil.isUserExists(dto.id)) {
            result = JsonUtil.success(false, "USER_NOT_FOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "deleteProject", dto.id, "사용자를 찾을 수 없음");
            return;
        }

        String uuid = findProjectUuidByTitle(dto.id, dto.project);
        if (uuid == null) {
            result = JsonUtil.success(false, "PROJECT_NOT_FOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "deleteProject", dto.id, "프로젝트를 찾을 수 없음: " + dto.project);
            return;
        }
        
        // UUID 기준으로 경로 구성
        String dirPath = FilePathsUtil.PROJECT_PATH + uuid;
        String membersPath = dirPath + FilePathsUtil.MEMBERS_JSON_PATH;

        // 멤버 로드
        Type listType = new TypeToken<List<MemberJson>>() {}.getType();
        List<MemberJson> members = JsonUtil.loadFromJson(membersPath, listType);
        
        if (members == null) members = new ArrayList<>();

        // JsonObject 생성
        Gson gson = new Gson();
        Map<String, String> saveData = new HashMap<>();
        saveData.put("uuid", uuid);
        JsonObject jsonObject = JsonParser.parseString(gson.toJson(saveData)).getAsJsonObject();

        // 삭제 권한 확인
        if (members.stream().anyMatch(m -> m.getId().equals(dto.id) && m.getPermission().equals("owner"))) {
            if (JsonUtil.deleteDirectory(dirPath)) {
                UserManager.deleteAllUserProject(jsonObject);
                result = JsonUtil.success(true, dto.project);
                out.println(result);
                JsonUtil.logWrite("INFO", "deleteProject", dto.id, "프로젝트 삭제 성공: " + dto.project);
            } else {
                result = JsonUtil.success(false, "SAVE_FAILED");
                out.println(result);
                JsonUtil.logWrite("ERROR", "deleteProject", dto.id, "프로젝트 삭제 실패: " + dto.project);
            }
        } else {
            result = JsonUtil.success(false, "PERMISSION_DENIED");
            out.println(result);
            JsonUtil.logWrite("WARN", "deleteProject", dto.id, "권한 거부: " + dto.project);
        }
    }
    
    public static void editProjectTitle(JsonObject data, PrintWriter out) {
        EditProjectTitleData dto = JsonUtil.parseToDto(data, EditProjectTitleData.class);
        String result;
        
        // 유저 id 확인
        if (!ValidatorUtil.isUserExists(dto.id)) {
            result = JsonUtil.success(false, "USER_NOT_FOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "editProjectTitle", dto.id, "사용자를 찾을 수 없음");
            return;
        }
        
        String uuid = findProjectUuidByTitle(dto.id, dto.project);
        if (uuid == null) {
            result = JsonUtil.success(false, "PROJECT_NOT_FOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "editProjectTitle", dto.id, "프로젝트를 찾을 수 없음: " + dto.project);
            return;
        }
        
        // 디렉토리 경로를 UUID로
        String dirPath = FilePathsUtil.PROJECT_PATH + uuid;
        String projectPath = dirPath + FilePathsUtil.PROJECT_JSON_PATH;
        String membersPath = dirPath + FilePathsUtil.MEMBERS_JSON_PATH;
        
        // 멤버 load
        Type listType = new TypeToken<List<MemberJson>>() {}.getType();
        List<MemberJson> members = JsonUtil.loadFromJson(membersPath, listType);
        if (members == null) members = new ArrayList<>();
        
        // 현재 요청자가 owner인지
        boolean isOwner = members.stream()
            .anyMatch(m -> m.getId().equals(dto.id) && m.getPermission().equals("owner"));
        if (!isOwner) {
            result = JsonUtil.success(false, "PERMISSION_DENIED");
            out.println(result);
            JsonUtil.logWrite("WARN", "editProjectTitle", dto.id, "권한 거부: " + dto.project);
            return;
        }
        
        listType = new TypeToken<List<UserProjectJson>>() {}.getType();
        List<UserProjectJson> userProjects = JsonUtil.loadFromJson(FilePathsUtil.USER_PROJECTS_JSON_PATH, listType);
        
        // 전체 유저의 projects 값(value)에서 중복 제목이 있는지 검사
        boolean isDuplicate = userProjects.stream()
            .flatMap(user -> user.getProjects().values().stream())
            .anyMatch(title -> title.equals(dto.editTitle));
        if (isDuplicate) {
            result = JsonUtil.success(false, "TITLE_DUPLICATE");
            out.println(result);
            JsonUtil.logWrite("WARN", "editProjectTitle", dto.id, "제목 중복: " + dto.editTitle);
            return;
        }

        // UUID에 해당하는 제목을 가진 유저들의 값만 수정
        for (UserProjectJson user : userProjects) {
            if (user.getProjects().containsKey(uuid)) {
                user.getProjects().put(uuid, dto.editTitle);
            }
        }
        
        ProjectJson projectData = JsonUtil.loadFromJson(projectPath, ProjectJson.class);
        projectData.setTitle(dto.editTitle);
        
        // 저장
        if (JsonUtil.saveToJson(FilePathsUtil.USER_PROJECTS_JSON_PATH, userProjects)
                && JsonUtil.saveToJson(projectPath, projectData)) {
            result = JsonUtil.success(true, dto.editTitle);
            out.println(result);
            JsonUtil.logWrite("INFO", "editProjectTitle", dto.id, "프로젝트 제목 변경 성공: " + dto.editTitle);
        } else {
            result = JsonUtil.success(false, "SAVE_FAILED");
            out.println(result);
            JsonUtil.logWrite("ERROR", "editProjectTitle", dto.id, "프로젝트 제목 변경 실패: " + dto.project);
        }
    }
    
    public static void editProjectDeadline(JsonObject data, PrintWriter out) {
        EditProjectDateData dto = JsonUtil.parseToDto(data, EditProjectDateData.class);
        String result;
        
        // 유저 id 확인
        if (!ValidatorUtil.isUserExists(dto.id)) {
            result = JsonUtil.success(false, "USER_NOT_FOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "editProjectDeadline", dto.id, "사용자를 찾을 수 없음");
            return;
        }
        
        String uuid = findProjectUuidByTitle(dto.id, dto.project);
        if (uuid == null) {
            result = JsonUtil.success(false, "PROJECT_NOT_FOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "editProjectDeadline", dto.id, "프로젝트를 찾을 수 없음: " + dto.project);
            return;
        }
        
        String membersPath = FilePathsUtil.PROJECT_PATH + uuid + FilePathsUtil.MEMBERS_JSON_PATH;
        
        // 멤버 load
        Type listType = new TypeToken<List<MemberJson>>() {}.getType();
        List<MemberJson> members = JsonUtil.loadFromJson(membersPath, listType);
        if (members == null) members = new ArrayList<>();
        
        // 현재 요청자가 owner인지
        boolean isOwner = members.stream()
            .anyMatch(m -> m.getId().equals(dto.id) && m.getPermission().equals("owner"));
        if (!isOwner) {
            result = JsonUtil.success(false, "PERMISSION_DENIED");
            out.println(result);
            JsonUtil.logWrite("WARN", "editProjectDeadline", dto.id, "권한 거부: " + dto.project);
            return;
        }
        
        // 날짜 포맷 유효성 검사
        ValidatorUtil.DateValidationResult validationResult = ValidatorUtil.validateDeadline(dto.editDeadline);
        switch (validationResult) {
            case INVALID_FORMAT:
                result = JsonUtil.success(false, "INVALID_DATE_FORMAT");
                out.println(result);
                JsonUtil.logWrite("WARN", "editProjectDeadline", dto.id, "날짜 형식 오류: " + dto.editDeadline);
                return;
            case INVALID_VALUE:
                result = JsonUtil.success(false, "INVALID_DATE_VALUE");
                out.println(result);
                JsonUtil.logWrite("WARN", "editProjectDeadline", dto.id, "날짜 값 오류: " + dto.editDeadline);
                return;
            default:
                break;
        }
        
        // 경로 구성
        String projectPath = FilePathsUtil.PROJECT_PATH + uuid + FilePathsUtil.PROJECT_JSON_PATH;

        // 기존 데이터 로드
        ProjectJson project = JsonUtil.loadFromJson(projectPath, ProjectJson.class);
        project.setDeadline(dto.editDeadline);

        // 저장
        if (JsonUtil.saveToJson(projectPath, project)) {
            result = JsonUtil.success(true, dto.editDeadline);
            out.println(result);
            JsonUtil.logWrite("INFO", "editProjectDeadline", dto.id, "프로젝트 마감일 변경 성공: " + dto.editDeadline);
        } else {
            result = JsonUtil.success(false, "SAVE_FAILED");
            out.println(result);
            JsonUtil.logWrite("ERROR", "editProjectDeadline", dto.id, "프로젝트 마감일 변경 실패: " + dto.project);
        }
    }
}
