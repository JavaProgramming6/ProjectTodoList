package com.java6.todolist.util;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Set;

import com.google.gson.reflect.TypeToken;
import com.java6.todolist.format.*;

public class ValidatorUtil {

	private static final String regex = "^"
		    + "(\\d{4})-"              // 연도: 4자리
		    + "(0[1-9]|1[0-2])-"       // 월: 01~12
		    + "(0[1-9]|[12][0-9]|3[01])\\s" // 일: 01~31
		    + "(0[0-9]|1[0-9]|2[0-3]):"     // 시: 00~23
		    + "([0-5][0-9])$";              // 분: 00~59

    //날짜 포맷
	private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
		    .appendPattern("yyyy-MM-dd HH:mm")
		    .parseDefaulting(java.time.temporal.ChronoField.ERA, 1) // ← 이거 꼭 추가
		    .toFormatter()
		    .withResolverStyle(ResolverStyle.STRICT);

    //날짜 포맷 결과 종류
    public enum DateValidationResult {
        VALID,
        INVALID_FORMAT,
        INVALID_VALUE
    }

    //날짜 포맷 확인
    public static DateValidationResult validateDeadline(String deadlineRaw) {
    	String deadline = deadlineRaw.trim()
    	        .replace('–', '-')  // en-dash (U+2013)
    	        .replace('−', '-'); // minus sign (U+2212);
    	if (!deadline.matches(regex)) {
            return DateValidationResult.INVALID_FORMAT;
        }

    	try {
    	    LocalDateTime.parse(deadline, formatter);
    	    return DateValidationResult.VALID;
    	} catch (DateTimeParseException e) {
    	    System.out.println("❗ 파싱 실패 메시지: " + e.getMessage());
    	    e.printStackTrace();  // 전체 스택 추적
    	    return DateValidationResult.INVALID_VALUE;
    	}
    }

    //유저(멤버) 권한 종류
    private static final Set<String> VALID_PERMISSIONS = Set.of("owner", "editor", "viewer");

    //권한 존재 검사
    public static boolean isValidPermission(String permission) {
        return VALID_PERMISSIONS.contains(permission);
    }

    //권한 유효성 검사
    public static boolean isWritablePermission(String uuid, String id) {
    	String membersPath = FilePathsUtil.PROJECT_PATH + uuid + FilePathsUtil.MEMBERS_JSON_PATH;
	    Type listType = new TypeToken<List<MemberJson>>() {}.getType();
	    List<MemberJson> members = JsonUtil.loadFromJson(membersPath, listType);
	    if (members == null) {
	        return false;
	    }

	    String permission = members.stream()
	        .filter(m -> m.getId().equals(id))
	        .map(MemberJson::getPermission)
	        .findFirst()
	        .orElse(null);

        return permission.equals("owner") || permission.equals("editor");
    }

    //시작기한과 마감기한 유효성 검사(start < end)
    public static boolean isStartBeforeEnd(String start, String end) {
        try {
            LocalDateTime startTime = LocalDateTime.parse(start, formatter);
            LocalDateTime endTime = LocalDateTime.parse(end, formatter);
            return startTime.isBefore(endTime);
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    
    //멤버 존재 여부
    public static boolean isMemberExists(String id, String uuid) {
    	String membersPath = FilePathsUtil.PROJECT_PATH + uuid + FilePathsUtil.MEMBERS_JSON_PATH;
    	Type listType = new TypeToken<List<MemberJson>>() {}.getType();
	    List<MemberJson> members = JsonUtil.loadFromJson(membersPath, listType);

    	return members != null && members.stream().anyMatch(u -> u.getId().equals(id));
    }
    
    //유저 존재 여부
    public static boolean isUserExists(String id) {
    	Type listType = new TypeToken<List<UserJson>>() {}.getType();
        List<UserJson> users = JsonUtil.loadFromJson(FilePathsUtil.USERS_JSON_PATH, listType);

    	return users != null && users.stream().anyMatch(u -> u.getId().equals(id));
    }
    
    //마감된 프로젝트 여부
    public static boolean isEnd(String deadline) {
        try {
            LocalDateTime startTime = LocalDateTime.now();
            LocalDateTime endTime = LocalDateTime.parse(deadline, formatter);

            return startTime.isBefore(endTime);
        } catch (DateTimeParseException e) {
            return false;
        }
    }
} 
                        