# 🛠 Spring Admin Boilerplate

Spring Boot 기반 관리자 어드민 보일러플레이트입니다.  
비즈니스 로직 없이 **인증 + 기본 CRUD** 구조만 담아, 새 프로젝트 시작 시 바로 복붙·확장할 수 있도록 설계했습니다.

## 📌 기술 스택

| 분류 | 기술 |
|---|---|
| Framework | Spring Boot 2.7 |
| Language | Java 11 |
| View | Thymeleaf + Layout Dialect |
| ORM | Spring Data JPA |
| DB (로컬) | H2 (인메모리) |
| DB (운영) | PostgreSQL |
| 인증 | Spring Security (세션 기반) |
| 빌드 | Gradle |
| 기타 | Lombok, Bootstrap 5, Bootstrap Icons |

## 🗂 프로젝트 구조

```
src/main/java/com/example/admin/
├── AdminApplication.java
├── config/
│   ├── PasswordConfig.java          # BCrypt Bean
│   ├── SecurityConfig.java          # 로그인 / 권한 설정
│   ├── GlobalControllerAdvice.java  # currentUser, isAdmin 전역 주입
│   ├── GlobalExceptionHandler.java  # 공통 에러 핸들러
│   └── DataInitializer.java         # 앱 시작 시 초기 계정 자동 생성
├── controller/
│   ├── AuthController.java          # /login
│   ├── DashboardController.java     # /dashboard
│   ├── PostController.java          # 샘플 CRUD (/posts)
│   └── UserController.java          # 사용자 관리 /admin/users (ADMIN 전용)
├── model/
│   ├── entity/
│   │   ├── BaseEntity.java          # createdAt, updatedAt, deleted (soft delete)
│   │   ├── User.java
│   │   └── Post.java                # 샘플 CRUD 엔티티
│   ├── enums/
│   │   └── UserRole.java            # ADMIN, USER
│   └── dto/
│       └── CustomUserDetails.java
├── repository/
│   ├── UserRepository.java
│   └── PostRepository.java
└── service/
    ├── UserService.java             # UserDetailsService 구현 포함
    └── PostService.java

src/main/resources/
├── application.yml                  # local(H2) / prod(PostgreSQL) 프로파일 분리
├── static/css/custom.css            # 사이드바 레이아웃 스타일
└── templates/
    ├── layouts/layout.html          # 공통 레이아웃 (사이드바 + 툴바)
    ├── login.html
    ├── dashboard.html
    ├── posts/                       # 목록 / 작성 / 상세 / 수정
    ├── users/                       # 목록 / 등록 / 상세 / 수정
    └── error/error.html
```

## 🚀 실행 방법

### 1. 로컬 실행 (H2 인메모리 DB)

```bash
git clone https://github.com/toolbee/spring-admin-boilerplate.git
cd spring-admin-boilerplate
./gradlew bootRun
```

브라우저에서 → **http://localhost:8080**

### 2. 초기 계정

| 이메일 | 비밀번호 | 역할 |
|---|---|---|
| admin@example.com | admin1234 | ADMIN |
| user@example.com | user1234 | USER |

> 앱 최초 실행 시 `DataInitializer`가 자동 생성합니다.

### 3. H2 콘솔 (로컬 전용)

**http://localhost:8080/h2-console**

- JDBC URL: `jdbc:h2:mem:admindb`
- Username: `sa` / Password: (없음)

### 4. 운영 환경 (PostgreSQL)

환경변수 설정 후 `prod` 프로파일로 실행:

```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=admindb
export DB_USER=admin
export DB_PASSWORD=secret
export SERVER_PORT=8080

./gradlew bootRun --args='--spring.profiles.active=prod'
```

## ✨ 주요 기능

### 인증 / 권한

- Spring Security 세션 기반 로그인
- `ADMIN` / `USER` 두 가지 역할
- `/admin/**` 경로는 ADMIN만 접근 가능
- Thymeleaf 템플릿에서 `${isAdmin}`, `${currentUser}` 바로 사용 가능

### CRUD 샘플 (Post)

| 기능 | URL | 설명 |
|---|---|---|
| 목록 | `GET /posts` | 페이지네이션 + 키워드 검색 |
| 작성 | `GET /posts/new` | 작성 폼 |
| 등록 | `POST /posts` | 저장 처리 |
| 상세 | `GET /posts/{id}` | 조회수 자동 증가 |
| 수정 폼 | `GET /posts/{id}/edit` | 수정 폼 |
| 수정 | `POST /posts/{id}` | 저장 처리 |
| 삭제 | `POST /posts/{id}/delete` | Soft Delete |

### Soft Delete

모든 엔티티는 `BaseEntity.deleted` 필드를 사용한 **논리 삭제** 방식입니다.  
실제 DB에서 row가 삭제되지 않고 `deleted = true`로 마킹됩니다.

## 🔧 새 도메인 엔티티 추가하는 법

`Post`를 참고해 아래 순서로 복사·수정하면 됩니다.

```
1. Entity    → src/.../model/entity/MyEntity.java
2. Repository → src/.../repository/MyEntityRepository.java
3. Service   → src/.../service/MyEntityService.java
4. Controller → src/.../controller/MyEntityController.java
5. Templates → src/.../templates/my-entity/{list, create, detail, edit}.html
6. 사이드바   → layouts/layout.html 에 nav-link 한 줄 추가
```

## 📝 라이선스

MIT
