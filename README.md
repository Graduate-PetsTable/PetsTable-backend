# 💻Dogker backend
## 🚀 소개
### 기술적 특징
• 이미지 업로드: Presigned URL을 활용한 비동기 업로드 방식 적용<br>
• 이미지 최적화: CDN 및 Lambda@Edge를 활용하여 썸네일, 단계별, 상세 이미지 등 상황에 맞게 동적 리사이징 구현<br>
• 포인트 적립 시스템: Redis Stream을 활용한 이벤트 기반 아키텍처로 비동기 처리 최적화<br>
• 레시피 검색 최적화: QueryDSL을 이용하여 제목, 내용, 효능, 재료로 검색할 수 있도록 동적 쿼리 및 효율적인 데이터 조회<br>
• CI/CD 자동화: GitHub Actions + Docker + Nginx를 활용한 무중단 배포 환경 구축<br>
• 단일 서버 내 Redis Cluster 환경 구축 : 단일 서버 내 클러스터링을 통해 분산 저장 및 샤딩 개념을 테스트<br>

### 프로젝트 소개
강아지 수제 간식 레시피를 공유합니다.

<details>
    <summary><h2>주요 기능</h2></summary>
  <br>
  <h3>1️⃣ 나의 반려견 등록 및 조회하기</h3>
  <img width="403" alt="스크린샷 2025-02-03 오후 10 52 44" src="https://github.com/user-attachments/assets/5d4a27a3-3b58-4785-bdd9-2dca0c6d86cb"/><br>
  • 나의 반려동물을 등록할 수 있습니다.<br>
  • 내가 등록한 반려견들의 정보들을 리스트업 할 수 있습니다.<br>
  • 해당 반려견의 정보로 들어가 반려견의 정보를 받아올수 있고 반려견들의 세부 정보를 업데이트 할 수 있습니다.<br><br>
  <img width="234" alt="스크린샷 2025-02-03 오후 10 52 51" src="https://github.com/user-attachments/assets/5a1057f5-9367-4eee-9d30-f2a73cda56cb"/><br>
  • 반려견의 정보를 등록하면 반려견의 정보 또한 모든 페이지에서 업데이트됩니다.<br><br>
  <img width="228" alt="스크린샷 2025-02-03 오후 10 53 03" src="https://github.com/user-attachments/assets/8829efc9-5dcd-4a3c-a398-bf054a703920"/><br>
  • 등록된 강아지 이름을 볼 수 있고 등록된 정보들과 등록한 레시피 갯수, 포인트 여부를 확인할 수 있습니다.<br>
  • 로그아웃 회원탈퇴가 가능합니다.<br><br>
  <h3>2️⃣ 레시피 등록 및 조회하기</h3>
  <img width="414" alt="레시피 목록" src="https://github.com/user-attachments/assets/aa29bcd8-b3fe-4912-ae9f-7289c039aeaa"/><br>
  • 수제 간식 레시피 목록을 확인할 수 있습니다.<br><br>
  <img width="446" alt="스크린샷 2025-02-03 오후 10 52 04" src="https://github.com/user-attachments/assets/70f0d86f-9e15-403e-937e-9a03c1cbe7e2"/><br>
  • 수제 간식 레시피를 단계별로 등록할 수 있으며, 해시태그 형식의 재료와 효능을 등록할 수 있습니다.<br>
  • 레시피를 등록하면 포인트를 획득할 수 있습니다.<br><br>
  <img width="406" alt="스크린샷 2025-02-03 오후 10 52 18" src="https://github.com/user-attachments/assets/a73f4f34-69e6-4908-9aae-b06413e33cec"/><br>
  • 수제 간식 레시피의 상세 내용을 확일할 수 있습니다.<br>
  • 작성자, 효능, 단계별 내용, 재료를 확인할 수 있습니다.<br><br>
  <h3>3️⃣ 나의 북마크 조회하기</h3>
  <img width="440" alt="스크린샷 2025-02-03 오후 10 52 30" src="https://github.com/user-attachments/assets/43a10c61-1488-4638-b711-1db63489a6f0"/><br>
  • 북마크를 등록할 수 있고, 나의 북마크 목록을 확인할 수 있습니다.<br>
