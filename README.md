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
│   ├── format/
│   │   ├── Avatar.js        # 아바타 스키마
│   │   ├── Product.js       # 상품 스키마
│   │   └── FittingResult.js # 피팅 결과 스키마
│   ├── manager/
│   │   ├── avatarRoutes.js  # 아바타 관련 라우트
│   │   ├── productRoutes.js # 상품 관련 라우트
│   │   └── fittingRoutes.js # 피팅 관련 라우트
│   ├── util/
│   │   └── database.js      # MongoDB 연결 설정
│   ├── Client.java               # Express 앱 설정
│   ├── CkuentHandler.java            # 서버 엔트리 포인트
│   ├── ClientMain.java
│   └──  ServerMain.java


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

## 📽️ 시연 영상(2025.06.09. 기준)

[![시연 영상](https://img.youtube.com/vi/kq1Hx8Iqd54/0.jpg)](https://youtube.com/shorts/kq1Hx8Iqd54)


## 👥 팀원

- **박규민** - Backend Developer
  - 
  
- **김문기** - Frontend Developer
  - 
    
- **강현서** – Frontend Developer / 3D 아바타 생성 담당
  - 
 
- **박재민** – Project Designer
  - 
  

## 🔮 향후 계획

- [ ] 공란
- [ ] 
## 📄 라이선스

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

<p align="center">Made with ❤️ by STL Team</p>
