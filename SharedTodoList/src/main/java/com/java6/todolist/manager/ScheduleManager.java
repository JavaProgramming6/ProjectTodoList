package com.java6.todolist.manager;

import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.java6.todolist.format.*;
import com.java6.todolist.util.*;

public class ScheduleManager {

    // 스케줄 추가
    public static void addSchedule(JsonObject data, PrintWriter out) {
        AddScheduleData dto = JsonUtil.parseToDto(data, AddScheduleData.class);
        String result;

        // title->uuid
        String uuid = ProjectManager.findProjectUuidByTitle(dto.id, dto.project);
        if (uuid == null) {
            result = JsonUtil.success(false, "PROJECT_NOT_FOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "addSchedule", dto.id, "프로젝트를 찾을 수 없음: " + dto.project);
            return;
        }
        
        
        String projectJsonPath = FilePathsUtil.PROJECT_PATH + uuid + FilePathsUtil.PROJECT_JSON_PATH;
        ProjectJson project = JsonUtil.loadFromJson(projectJsonPath, ProjectJson.class);
        
        if(!ValidatorUtil.isEnd(project.getDeadline())) {
        	result = JsonUtil.success(false, "PROJECT_EXPIRED");
            out.println(result);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formatted = LocalDateTime.now().format(formatter);
            JsonUtil.logWrite("WARN", "addSchedule", dto.id,
                              "프로젝트 기한 초과: 현재시간=" + formatted
                              + ", 마감기한=" + project.getDeadline());
            return;
        }
        
        
        // 수정 권한 확인
        if (!ValidatorUtil.isWritablePermission(uuid, dto.id)) {
            result = JsonUtil.success(false, "PERMISSION_DENIED");
            out.println(result);
            JsonUtil.logWrite("WARN", "addSchedule", dto.id, "권한 거부: 스케줄 추가 불가, 프로젝트 UUID=" + uuid);
            return;
        }

        // 날짜 포맷 확인(start)
        ValidatorUtil.DateValidationResult validationResult = ValidatorUtil.validateDeadline(dto.start);
        switch (validationResult) {
            case INVALID_FORMAT:
                result = JsonUtil.success(false, "INVALID_DATE_FORMAT");
                out.println(result);
                JsonUtil.logWrite("WARN", "addSchedule", dto.id, "잘못된 날짜 형식(start): " + dto.start);
                return;
            case INVALID_VALUE:
                result = JsonUtil.success(false, "INVALID_DATE_VALUE");
                out.println(result);
                JsonUtil.logWrite("WARN", "addSchedule", dto.id, "날짜 값 오류(start): " + dto.start);
                return;
            default:
                break;
        }

        // 날짜 포맷 확인(end)
        validationResult = ValidatorUtil.validateDeadline(dto.end);
        switch (validationResult) {
            case INVALID_FORMAT:
                result = JsonUtil.success(false, "INVALID_DATE_FORMAT");
                out.println(result);
                JsonUtil.logWrite("WARN", "addSchedule", dto.id, "잘못된 날짜 형식(end): " + dto.end);
                return;
            case INVALID_VALUE:
                result = JsonUtil.success(false, "INVALID_DATE_VALUE");
                out.println(result);
                JsonUtil.logWrite("WARN", "addSchedule", dto.id, "날짜 값 오류(end): " + dto.end);
                return;
            default:
                break;
        }

        // 날짜 유효성 검사(start < end)
        if (!ValidatorUtil.isStartBeforeEnd(dto.start, dto.end)) {
            result = JsonUtil.success(false, "INVALID_DATE_RANGE");
            out.println(result);
            JsonUtil.logWrite("WARN", "addSchedule", dto.id, "날짜 범위 오류: start=" + dto.start + ", end=" + dto.end);
            return;
        }

        dto.assigned.removeIf(id -> id == null || id.isBlank());
        // 할당인원 id 유효성 확인
        if (dto.assigned != null && !dto.assigned.isEmpty()
                && dto.assigned.stream().anyMatch(u -> !ValidatorUtil.isMemberExists(u, uuid))) {
            result = JsonUtil.success(false, "MEMBER_NOT_FOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "addSchedule", dto.id, "할당된 멤버를 찾을 수 없음: " + dto.assigned);
            return;
        }

        // 경로 지정
        String schedulesPath = FilePathsUtil.PROJECT_PATH + uuid + FilePathsUtil.SCHEDULE_JSON_PATH;
        Type listType = new TypeToken<List<ScheduleJson>>() {}.getType();
        List<ScheduleJson> schedules = JsonUtil.loadFromJson(schedulesPath, listType);
        if (schedules == null) schedules = new ArrayList<>();

        ScheduleJson newSchedule = new ScheduleJson(dto.title, dto.start, dto.end, dto.assigned);
        // 스케줄 추가
        schedules.add(newSchedule);

        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        // 정렬
        schedules.sort(Comparator.comparing(s -> LocalDateTime.parse(s.getStart(), format)));

        // 저장
        if (JsonUtil.saveToJson(schedulesPath, schedules)) {
            result = JsonUtil.success(true, dto.title);
            out.println(result);
            JsonUtil.logWrite("INFO", "addSchedule", dto.id, "스케줄 추가 성공: " + dto.title + " (프로젝트 UUID=" + uuid + ")");
        } else {
            result = JsonUtil.success(false, "SAVE_FAILED");
            out.println(result);
            JsonUtil.logWrite("ERROR", "addSchedule", dto.id, "스케줄 저장 실패: " + dto.title + " (프로젝트 UUID=" + uuid + ")");
        }
    }

    // 스케줄 삭제
    public static void deleteSchedule(JsonObject data, PrintWriter out) {
        DeleteScheduleData dto = JsonUtil.parseToDto(data, DeleteScheduleData.class);
        String result;

        // title->uuid
        String uuid = ProjectManager.findProjectUuidByTitle(dto.id, dto.project);
        if (uuid == null) {
            result = JsonUtil.success(false, "PROJECT_NOT_FOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "deleteSchedule", dto.id, "프로젝트를 찾을 수 없음: " + dto.project);
            return;
        }
        
        String projectJsonPath = FilePathsUtil.PROJECT_PATH + uuid + FilePathsUtil.PROJECT_JSON_PATH;
        ProjectJson project = JsonUtil.loadFromJson(projectJsonPath, ProjectJson.class);
        
        if(!ValidatorUtil.isEnd(project.getDeadline())) {
        	result = JsonUtil.success(false, "PROJECT_EXPIRED");
            out.println(result);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formatted = LocalDateTime.now().format(formatter);
            JsonUtil.logWrite("WARN", "addSchedule", dto.id,
                              "프로젝트 기한 초과: 현재시간=" + formatted
                              + ", 마감기한=" + project.getDeadline());
            return;
        }
        

        // 수정 권한 검사
        if (!ValidatorUtil.isWritablePermission(uuid, dto.id)) {
            result = JsonUtil.success(false, "PERMISSION_DENIED");
            out.println(result);
            JsonUtil.logWrite("WARN", "deleteSchedule", dto.id, "권한 거부: 스케줄 삭제 불가, 프로젝트 UUID=" + uuid);
            return;
        }

        // 경로 지정
        String schedulesPath = FilePathsUtil.PROJECT_PATH + uuid + FilePathsUtil.SCHEDULE_JSON_PATH;
        Type listType = new TypeToken<List<ScheduleJson>>() {}.getType();
        List<ScheduleJson> schedules = JsonUtil.loadFromJson(schedulesPath, listType);

        // 인덱스 초과 여부 및 null 판단
        if (schedules == null || dto.index - 1 < 0 || dto.index > schedules.size()) {
            result = JsonUtil.success(false, "INDEX_OUT_OF_BOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "deleteSchedule", dto.id, "인덱스 범위 초과: index=" + dto.index);
            return;
        }

        // 삭제
        ScheduleJson removed = schedules.remove(dto.index - 1);

        // 저장
        if (JsonUtil.saveToJson(schedulesPath, schedules)) {
            result = JsonUtil.success(true, null);
            out.println(result);
            JsonUtil.logWrite("INFO", "deleteSchedule", dto.id,
                    "스케줄 삭제 성공: " + removed.getTitle() + " (인덱스=" + dto.index + ")");
        } else {
            result = JsonUtil.success(false, "SAVE_FAILED");
            out.println(result);
            JsonUtil.logWrite("ERROR", "deleteSchedule", dto.id, "스케줄 삭제 저장 실패: 인덱스=" + dto.index);
        }
    }

    // 제목 수정
    public static void editScheduleTitle(JsonObject data, PrintWriter out) {
        EditScheduleTitleData dto = JsonUtil.parseToDto(data, EditScheduleTitleData.class);
        String result;

        // title->uuid
        String uuid = ProjectManager.findProjectUuidByTitle(dto.id, dto.project);
        if (uuid == null) {
            result = JsonUtil.success(false, "PROJECT_NOT_FOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "editScheduleTitle", dto.id, "프로젝트를 찾을 수 없음: " + dto.project);
            return;
        }
        
        String projectJsonPath = FilePathsUtil.PROJECT_PATH + uuid + FilePathsUtil.PROJECT_JSON_PATH;
        ProjectJson project = JsonUtil.loadFromJson(projectJsonPath, ProjectJson.class);
        
        if(!ValidatorUtil.isEnd(project.getDeadline())) {
        	result = JsonUtil.success(false, "PROJECT_EXPIRED");
            out.println(result);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formatted = LocalDateTime.now().format(formatter);
            JsonUtil.logWrite("WARN", "addSchedule", dto.id,
                              "프로젝트 기한 초과: 현재시간=" + formatted
                              + ", 마감기한=" + project.getDeadline());
            return;
        }
        

        // 수정 권한 판단
        if (!ValidatorUtil.isWritablePermission(uuid, dto.id)) {
            result = JsonUtil.success(false, "PERMISSION_DENIED");
            out.println(result);
            JsonUtil.logWrite("WARN", "editScheduleTitle", dto.id, "권한 거부: 제목 수정 불가, UUID=" + uuid);
            return;
        }

        // 경로 지정
        String schedulesPath = FilePathsUtil.PROJECT_PATH + uuid + FilePathsUtil.SCHEDULE_JSON_PATH;
        Type listType = new TypeToken<List<ScheduleJson>>() {}.getType();
        List<ScheduleJson> schedules = JsonUtil.loadFromJson(schedulesPath, listType);

        if (schedules == null || dto.index - 1 < 0 || dto.index > schedules.size()) {
            result = JsonUtil.success(false, "INDEX_OUT_OF_BOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "editScheduleTitle", dto.id, "인덱스 범위 초과: index=" + dto.index);
            return;
        }

        String oldTitle = schedules.get(dto.index - 1).getTitle();
        // 제목 수정
        schedules.get(dto.index - 1).setTitle(dto.editTitle);

        // 저장
        if (JsonUtil.saveToJson(schedulesPath, schedules)) {
            result = JsonUtil.success(true, dto.editTitle);
            out.println(result);
            JsonUtil.logWrite("INFO", "editScheduleTitle", dto.id,
                    "스케줄 제목 변경 성공: '" + oldTitle + "' -> '" + dto.editTitle + "' (인덱스=" + dto.index + ")");
        } else {
            result = JsonUtil.success(false, "SAVE_FAILED");
            out.println(result);
            JsonUtil.logWrite("ERROR", "editScheduleTitle", dto.id, "스케줄 제목 수정 저장 실패: index=" + dto.index);
        }
    }

    // 시작기한 수정
    public static void editScheduleStartDate(JsonObject data, PrintWriter out) {
        EditScheduleDateData dto = JsonUtil.parseToDto(data, EditScheduleDateData.class);
        String result;

        // title->uuid
        String uuid = ProjectManager.findProjectUuidByTitle(dto.id, dto.project);
        if (uuid == null) {
            result = JsonUtil.success(false, "PROJECT_NOT_FOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "editScheduleStartDate", dto.id, "프로젝트를 찾을 수 없음: " + dto.project);
            return;
        }
        
        String projectJsonPath = FilePathsUtil.PROJECT_PATH + uuid + FilePathsUtil.PROJECT_JSON_PATH;
        ProjectJson project = JsonUtil.loadFromJson(projectJsonPath, ProjectJson.class);
        
        if(!ValidatorUtil.isEnd(project.getDeadline())) {
        	result = JsonUtil.success(false, "PROJECT_EXPIRED");
            out.println(result);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formatted = LocalDateTime.now().format(formatter);
            JsonUtil.logWrite("WARN", "addSchedule", dto.id,
                              "프로젝트 기한 초과: 현재시간=" + formatted
                              + ", 마감기한=" + project.getDeadline());
            return;
        }
        

        // 수정 권한 판단
        if (!ValidatorUtil.isWritablePermission(uuid, dto.id)) {
            result = JsonUtil.success(false, "PERMISSION_DENIED");
            out.println(result);
            JsonUtil.logWrite("WARN", "editScheduleStartDate", dto.id, "권한 거부: 시작기한 수정 불가, UUID=" + uuid);
            return;
        }

        // 일정 리스트 불러오기
        String schedulesPath = FilePathsUtil.PROJECT_PATH + uuid + FilePathsUtil.SCHEDULE_JSON_PATH;
        Type listType = new TypeToken<List<ScheduleJson>>() {}.getType();
        List<ScheduleJson> schedules = JsonUtil.loadFromJson(schedulesPath, listType);

        if (schedules == null || dto.index - 1 < 0 || dto.index > schedules.size()) {
            result = JsonUtil.success(false, "INDEX_OUT_OF_BOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "editScheduleStartDate", dto.id, "인덱스 범위 초과: index=" + dto.index);
            return;
        }

        // 날짜 포맷 확인(editDate)
        ValidatorUtil.DateValidationResult validationResult = ValidatorUtil.validateDeadline(dto.editDate);
        switch (validationResult) {
            case INVALID_FORMAT:
                result = JsonUtil.success(false, "INVALID_DATE_FORMAT");
                out.println(result);
                JsonUtil.logWrite("WARN", "editScheduleStartDate", dto.id, "잘못된 날짜 형식(start 수정): " + dto.editDate);
                return;
            case INVALID_VALUE:
                result = JsonUtil.success(false, "INVALID_DATE_VALUE");
                out.println(result);
                JsonUtil.logWrite("WARN", "editScheduleStartDate", dto.id, "날짜 값 오류(start 수정): " + dto.editDate);
                return;
            default:
                break;
        }

        // start < end 판단
        String end = schedules.get(dto.index - 1).getEnd();
        if (!ValidatorUtil.isStartBeforeEnd(dto.editDate, end)) {
            result = JsonUtil.success(false, "INVALID_DATE_RANGE");
            out.println(result);
            JsonUtil.logWrite("WARN", "editScheduleStartDate", dto.id,
                    "날짜 범위 오류(start 수정): start=" + dto.editDate + ", end=" + end);
            return;
        }

        String oldStart = schedules.get(dto.index - 1).getStart();
        // 수정
        schedules.get(dto.index - 1).setStart(dto.editDate);

        // 저장
        if (JsonUtil.saveToJson(schedulesPath, schedules)) {
            result = JsonUtil.success(true, dto.editDate);
            out.println(result);
            JsonUtil.logWrite("INFO", "editScheduleStartDate", dto.id,
                    "시작기한 변경 성공: '" + oldStart + "' -> '" + dto.editDate + "' (인덱스=" + dto.index + ")");
        } else {
            result = JsonUtil.success(false, "SAVE_FAILED");
            out.println(result);
            JsonUtil.logWrite("ERROR", "editScheduleStartDate", dto.id, "시작기한 수정 저장 실패: index=" + dto.index);
        }
    }

    // 마감기한 수정
    public static void editScheduleDeadline(JsonObject data, PrintWriter out) {
        EditScheduleDateData dto = JsonUtil.parseToDto(data, EditScheduleDateData.class);
        String result;

        // title->uuid
        String uuid = ProjectManager.findProjectUuidByTitle(dto.id, dto.project);
        if (uuid == null) {
            result = JsonUtil.success(false, "PROJECT_NOT_FOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "editScheduleDeadline", dto.id, "프로젝트를 찾을 수 없음: " + dto.project);
            return;
        }
        
        String projectJsonPath = FilePathsUtil.PROJECT_PATH + uuid + FilePathsUtil.PROJECT_JSON_PATH;
        ProjectJson project = JsonUtil.loadFromJson(projectJsonPath, ProjectJson.class);
        
        if(!ValidatorUtil.isEnd(project.getDeadline())) {
        	result = JsonUtil.success(false, "PROJECT_EXPIRED");
            out.println(result);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formatted = LocalDateTime.now().format(formatter);
            JsonUtil.logWrite("WARN", "addSchedule", dto.id,
                              "프로젝트 기한 초과: 현재시간=" + formatted
                              + ", 마감기한=" + project.getDeadline());
            return;
        }
        

        // 수정 권한 판단
        if (!ValidatorUtil.isWritablePermission(uuid, dto.id)) {
            result = JsonUtil.success(false, "PERMISSION_DENIED");
            out.println(result);
            JsonUtil.logWrite("WARN", "editScheduleDeadline", dto.id, "권한 거부: 마감기한 수정 불가, UUID=" + uuid);
            return;
        }

        // 일정 리스트 불러오기
        String schedulesPath = FilePathsUtil.PROJECT_PATH + uuid + FilePathsUtil.SCHEDULE_JSON_PATH;
        Type listType = new TypeToken<List<ScheduleJson>>() {}.getType();
        List<ScheduleJson> schedules = JsonUtil.loadFromJson(schedulesPath, listType);

        if (schedules == null || dto.index - 1 < 0 || dto.index > schedules.size()) {
            result = JsonUtil.success(false, "INDEX_OUT_OF_BOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "editScheduleDeadline", dto.id, "인덱스 범위 초과: index=" + dto.index);
            return;
        }

        // 날짜 포맷 확인(editDate)
        ValidatorUtil.DateValidationResult validationResult = ValidatorUtil.validateDeadline(dto.editDate);
        switch (validationResult) {
            case INVALID_FORMAT:
                result = JsonUtil.success(false, "INVALID_DATE_FORMAT");
                out.println(result);
                JsonUtil.logWrite("WARN", "editScheduleDeadline", dto.id, "잘못된 날짜 형식(end 수정): " + dto.editDate);
                return;
            case INVALID_VALUE:
                result = JsonUtil.success(false, "INVALID_DATE_VALUE");
                out.println(result);
                JsonUtil.logWrite("WARN", "editScheduleDeadline", dto.id, "날짜 값 오류(end 수정): " + dto.editDate);
                return;
            default:
                break;
        }

        // start <= end 판단
        String start = schedules.get(dto.index - 1).getStart();
        if (!ValidatorUtil.isStartBeforeEnd(start, dto.editDate)) {
            result = JsonUtil.success(false, "INVALID_DATE_RANGE");
            out.println(result);
            JsonUtil.logWrite("WARN", "editScheduleDeadline", dto.id,
                    "날짜 범위 오류(end 수정): start=" + start + ", end=" + dto.editDate);
            return;
        }

        String oldEnd = schedules.get(dto.index - 1).getEnd();
        // 수정
        schedules.get(dto.index - 1).setEnd(dto.editDate);

        // 저장
        if (JsonUtil.saveToJson(schedulesPath, schedules)) {
            result = JsonUtil.success(true, dto.editDate);
            out.println(result);
            JsonUtil.logWrite("INFO", "editScheduleDeadline", dto.id,
                    "마감기한 변경 성공: '" + oldEnd + "' -> '" + dto.editDate + "' (인덱스=" + dto.index + ")");
        } else {
            result = JsonUtil.success(false, "SAVE_FAILED");
            out.println(result);
            JsonUtil.logWrite("ERROR", "editScheduleDeadline", dto.id, "마감기한 수정 저장 실패: index=" + dto.index);
        }
    }

    // 할당인원 추가
    public static void addScheduleAssigned(JsonObject data, PrintWriter out) {
        EditScheduleAssignedData dto = JsonUtil.parseToDto(data, EditScheduleAssignedData.class);
        String result;

        // title->uuid
        String uuid = ProjectManager.findProjectUuidByTitle(dto.id, dto.project);
        if (uuid == null) {
            result = JsonUtil.success(false, "PROJECT_NOT_FOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "addScheduleAssigned", dto.id, "프로젝트를 찾을 수 없음: " + dto.project);
            return;
        }
        
        String projectJsonPath = FilePathsUtil.PROJECT_PATH + uuid + FilePathsUtil.PROJECT_JSON_PATH;
        ProjectJson project = JsonUtil.loadFromJson(projectJsonPath, ProjectJson.class);
        
        if(!ValidatorUtil.isEnd(project.getDeadline())) {
        	result = JsonUtil.success(false, "PROJECT_EXPIRED");
            out.println(result);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formatted = LocalDateTime.now().format(formatter);
            JsonUtil.logWrite("WARN", "addSchedule", dto.id,
                              "프로젝트 기한 초과: 현재시간=" + formatted
                              + ", 마감기한=" + project.getDeadline());
            return;
        }
        

        // 수정 권한 여부
        if (!ValidatorUtil.isWritablePermission(uuid, dto.id)) {
            result = JsonUtil.success(false, "PERMISSION_DENIED");
            out.println(result);
            JsonUtil.logWrite("WARN", "addScheduleAssigned", dto.id, "권한 거부: 할당인원 추가 불가, UUID=" + uuid);
            return;
        }

        // 일정 리스트 불러오기
        String schedulesPath = FilePathsUtil.PROJECT_PATH + uuid + FilePathsUtil.SCHEDULE_JSON_PATH;
        Type listType = new TypeToken<List<ScheduleJson>>() {}.getType();
        List<ScheduleJson> schedules = JsonUtil.loadFromJson(schedulesPath, listType);

        if (schedules == null || dto.index - 1 < 0 || dto.index > schedules.size()) {
            result = JsonUtil.success(false, "INDEX_OUT_OF_BOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "addScheduleAssigned", dto.id, "인덱스 범위 초과: index=" + dto.index);
            return;
        }

        // 멤버 존재 여부
        if (!ValidatorUtil.isMemberExists(dto.assigned, uuid)) {
            result = JsonUtil.success(false, "MEMBER_NOT_FOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "addScheduleAssigned", dto.id, "할당할 멤버를 찾을 수 없음: " + dto.assigned);
            return;
        }

        // 해당 index 스케줄 불러오기 및 추가 및 중복 여부 판단
        ScheduleJson schedule = schedules.get(dto.index - 1);
        Set<String> assignedSet = schedule.getAssigned();
        if (!assignedSet.add(dto.assigned)) {
            result = JsonUtil.success(false, "ALREADY_ASSIGNED");
            out.println(result);
            JsonUtil.logWrite("WARN", "addScheduleAssigned", dto.id,
                    "이미 할당된 멤버: " + dto.assigned + " (스케줄 인덱스=" + dto.index + ")");
            return;
        }

        // 수정
        schedule.setAssigned(assignedSet);

        // 저장
        if (JsonUtil.saveToJson(schedulesPath, schedules)) {
            result = JsonUtil.success(true, dto.assigned);
            out.println(result);
            JsonUtil.logWrite("INFO", "addScheduleAssigned", dto.id,
                    "할당인원 추가 성공: " + dto.assigned + " (스케줄 인덱스=" + dto.index + ")");
        } else {
            result = JsonUtil.success(false, "SAVE_FAILED");
            out.println(result);
            JsonUtil.logWrite("ERROR", "addScheduleAssigned", dto.id, "할당인원 추가 저장 실패: index=" + dto.index);
        }
    }

    // 할당인원 삭제
    public static void deleteScheduleAssigned(JsonObject data, PrintWriter out) {
        EditScheduleAssignedData dto = JsonUtil.parseToDto(data, EditScheduleAssignedData.class);
        String result;

        // title->uuid
        String uuid = ProjectManager.findProjectUuidByTitle(dto.id, dto.project);
        if (uuid == null) {
            result = JsonUtil.success(false, "PROJECT_NOT_FOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "deleteScheduleAssigned", dto.id, "프로젝트를 찾을 수 없음: " + dto.project);
            return;
        }
        
        String projectJsonPath = FilePathsUtil.PROJECT_PATH + uuid + FilePathsUtil.PROJECT_JSON_PATH;
        ProjectJson project = JsonUtil.loadFromJson(projectJsonPath, ProjectJson.class);
        
        if(!ValidatorUtil.isEnd(project.getDeadline())) {
        	result = JsonUtil.success(false, "PROJECT_EXPIRED");
            out.println(result);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formatted = LocalDateTime.now().format(formatter);
            JsonUtil.logWrite("WARN", "addSchedule", dto.id,
                              "프로젝트 기한 초과: 현재시간=" + formatted
                              + ", 마감기한=" + project.getDeadline());
            return;
        }
        

        // 수정 권한 여부
        if (!ValidatorUtil.isWritablePermission(uuid, dto.id)) {
            result = JsonUtil.success(false, "PERMISSION_DENIED");
            out.println(result);
            JsonUtil.logWrite("WARN", "deleteScheduleAssigned", dto.id, "권한 거부: 할당인원 삭제 불가, UUID=" + uuid);
            return;
        }

        // 일정 리스트 불러오기
        String schedulesPath = FilePathsUtil.PROJECT_PATH + uuid + FilePathsUtil.SCHEDULE_JSON_PATH;
        Type listType = new TypeToken<List<ScheduleJson>>() {}.getType();
        List<ScheduleJson> schedules = JsonUtil.loadFromJson(schedulesPath, listType);

        if (schedules == null || dto.index - 1 < 0 || dto.index > schedules.size()) {
            result = JsonUtil.success(false, "INDEX_OUT_OF_BOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "deleteScheduleAssigned", dto.id, "인덱스 범위 초과: index=" + dto.index);
            return;
        }

        // 해당 index 스케줄 불러오기 및 삭제
        ScheduleJson schedule = schedules.get(dto.index - 1);
        Set<String> assignedSet = schedule.getAssigned();
        if (!assignedSet.remove(dto.assigned)) {
            result = JsonUtil.success(false, "ASSIGNED_NOT_FOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "deleteScheduleAssigned", dto.id,
                    "삭제할 할당인원을 찾을 수 없음: " + dto.assigned + " (스케줄 인덱스=" + dto.index + ")");
            return;
        }

        // 수정
        schedule.setAssigned(assignedSet);

        // 저장
        if (JsonUtil.saveToJson(schedulesPath, schedules)) {
            result = JsonUtil.success(true, dto.assigned);
            out.println(result);
            JsonUtil.logWrite("INFO", "deleteScheduleAssigned", dto.id,
                    "할당인원 삭제 성공: " + dto.assigned + " (스케줄 인덱스=" + dto.index + ")");
        } else {
            result = JsonUtil.success(false, "SAVE_FAILED");
            out.println(result);
            JsonUtil.logWrite("ERROR", "deleteScheduleAssigned", dto.id, "할당인원 삭제 저장 실패: index=" + dto.index);
        }
    }

    // 스케줄 조회
    public static void fetchSchedule(JsonObject data, PrintWriter out) {
        FetchScheduleData dto = JsonUtil.parseToDto(data, FetchScheduleData.class);
        String result;

        // title->uuid
        String uuid = ProjectManager.findProjectUuidByTitle(dto.id, dto.project);
        if (uuid == null) {
            result = JsonUtil.success(false, "PROJECT_NOT_FOUND");
            out.println(result);
            JsonUtil.logWrite("WARN", "fetchSchedule", dto.id, "프로젝트를 찾을 수 없음: " + dto.project);
            return;
        }
        

        // 일정 리스트 불러오기
        String schedulesPath = FilePathsUtil.PROJECT_PATH + uuid + FilePathsUtil.SCHEDULE_JSON_PATH;
        Type listType = new TypeToken<List<ScheduleJson>>() {}.getType();
        List<ScheduleJson> schedules = JsonUtil.loadFromJson(schedulesPath, listType);

        if (schedules == null || schedules.isEmpty()) {
            out.println(JsonUtil.success(false, "NO_SCHEDULES"));
            JsonUtil.logWrite("INFO", "fetchSchedule", dto.id, "등록된 스케줄 없음: 프로젝트=" + dto.project);
        } else {
            out.println(JsonUtil.success(true, schedules));
            JsonUtil.logWrite("INFO", "fetchSchedule", dto.id, "스케줄 조회 성공: 프로젝트=" + dto.project);
        }
    }
}
