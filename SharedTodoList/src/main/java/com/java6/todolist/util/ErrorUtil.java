package com.java6.todolist.util;

public class ErrorUtil {

    public static void handleError(String errorCode) {
        switch (errorCode) {
            case "ID_DUPLICATE":
                System.out.println("이미 존재하는 ID입니다.");
                break;
            case "SAVE_FAILED":
                System.out.println("저장 중 오류가 발생했습니다.");
                break;
            case "ID_NOT_FOUND":
                System.out.println("ID가 존재하지 않습니다.");
                break;
            case "WRONG_PASSWORD":
                System.out.println("비밀번호가 틀렸습니다.");
                break;
            case "USER_NOT_FOUND":
                System.out.println("사용자 정보를 찾을 수 없습니다.");
                break;
            case "PROJECT_NOT_FOUND":
                System.out.println("해당 프로젝트가 존재하지 않습니다.");
                break;
            case "PROJECT_ALREADY_EXISTS":
                System.out.println("이미 존재하는 프로젝트입니다.");
                break;
            case "INVALID_DATE_FORMAT":
                System.out.println("날짜 형식이 잘못되었습니다. 예: 2025-5-5 1:00");
                break;
            case "INVALID_DATE_VALUE":
                System.out.println("존재할 수 없는 날짜입니다. 예: 2월 31일");
                break;
            case "INVALID_DATE_RANGE":
                System.out.println("시작 기한이 마감 기한보다 늦을 수 없습니다.");
                break;
            case "PERMISSION_DENIED":
                System.out.println("해당 작업을 수행할 권한이 없습니다.");
                break;
            case "TITLE_DUPLICATE":
                System.out.println("제목이 중복됩니다.");
                break;
            case "INDEX_OUT_OF_BOUND":
                System.out.println("유효하지 않은 인덱스입니다.");
                break;
            case "MEMBER_NOT_FOUND":
                System.out.println("해당 멤버를 찾을 수 없습니다.");
                break;
            case "ALREADY_ASSIGNED":
                System.out.println("이미 할당된 멤버입니다.");
                break;
            case "ASSIGNED_NOT_FOUND":
                System.out.println("할당되지 않은 멤버입니다.");
                break;
            case "NO_SCHEDULES":
                System.out.println("스케줄이 존재하지 않습니다.");
                break;
            case "INVALID_PERMISSION_VALUE":
                System.out.println("유효하지 않은 권한 값입니다.");
                break;
            case "NO_MEMBER":
                System.out.println("멤버가 존재하지 않습니다.");
                break;
            case "PROJECT_EXPIRED":
                System.out.println("이미 종료된 프로젝트입니다.");
                break;
            case "NO_PROJECT" :
            	System.out.println("참여 중인 프로젝트가 업습니다.");
                break;
            default:
                System.out.println("알 수 없는 오류가 발생했습니다. 다시 시도해주세요.");
        }
    }
}

