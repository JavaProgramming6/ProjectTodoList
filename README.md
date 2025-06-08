# STL(Project Shared TodoList) - 프로젝트 공유 투두리스트

> CLI기반 프로젝트 관리 공유 투두리스트

[![JAVA](https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white)](https://www.java.com/ko/)



## 📌 프로젝트 소개

STL은 파일락과 소켓 통신을 이용하여 프로젝트 일정 관리를 용이하게 해주는 CLI 기반 공유 투두리스트입니다.

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

### DevOps & Collaboration
- **Version Control**: Git & GitHub
- **Project Repository**: https://github.com/JavaProgramming6/ProjectTodoList

## 🚀 시작하기

### 필요 사항
- Java (v21 이상)

### 설치 방법

1. **저장소 클론**
   ```bash
   git clone https://github.com/FitTwinProjectTeam/FitTwin.git
   cd FitTwin
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

## 📊 데이터 스키마

### Product Schema
```javascript
{
  title: String,
  image: String,
  price: Number,
  description: String,
  category: Enum['상의', '하의', '아우터', '신발', '액세서리', '셋업', '기타'],
  stock: Number,
  isAvailable: Boolean
}
```

### Avatar Schema
```javascript
{
  name: String,
  gender: Enum['남성', '여성'],
  height: Number,
  weight: Number,
  bodyShapeData: Object,
  createdAt: Date
}
```

### FittingResult Schema
```javascript
{
 avatarId: ObjectId (ref: 'Avatar'),
 productId: ObjectId (ref: 'Product'),
 fitScore: Number,      // AI 피팅 점수
 fitComment: String,    // 분석 코멘트
 imageURL: String,      // 피팅 시뮬레이션 이미지
 createdAt: Date
}
```

## 📽️ 시연 영상(2025.06.09. 기준) - 추가 예정

[![시연 영상](https://img.youtube.com/vi/kq1Hx8Iqd54/0.jpg)](https://youtube.com/shorts/kq1Hx8Iqd54)


## 👥 팀원

- **박규민** - 역할명
  - 
  
- **김문기** - 역할명
  - 
    
- **강현서** – 역할명
  - 
 
- **박재민** – 역할명
  - 
  

## 🔮 향후 계획

- [ ] 공란
- [ ] 
## 📄 라이선스

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

<p align="center">Made with ❤️ by STL Team</p>
