# 📚 BookManager

Spring Boot + MyBatis 기반의 **관리자 전용 도서 관리 시스템**이다.
도서/회원 CRUD를 넘어 **대출·반납 기록 보존, 이미지 업로드(원본/썸네일), 소프트 삭제, 전역 예외 처리, 세션 기반 접근 제어** 등 실무 요구사항을 반영했다. 이 문서는 프로젝트를 처음 접하는 사람이 **무엇을, 어떻게 사용**할지 빠르게 파악하도록 구성되어 있다.

---

## Table of Contents

1. [Features](#features)
2. [Architecture](#architecture)
3. [Tech Stack](#tech-stack)
4. [Directory Structure](#directory-structure)
5. [Database Schema](#database-schema)
6. [Configuration](#configuration)
7. [Run Locally](#run-locally)
8. [Route Map (Pages)](#route-map-pages)
9. [Design Decisions & Rationale](#design-decisions--rationale)
10. [Validation & Error Handling](#validation--error-handling)
11. [File Upload Pipeline](#file-upload-pipeline)
12. [Pagination & Search](#pagination--search)
13. [Limitations](#limitations)
14. [Roadmap](#roadmap)

---

## Features

* **도서 관리**

  * 등록/수정/삭제/상세/목록
  * 이미지 업로드(원본 + 썸네일), **UUID 파일명**, 도서 삭제 시 이미지 동기 삭제
* **회원 관리**

  * 등록/수정/상세/목록
  * **논리 삭제(soft delete)** 및 복구, 가입일 정렬, 탈퇴 상태 UI 분기
* **대출 관리**

  * 대출 등록/반납/삭제, 목록/상세
  * **재고 차감/증가**(대출/반납 시) 및 **반납 여부 필터링**
  * `LoanDto`로 **Loan + Book + Member** 화면 데이터 일괄 전달
* **공통**

  * 키워드 검색 + 페이지네이션
  * 관리자 로그인/로그아웃(세션), 전역 예외 처리, 404/500 페이지
  * Thymeleaf Fragment 기반 공통 레이아웃

---

## Architecture

* **Layered MVC**

  * `controller` → `service` → `repository(MyBatis)` → `DB`
* **Session Gate**

  * Spring Security는 최소 설정(**permitAll**), 실제 접근 제어는 세션 검사 **Interceptor**로 수행
  * 세션 키: `loginAdmin`
* **MyBatis XML Mapper**

  * 명시적 SQL 관리(검색/페이징/조인/조건 분기)
* **FileStore**

  * 로컬 디스크 저장, 썸네일 생성, 안전한 파일명( UUID )

---

## Tech Stack

* **Backend**: Spring Boot 3.x, Java 17, Spring Validation
* **Persistence**: MyBatis (XML Mapper), MySQL 8.x
* **View**: Thymeleaf, Bootstrap 5
* **Security**: Spring Security(최소 구성) + 세션 인터셉터, BCryptPasswordEncoder
* **Etc.**: Lombok, Logback, Gradle, Thumbnailator

---

## Directory Structure

```
bookmanager/
└─ src/main/java/com/changhak/bookmanager
   ├─ config/            # SecurityConfig, WebConfig(Interceptor, ResourceHandler)
   ├─ controller/        # Book, Member, Loan, Login, Home
   ├─ domain/            # Admin, Book, Member, Loan
   ├─ dto/               # LoanDto, SearchCondition
   ├─ exception/         # GlobalExceptionHandler (@ControllerAdvice)
   ├─ interceptor/       # LoginCheckInterceptor (세션 검사)
   ├─ repository/        # Admin/Book/Member/Loan Repository (MyBatis @Mapper)
   ├─ service/           # Book/Member/Loan/Login/Inventory 서비스
   └─ util/              # FileStore (저장/썸네일/삭제)
src/main/resources/
   ├─ mapper/            # *.xml (MyBatis SQL)
   ├─ templates/         # Thymeleaf pages (book/member/loan/login/home/error, fragments)
   └─ static/            # 정적 리소스
```

---

## Database Schema

> 테이블 4개: `admin`, `book`, `member`, `loan`
> 대출은 `book`↔`member`를 참조하는 관계. 회원 삭제는 **논리 삭제**로 기록 보존.

[📄 schema.sql](./schema.sql)


---

## Configuration

`src/main/resources/application.yml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/bookmanager?serverTimezone=UTC&characterEncoding=UTF-8
    username: ${DB_USERNAME}   # e.g., root
    password: ${DB_PASSWORD}   # e.g., ****
    driver-class-name: com.mysql.cj.jdbc.Driver

  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB

mybatis:
  configuration:
    map-underscore-to-camel-case: true
  mapper-locations: classpath:/mapper/**/*.xml
  type-aliases-package: com.changhak.bookmanager.domain
```

> **Note**
>
> * 뷰는 Thymeleaf 자동 설정(기본 prefix: `classpath:/templates/`)을 사용한다.
> * 최초 실행 시 `FileStore`가 **`${user.dir}/upload/`**, **`${user.dir}/upload/thumb/`** 디렉터리를 생성한다. 정적 매핑은 `/upload/**`로 제공된다.

---

## Run Locally

### Prerequisites

* JDK 17+
* MySQL 8.x
* Git, Gradle Wrapper(동봉)

### 1) 데이터베이스 생성

```sql
CREATE DATABASE bookmanager CHARACTER SET utf8mb4;
```

### 2) 환경 설정

* `application.yml`의 DB 계정/비밀번호를 수정하거나, OS 환경변수로 `DB_USERNAME`, `DB_PASSWORD` 주입

### 3) 관리자 계정 생성

* BCrypt 해시 비밀번호를 사용해야 한다.

  * Java에서 생성 예시:

    ```java
    System.out.println(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("admin1234"));
    ```
* 생성된 해시를 아래 SQL에 적용:

  ```sql
  INSERT INTO admin(login_id, password, name)
  VALUES ('admin', '<BCrypt Hash Here>', '관리자');
  ```

### 4) 실행

```bash
./gradlew bootRun
# http://localhost:8080
```

---

## Route Map (Pages)

> 서버 사이드 렌더링(Thymeleaf) 기반. 파라미터는 일부 생략.

**Auth**

* `GET /login`  로그인 페이지
* `POST /login` 로그인 처리 → 세션 `loginAdmin` 설정
* `POST /logout` 로그아웃(세션 무효화)

**Home**

* `GET /`  (세션 필요, 없으면 `/login`으로 리다이렉트)

**Books**

* `GET /books`                목록(검색/페이징)
* `GET /books/{id}`           상세
* `GET /books/new`            등록 폼
* `POST /books/add`           등록 처리(이미지 업로드)
* `GET /books/edit/{id}`      수정 폼
* `POST /books/edit/{id}`     수정 처리(이미지 교체 시 이전 파일 정리)
* `POST /books/delete/{id}`   삭제(논리 삭제 + 원본/썸네일 동기 삭제)

**Members**

* `GET /members`              목록(검색/페이징)
* `GET /members/{id}`         상세
* `GET /members/new`          등록 폼
* `POST /members/add`         등록 처리
* `GET /members/edit/{id}`    수정 폼
* `POST /members/edit/{id}`   수정 처리
* `POST /members/delete/{id}` 삭제(논리 삭제)

**Loans**

* `GET /loans`                목록(검색/페이징, 반납 여부 필터)
* `GET /loans/{id}`           상세
* `GET /loans/new`            등록 폼
* `POST /loans/add`           대출 처리(재고 차감)
* `POST /loans/return/{id}`   반납 처리(재고 증가)
* `POST /loans/delete/{id}`   삭제(완전 삭제)

> 일부 경로명은 템플릿/컨트롤러 기준으로 정리했다. 프로젝트 버전에 따라 세부 경로나 파라미터명이 다를 수 있다.

---

## Design Decisions & Rationale

> 개발 일지의 고민과 해결 과정을 **결과 요약** 형태로 반영

* **논리 삭제(Soft Delete) 채택**
  단순 삭제 대신 `withdrawn/deleted` 플래그를 둬 **대출 이력 보존**과 **복구 가능성**을 확보. 운영 데이터 정합성 강화.
* **대출 ↔ 재고 일관성**
  대출 시 재고 차감, 반납 시 증가. 순환 의존성 방지를 위해 \*\*`InventoryService`\*\*로 재고 변경을 **단일 진입점**으로 분리.
* **DTO 집계 전송(`LoanDto`)**
  화면에서 필요한 도서/회원/대출 정보를 한 번에 렌더링. **조인/조회 최적화 + 템플릿 단순화**.
* **세션 인터셉터 기반 접근 제어**
  Security는 최소 설정(permitAll). 실제 접근은 \*\*`LoginCheckInterceptor`\*\*가 세션 검사로 차단. 폼 기반 관리자 로그인 유지.
* **파일 업로드 안정성**
  UUID 파일명으로 충돌 방지, 썸네일 자동 생성, 삭제 시 원본/썸네일 **동기화 제거**로 **고아 파일 방지**.
* **검색/페이징 공통화**
  `SearchCondition(page, size, keyword, status)`를 **도서/회원/대출 공통**으로 사용, 컨트롤러-서비스-리포지토리 간 파라미터 일관성 확보.
* **예외 처리 일원화**
  `@ControllerAdvice`에서 DuplicateKey, 404, 500 등을 **뷰로 매핑**. 사용자 친화적 에러 응답 유지.

---

## Validation & Error Handling

* **Server-side Validation**

  * `@NotBlank`, `@Positive`, `@Email` 등 Bean Validation 적용
  * `BindingResult`로 오류 메시지 렌더링
* **GlobalExceptionHandler**

  * **DuplicateKeyException**: 중복 데이터 처리
  * **NoResourceFoundException**: 404 페이지
  * **기타 Exception**: 500 페이지
* **UX**

  * 실패 시 사용자 친화 메시지 + 입력값 보존

---

## File Upload Pipeline

1. 사용자 업로드 → `MultipartFile` 수신
2. `FileStore`가 **UUID 파일명** 생성 후 `${user.dir}/upload/` 저장
3. `Thumbnailator`로 **썸네일** 생성 → `${user.dir}/upload/thumb/` 저장
4. 수정/삭제 시 기존 파일들 **동기화 정리**
5. 정적 매핑 `/upload/**` 통해 이미지 제공(WebConfig)

---

## Pagination & Search

* 공통 DTO: `SearchCondition`

  * `page`(1-base), `size`(기본 10), `keyword`, `status`(대출: `ALL|RETURNED|NOT_RETURNED`)
* MyBatis에서 `LIMIT #{size} OFFSET #{offset}` 형태로 처리
* 템플릿에서 페이지 네비게이션 활성/비활성 처리

---

## Limitations

* **초기 관리자 생성 UI 없음**: DB에 해시 비밀번호로 직접 INSERT 필요
* **보안 최소 구성**: 시큐리티는 permitAll, 세션 인터셉터 접근 제어(운영 배포 전 보강 권장)
* **로컬 파일 저장**: 객체 스토리지/S3 미적용(운영 환경 대응 필요)
* **동시성**: 기본적인 재고 검증만 적용(낙관/비관 락, 트랜잭션 격리 강화 검토 대상)

## Roadmap
- 테스트: 단위/통합 테스트 보강, 컨트롤러/리포지토리 커버리지 확장  
- 보안: CSRF 활성화, 인가 규칙 세분화, 관리자 생성/권한 관리 화면  
- 인프라: Docker, CI/CD, AWS EC2+RDS, 외부 스토리지(S3)  
- API: REST API 분리(외부 클라이언트용), 프론트(React/Vue) 연동  
- 성능: 캐시(Redis), 페이징 성능 최적화, N+1 방지 쿼리 개선