</details>

### 🛠️ 기술 스택

| SpringBoot3.2.4, JDK21 | Web Applicatoin Server 구축 |
|:--------:|:------------:|
| Spring Data JPA | ORM, DBMS와 통신을 위해 사용 |
| MySQL | DBMS |
| Oracle Cloud VM Instance | 서버 호스팅 |
| AWS RDS | DB 관리 |
| Spring Security | JWT 인증, 권한 관리 및 보안 설정 |
| AWS S3 | 클라이언트가 업로드하는 이미지 저장소 |
| AWS CloudFront | 캐시 서버(CDN)를 통해 이미지 전송 최적화 |
| AWS Lambda@Edge | 동적 이미지 리사이징 및 최적화 |
| Redis | 빠른 속도의 인메모리 캐시 처리 |
| Github Action | CI / CD 파이프라인 자동화 |
| Docker | 애플리케이션 컨테이너화 |


## 👥 팀원
| **Name** | **Position** |
|:--------:|:------------:|
| **선승구** | `Backend` |

## 아키텍쳐
<img width="604" alt="스크린샷 2025-02-03 오후 10 38 05" src="https://github.com/user-attachments/assets/5355d5ba-439e-4070-bbb4-c3a8a1d7090b" />

## ERD
![Docker ERD](https://github.com/user-attachments/assets/8d89e849-cdc2-4024-b8b1-92934011ad91)

## 폴더 구조
```
└─ 📁petstable
    ├── domain
    │   ├── board
    │   │   ├── controller
    │   │   ├── dto
    │   │   │   ├── request
    │   │   │   └── response
    │   │   ├── entity
    │   │   ├── message
    │   │   ├── repository
    │   │   └── service
    │   ├── bookmark
    │   │   ├── controller
    │   │   ├── dto
    │   │   │   └── response
    │   │   ├── entity
    │   │   ├── message
    │   │   ├── repository
    │   │   └── service
    │   ├── detail
    │   │   ├── dto
    │   │   │   ├── request
    │   │   │   └── response
    │   │   ├── entity
    │   │   ├── repository
    │   │   └── service
    │   ├── fcm
    │   │   ├── controller
    │   │   ├── dto
    │   │   │   ├── request
    │   │   │   └── response
    │   │   ├── message
    │   │   └── service
    │   ├── ingredient
    │   │   ├── dto
    │   │   │   ├── request
    │   │   │   └── response
    │   │   ├── entity
    │   │   ├── repository
    │   │   └── service
    │   ├── member
    │   │   ├── controller
    │   │   ├── dto
    │   │   │   ├── request
    │   │   │   └── response
    │   │   ├── entity
    │   │   ├── message
    │   │   ├── repository
    │   │   └── service
    │   ├── pet
    │   │   ├── controller
    │   │   ├── dto
    │   │   │   ├── request
    │   │   │   └── response
    │   │   ├── entity
    │   │   ├── message
    │   │   ├── repository
    │   │   └── service
    │   ├── point
    │   │   ├── controller
    │   │   ├── dto
    │   │   │   ├── request
    │   │   │   └── response
    │   │   ├── entity
    │   │   ├── event
    │   │   ├── message
    │   │   ├── repository
    │   │   └── service
    │   ├── report
    │   │   ├── controller
    │   │   ├── dto
    │   │   │   ├── request
    │   │   │   └── response
    │   │   ├── entity
    │   │   ├── message
    │   │   ├── repository
    │   │   └── service
    │   └── tag
    │       ├── dto
    │       │   ├── request
    │       │   └── response
    │       ├── entity
    │       ├── message
    │       ├── repository
    │       └── service
    └── global
        ├── auth
        │   ├── apple
        │   ├── dto
        │   │   ├── request
        │   │   └── response
        │   └── google
        ├── config
        ├── controller
        ├── exception
        ├── refresh
        │   ├── dto
        │   │   ├── request
        │   │   └── response
        │   ├── entity
        │   └── service
        ├── scheduler
        └── support
```
