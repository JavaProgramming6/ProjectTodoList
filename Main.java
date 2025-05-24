import service.ProjectService;
import java.util.Scanner;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final ProjectService ps = new ProjectService();
    
    public static void main(String[] args) {
        System.out.print("아이디 입력: ");
        String id = sc.nextLine();
        mainMenu(id);
       }
       public static void mainMenu(String id) {
           while (true) {
               System.out.println("\n[프로젝트 메인화면]");
               System.out.println("1. 프로젝트 생성");
               System.out.println("2. 프로젝트 삭제");
               System.out.println("3. 프로젝트 관리");
               System.out.println("4. 돌아가기");
               System.out.print("선택: ");
               int sel = sc.nextInt(); sc.nextLine();

               switch (sel) {
                   case 1 -> createProject(id);
                   case 2 -> deleteProject(id);
                   case 3 -> manageProject(id);
                   case 4 -> {
                       System.out.println("메인으로 돌아갑니다.");
                       return;
                   }
                   default -> System.out.println("잘못된 선택입니다.");
               }
           }
       }

       public static void createProject(String id) {
           System.out.print("제목: ");
           String title = sc.nextLine();
           System.out.print("마감일: ");
           String deadline = sc.nextLine();
           ps.createProject(id, title, deadline);
       }

       public static void deleteProject(String id) {
           System.out.print("삭제할 제목: ");
           String title = sc.nextLine();
           ps.deleteProject(id, title);
       }

       public static void manageProject(String id) {
           while (true) {
               System.out.println("\n[프로젝트 관리]");
               System.out.println("1. 프로젝트 선택");
               System.out.println("2. 프로젝트 목록 보기");
               System.out.println("3. 돌아가기");
               System.out.print("선택: ");
               int sel = sc.nextInt(); sc.nextLine();

               switch (sel) {
                   case 1 -> selectProject(id);
                   case 2 -> ps.listProjects(id);
                   case 3 -> {
                       System.out.println("뒤로 갑니다.");
                       return;
                   }
                   default -> System.out.println("잘못된 선택입니다.");
               }
           }
       }

       public static void selectProject(String id) {
           System.out.print("선택할 프로젝트 제목: ");
           String title = sc.nextLine();

           while (true) {
               System.out.println("\n[프로젝트 선택 - " + title + "]");
               System.out.println("1. 프로젝트 수정");
               System.out.println("2. 스케줄 관리 (미구현)");
               System.out.println("3. 멤버 관리 (미구현)");
               System.out.println("4. 돌아가기");
               System.out.print("선택: ");
               int sel = sc.nextInt(); sc.nextLine();

               switch (sel) {
                   case 1 -> editProject(id, title);
                   case 2, 3 -> System.out.println("해당 기능은 아직 구현되지 않았습니다.");
                   case 4 -> {
                       System.out.println("프로젝트 관리로 돌아갑니다.");
                       return;
                   }
                   default -> System.out.println("잘못된 선택입니다.");
               }
           }
       }

       public static void editProject(String id, String title) {
           while (true) {
               System.out.println("\n[프로젝트 수정 - " + title + "]");
               System.out.println("1. 제목 수정");
               System.out.println("2. 마감기한 수정");
               System.out.println("3. 돌아가기");
               System.out.print("선택: ");
               int sel = sc.nextInt(); sc.nextLine();

               switch (sel) {
                   case 1 -> {
                       System.out.print("새 제목: ");
                       String newTitle = sc.nextLine();
                       ps.editProjectTitle(id, title, newTitle);
                       title = newTitle; // 제목이 바뀌었으니 변수도 갱신
                   }
                   case 2 -> {
                       System.out.print("새 마감기한: ");
                       String newDeadline = sc.nextLine();
                       ps.editProjectDeadline(id, title, newDeadline);
                   }
                   case 3 -> {
                       System.out.println("프로젝트 선택 화면으로 돌아갑니다.");
                       return;
                   }
                   default -> System.out.println("잘못된 선택입니다.");
               }
           }
       }
 }
