package com.java6.todolist.manager;

import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.java6.todolist.format.*;
import com.java6.todolist.util.*;

public class MemberManager {
    public static void addMember(JsonObject data, PrintWriter out) {
        AddMemberData dto = JsonUtil.parseToDto(data, AddMemberData.class);
        String result;

        // UUID 가져오기
        String uuid = ProjectManager.findProjectUuidByTitle(dto.id, dto.project);
        if (uuid == null) {
            result = JsonUtil.success(false, "PROJECT_NOT_FOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "addMember", dto.id, "프로젝트를 찾을 수 없음: " + dto.project);
            return;
        }

        // 경로 설정
        String membersPath = FilePathsUtil.PROJECT_PATH + uuid + FilePathsUtil.MEMBERS_JSON_PATH;
        File membersFile = new File(membersPath);
        if (!membersFile.exists()) {
            result = JsonUtil.success(false, "PROJECT_NOT_FOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "addMember", dto.id, "members.json 파일이 존재하지 않음: " + uuid);
            return;
        }

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
            JsonUtil.logWrite("WARN", "addMember", dto.id, "권한 거부: " + dto.project);
            return;
        }

        if (!ValidatorUtil.isUserExists(dto.addId)) {
            result = JsonUtil.success(false, "USER_NOT_FOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "addMember", dto.id, "추가 대상 사용자를 찾을 수 없음: " + dto.addId);
            return;
        }

        // 제목 중복 체크
        Type userProjectListType = new TypeToken<List<UserProjectJson>>() {}.getType();
        List<UserProjectJson> userProjects = JsonUtil.loadFromJson(FilePathsUtil.USER_PROJECTS_JSON_PATH, userProjectListType);

        boolean isDuplicate = userProjects.stream()
            .filter(p -> p.getId().equals(dto.addId))
            .flatMap(p -> p.getProjects().values().stream())
            .anyMatch(title -> title.equals(dto.project));
        if (isDuplicate) {
            result = JsonUtil.success(false, "TITLE_DUPLICATE");
            out.println(result);
            JsonUtil.logWrite("WARN", "addMember", dto.id, "프로젝트 중복 참여 시도: " + dto.addId + " -> " + dto.project);
            return;
        }

        // 권한 값 유효성 검사
        if (!ValidatorUtil.isValidPermission(dto.permission)) {
            result = JsonUtil.success(false, "INVALID_PERMISSION_VALUE");
            out.println(result);
            JsonUtil.logWrite("WARN", "addMember", dto.id, "잘못된 권한 값: " + dto.permission);
            return;
        }

        // 멤버 추가
        members.add(new MemberJson(dto.addId, dto.role, dto.permission));

        // 저장
        if (JsonUtil.saveToJson(membersPath, members)) {
            // 유저 프로젝트 목록에 추가
            Gson gson = new Gson();
            Map<String, String> addUserProjectData = new HashMap<>();
            addUserProjectData.put("id", dto.addId);
            addUserProjectData.put("uuid", uuid);
            addUserProjectData.put("title", dto.project);
            String jsonRequest = gson.toJson(addUserProjectData);
            JsonObject jsonObject = JsonParser.parseString(jsonRequest).getAsJsonObject();

            UserManager.addUserProject(jsonObject);
            result = JsonUtil.success(true, dto.addId);
            JsonUtil.logWrite("INFO", "addMember", dto.id, "멤버 추가 성공: " + dto.addId + " -> " + dto.project);
        } else {
            result = JsonUtil.success(false, "SAVE_FAILED");
            JsonUtil.logWrite("ERROR", "addMember", dto.id, "members.json 저장 실패: " + uuid);
        }
        out.println(result);
    }

    public static void deleteMember(JsonObject data, PrintWriter out) {
        DeleteMemberData dto = JsonUtil.parseToDto(data, DeleteMemberData.class);
        String result;

        // UUID 찾기
        String uuid = ProjectManager.findProjectUuidByTitle(dto.id, dto.project);
        if (uuid == null) {
            result = JsonUtil.success(false, "PROJECT_NOT_FOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "deleteMember", dto.id, "프로젝트를 찾을 수 없음: " + dto.project);
            return;
        }

        // 경로 설정
        String membersPath = FilePathsUtil.PROJECT_PATH + uuid + FilePathsUtil.MEMBERS_JSON_PATH;
        File membersFile = new File(membersPath);
        if (!membersFile.exists()) {
            result = JsonUtil.success(false, "PROJECT_NOT_FOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "deleteMember", dto.id, "members.json 파일이 존재하지 않음: " + uuid);
            return;
        }

        // 멤버 로드
        Type listType = new TypeToken<List<MemberJson>>() {}.getType();
        List<MemberJson> members = JsonUtil.loadFromJson(membersPath, listType);
        if (members == null) members = new ArrayList<>();

        // owner 권한 확인
        boolean isOwner = members.stream()
            .anyMatch(m -> m.getId().equals(dto.id) && m.getPermission().equals("owner"));
        if (!isOwner) {
            result = JsonUtil.success(false, "PERMISSION_DENIED");
            out.println(result);
            JsonUtil.logWrite("WARN", "deleteMember", dto.id, "권한 거부: " + dto.project);
            return;
        }

        // 멤버 삭제
        boolean removed = members.removeIf(m -> m.getId().equals(dto.deleteId));
        if (!removed) {
            result = JsonUtil.success(false, "MEMBER_NOT_FOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "deleteMember", dto.id, "삭제할 멤버가 없음: " + dto.deleteId);
            return;
        }

        // 저장
        if (JsonUtil.saveToJson(membersPath, members)) {
            // UserProject 동기화
            JsonObject userProjectInfo = new JsonObject();
            userProjectInfo.addProperty("id", dto.deleteId);
            userProjectInfo.addProperty("uuid", uuid);
            UserManager.deleteUserProject(userProjectInfo);
            result = JsonUtil.success(true, dto.deleteId);
            JsonUtil.logWrite("INFO", "deleteMember", dto.id, "멤버 삭제 성공: " + dto.deleteId + " from " + dto.project);
        } else {
            result = JsonUtil.success(false, "SAVE_FAILED");
            JsonUtil.logWrite("ERROR", "deleteMember", dto.id, "members.json 저장 실패: " + uuid);
        }
        out.println(result);
    }

    public static void editMemberRole(JsonObject data, PrintWriter out) {
        EditMemberRoleData dto = JsonUtil.parseToDto(data, EditMemberRoleData.class);
        String result;

        // UUID 찾기
        String uuid = ProjectManager.findProjectUuidByTitle(dto.id, dto.project);
        if (uuid == null) {
            result = JsonUtil.success(false, "PROJECT_NOT_FOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "editMemberRole", dto.id, "프로젝트를 찾을 수 없음: " + dto.project);
            return;
        }

        String membersPath = FilePathsUtil.PROJECT_PATH + uuid + FilePathsUtil.MEMBERS_JSON_PATH;
        File membersFile = new File(membersPath);
        if (!membersFile.exists()) {
            result = JsonUtil.success(false, "PROJECT_NOT_FOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "editMemberRole", dto.id, "members.json 파일이 존재하지 않음: " + uuid);
            return;
        }

        Type listType = new TypeToken<List<MemberJson>>() {}.getType();
        List<MemberJson> members = JsonUtil.loadFromJson(membersPath, listType);
        if (members == null) members = new ArrayList<>();

        // owner 권한 확인
        boolean isOwner = members.stream()
            .anyMatch(m -> m.getId().equals(dto.id) && m.getPermission().equals("owner"));
        if (!isOwner) {
            result = JsonUtil.success(false, "PERMISSION_DENIED");
            out.println(result);
            JsonUtil.logWrite("WARN", "editMemberRole", dto.id, "권한 거부: " + dto.project);
            return;
        }

        // 멤버가 없다면
        boolean memberExists = members.stream()
            .anyMatch(m -> m.getId().equals(dto.editId));
        if (!memberExists) {
            result = JsonUtil.success(false, "MEMBER_NOT_FOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "editMemberRole", dto.id, "수정할 멤버가 없음: " + dto.editId);
            return;
        }

        // 멤버 수정
        for (MemberJson m : members) {
            if (m.getId().equals(dto.editId)) {
                m.setRole(dto.editRole);
                break;
            }
        }

        // 저장
        if (JsonUtil.saveToJson(membersPath, members)) {
            result = JsonUtil.success(true, dto.editId);
            JsonUtil.logWrite("INFO", "editMemberRole", dto.id, "멤버 역할 변경 성공: " + dto.editId + " -> " + dto.editRole);
        } else {
            result = JsonUtil.success(false, "SAVE_FAILED");
            JsonUtil.logWrite("ERROR", "editMemberRole", dto.id, "members.json 저장 실패: " + uuid);
        }
        out.println(result);
    }

    public static void editMemberPermission(JsonObject data, PrintWriter out) {
        EditMemberPermissionData dto = JsonUtil.parseToDto(data, EditMemberPermissionData.class);
        String result;

        // UUID 찾기
        String uuid = ProjectManager.findProjectUuidByTitle(dto.id, dto.project);
        if (uuid == null) {
            result = JsonUtil.success(false, "PROJECT_NOT_FOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "editMemberPermission", dto.id, "프로젝트를 찾을 수 없음: " + dto.project);
            return;
        }

        // 경로 설정
        String membersPath = FilePathsUtil.PROJECT_PATH + uuid + FilePathsUtil.MEMBERS_JSON_PATH;
        File membersFile = new File(membersPath);
        if (!membersFile.exists()) {
            result = JsonUtil.success(false, "PROJECT_NOT_FOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "editMemberPermission", dto.id, "members.json 파일이 존재하지 않음: " + uuid);
            return;
        }

        // 멤버 로드
        Type listType = new TypeToken<List<MemberJson>>() {}.getType();
        List<MemberJson> members = JsonUtil.loadFromJson(membersPath, listType);
        if (members == null) members = new ArrayList<>();

        // owner 권한 확인
        boolean isOwner = members.stream()
            .anyMatch(m -> m.getId().equals(dto.id) && m.getPermission().equals("owner"));
        if (!isOwner) {
            result = JsonUtil.success(false, "PERMISSION_DENIED");
            out.println(result);
            JsonUtil.logWrite("WARN", "editMemberPermission", dto.id, "권한 거부: " + dto.project);
            return;
        }

        // 멤버가 없다면
        boolean memberExists = members.stream()
            .anyMatch(m -> m.getId().equals(dto.editId));
        if (!memberExists) {
            result = JsonUtil.success(false, "MEMBER_NOT_FOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "editMemberPermission", dto.id, "수정할 멤버가 없음: " + dto.editId);
            return;
        }

        // 권한 값 유효성 검사
        if (!ValidatorUtil.isValidPermission(dto.editPermission)) {
            result = JsonUtil.success(false, "INVALID_PERMISSION_VALUE");
            out.println(result);
            JsonUtil.logWrite("WARN", "editMemberPermission", dto.id, "잘못된 권한 값: " + dto.editPermission);
            return;
        }

        for (MemberJson m : members) {
            if (m.getId().equals(dto.editId)) {
                m.setPermission(dto.editPermission);
                break;
            }
        }

        // 저장
        if (JsonUtil.saveToJson(membersPath, members)) {
            result = JsonUtil.success(true, dto.editId);
            JsonUtil.logWrite("INFO", "editMemberPermission", dto.id, "멤버 권한 변경 성공: " + dto.editId + " -> " + dto.editPermission);
        } else {
            result = JsonUtil.success(false, "SAVE_FAILED");
            JsonUtil.logWrite("ERROR", "editMemberPermission", dto.id, "members.json 저장 실패: " + uuid);
        }
        out.println(result);
    }

    public static void fetchMember(JsonObject data, PrintWriter out) {
        FetchMemberData dto = JsonUtil.parseToDto(data, FetchMemberData.class);
        String result;

        // uuid->title
        String uuid = ProjectManager.findProjectUuidByTitle(dto.id, dto.project);
        if (uuid == null) {
            result = JsonUtil.success(false, "PROJECT_NOT_FOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "fetchMember", dto.id, "프로젝트를 찾을 수 없음: " + dto.project);
            return;
        }

        // 멤버 load
        String membersPath = FilePathsUtil.PROJECT_PATH + uuid + FilePathsUtil.MEMBERS_JSON_PATH;
        Type listType = new TypeToken<List<MemberJson>>() {}.getType();
        List<MemberJson> members = JsonUtil.loadFromJson(membersPath, listType);

        // 멤버 리스트 보냄
        if (members == null || members.isEmpty()) {
            result = JsonUtil.success(false, "NO_MEMBERS");
            out.println(result);
            JsonUtil.logWrite("INFO", "fetchMember", dto.id, "멤버가 없음: " + dto.project);
        } else {
            result = JsonUtil.success(true, members);
            out.println(result);
            JsonUtil.logWrite("INFO", "fetchMember", dto.id, "멤버 조회 성공: " + dto.project);
        }
    }
}
