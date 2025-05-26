package service;

import model.Project;
import util.JsonUtil;

import java.util.*;

public class ProjectService {
    public void createProject(String id, String title, String deadline) {
        List<Project> projects = JsonUtil.loadProjects();
        for (Project p : projects) {
            if (p.getId().equals(id) && p.getTitle().equals(title)) {
                System.out.println("이미 같은 이름의 프로젝트가 존재합니다.");
                return;
            }
        }
        projects.add(new Project(id, title, deadline));
        JsonUtil.saveProjects(projects);
        System.out.println("프로젝트가 생성되었습니다.");
    }

    public void deleteProject(String id, String title) {
        List<Project> projects = JsonUtil.loadProjects();
        projects.removeIf(p -> p.getId().equals(id) && p.getTitle().equals(title));
        JsonUtil.saveProjects(projects);
        System.out.println("프로젝트가 삭제되었습니다.");
    }

    public void listProjects(String id) {
        List<Project> projects = JsonUtil.loadProjects();
        for (Project p : projects) {
            if (p.getId().equals(id)) {
                System.out.println("▶ " + p.getTitle() + " (마감: " + p.getDeadline() + ")");
            }
        }
    }

    public void editProjectTitle(String id, String oldTitle, String newTitle) {
        List<Project> projects = JsonUtil.loadProjects();
        for (Project p : projects) {
            if (p.getId().equals(id) && p.getTitle().equals(oldTitle)) {
                p.setTitle(newTitle);
                JsonUtil.saveProjects(projects);
                System.out.println("제목이 수정되었습니다.");
                return;
            }
        }
        System.out.println("해당 프로젝트를 찾을 수 없습니다.");
    }

    public void editProjectDeadline(String id, String title, String newDeadline) {
        List<Project> projects = JsonUtil.loadProjects();
        for (Project p : projects) {
            if (p.getId().equals(id) && p.getTitle().equals(title)) {
                p.setDeadline(newDeadline);
                JsonUtil.saveProjects(projects);
                System.out.println("마감일이 수정되었습니다.");
                return;
            }
        }
        System.out.println("해당 프로젝트를 찾을 수 없습니다.");
    }
}
