# STL(Project Shared TodoList) - 프로젝트 공유 투두리스트

> CLI기반 프로젝트 관리 공유 투두리스트

[![JAVA](https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white)](https://www.java.com/ko/)



## 📌 프로젝트 소개

STL은 FileLock과 소켓 통신을 이용하여 프로젝트 일정 관리를 용이하게 해주는 CLI 기반 공유 투두리스트입니다.

### 주요 해결 과제
- 프로젝트 일정 관리하는 프로그램 사용 복잡도 해결
- CLI 기반 일정 관리 프로그램 제공

## ✨ 주요 기능
- 👤 **사용자**: 다중 사용자 접속 가능 및 로그인, 세션 관리
- 📡 **프로젝트 관리**: 프로젝트 추가 및 삭제, 수정
- 👕 **일정 관리**: 프로젝트 내부 일정 추가 및 삭제, 수정 (프로젝트 별 일정 설정 가능)
- 🛒 **멤버 관리**: 프로젝트 내부 팀원 추가 및 삭제, 수정 (권한/역할 설정 가능)

## 🛠️ 사용 기술

### Core
- **Java**: openjdk 21.0.6 2025-01-21 LTS 기준 개발
- **CLI 기반 인터페이스**: expo-router (파일 기반 라우팅)  
- **JSON 처리**: GSON 라이브러리로 데이터 직렬화/역직렬화
- **소켓 통신**: ServerSocket, Socket을 이용한 클라이언트-서버 구조
- **동시성 제어**: FileLock을 사용하여 파일 동시 접근 방지

### Build & Dependency Management
- **Apache Maven**: 프로젝트 의존성 관리 및 빌드 자동화
- **CLI 기반 인터페이스**: expo-router (파일 기반 라우팅)  

### DevOps & Collaboration
- **Version Control**: Git & GitHub
- **Project Repository**: https://github.com/JavaProgramming6/ProjectTodoList

## 🚀 시작하기

### 필요 사항
- Java (v21 권장)
- Maven (v3.9.10 권장)
  
### 설치 방법(powerShell 기준)

1. **저장소 클론**
   ```bash
   git clone https://github.com/JavaProgramming6/ProjectTodoList.git
   cd ProjectTodoList\SharedTodoList
   ```

2. **컴파일**
   ```bash
   mvn clean compile
   ```
3. **서버 실행**
   ```bash
   mvn exec:java "-Dexec.mainClass=com.java6.todolist.ServerMain"
   ```
4. **클라이언트 실행**
   ```bash
   mvn exec:java "-Dexec.mainClass=com.java6.todolist.ClientMain"
   ```

## 📁 프로젝트 구조

```
com.java6/
├── todolist/
│   ├── format/ # 데이터 포맷팅을 위한 클래스
│   ├── manager/
│   │   ├── MemberManager.java   # 멤버 처리
│   │   ├── MenuManager.java     # 클라이언트 메뉴 출력
│   │   ├── ScheduleManager.java # 일정 처리
│   │   ├── UserManager.java     # 유저 정보 처리
│   │   └── ProjectManager.java  # 프로젝트 처리 
│   ├── util/
│   │   ├── Validator.java       # 유효성 처리
│   │   ├── JsonUtil.java        # JSON 관련 파일/폴더 처리
│   │   ├── FilePathsUtil.java   # 파일 경로 저장 클래스
│   │   ├── ErrorUtil.java       # 에러 출력 처리
│   │   ├── CommandUtil.java     # 유저 입력 처리
│   │   └── AuthUtil.java        # 비밀번호 처리
│   ├── Client.java              # 유저 정보 클래스
│   ├── CkuentHandler.java       # 클라이언트 입출력 처리
│   ├── ClientMain.java          # 클라이언트 실행
│   └── ServerMain.java          # 서버 실행


```

## 📊 json 형식

### user.json: 유저 아이디, 비밀번호, 이름
```json
[
   {
      "id": "id1",
      "password": "hashing password1",
      "name": "name1"
   },
   {
      "id": "id2",
      "password": "hashing password2",
      "name": "name2"
   }
]
```

### user_projects.json: 유저 프로젝트 리스트
```json
[
   {
      "id": "id1",
      "projects": {
         "hashing project1": "project1",
         "hashing project2": "project2"
      }
   },
   {
      "id": "id2",
      "projects": {
         "hashing project1": "project1",
         "hashing project3": "project3"
      }
   },
   
]
```

### members.json: 프로젝트 내부 팀원 아이디, 역할, 권한
```json
[
   {
      "id": "id1",
      "role": "role1",
      "permission": "permission1"
   },
   {
      "id": "id2",
      "role": "role2",
      "permission": "permission2"
   },
]
```

### project.json: 해당 프로젝트 명, 마감일
```json
[
   "title": "project Name",
   "deadline": "yyyy-MM-dd HH:mm"
]
```

### schedule.json: 프로젝트 내부 스케줄(스케줄명, 시작일, 마감일, 할당 팀원)
```json
[
   {
      "title": "title1",
      "start": "yyyy-MM-dd HH:mm",
      "end": "yyyy-MM-dd HH:mm",
      "assigned": [
         "id1", "id2"
      ]
   },
   {
      "title": "title2",
      "start": "yyyy-MM-dd HH:mm",
      "end": "yyyy-MM-dd HH:mm",
      "assigned": [
         "id1", "id2", "id3"
      ]
   },
]
```

## 📽️ 시연 영상(2025.06.09. 기준) - 추가 예정

[![시연 영상](https://youtu.be/WPmSpjV2mQg)](https://youtu.be/WPmSpjV2mQg)


## 👥 팀원

- **박규민** - 팀장 및 개발
  - 프로젝트 관리 기능 구현
  - 발표 자료 제작 및 발표
  
- **김문기** - 개발
  - 프로젝트 전체 흐름 구상 및 구현 방법 제안
  - 소켓 통신 구현
  - 파일 작업 함수 구현
  - 서버 기능 구현
    
- **강현서** – 개발
  - 화면 입출력 디자인 및 구조 정비
  - 발표 자료 제작
 
- **박재민** – 개발
  - 일정 관리 기능 구현
  - 클라이언트-서버 코드 연결
  - 테스트 및 빌드 관리
  

## 🔮 향후 계획

- [ ] 공란


## 📄 라이선스

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

<p align="center">Made with ❤️ by STL Team</p>
