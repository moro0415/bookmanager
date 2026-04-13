<div align="center">

# BookManager

**Spring Boot + MyBatis 기반 도서 관리 시스템**

관리자 로그인 · 도서/회원 CRUD · 대출/반납 · 이미지 업로드 · 검색/페이징

[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=flat&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.6-6DB33F?style=flat&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MyBatis](https://img.shields.io/badge/MyBatis-3.0.4-DC382D?style=flat&logo=mybatis&logoColor=white)](https://mybatis.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-SSR-005F0F?style=flat&logo=thymeleaf&logoColor=white)](https://www.thymeleaf.org/)

</div>

---

## 주요 기능

| 기능 | 설명 |
|---|---|
| **도서 관리** | 등록/수정/삭제/상세/목록, 이미지 업로드(원본 + 썸네일), 삭제 시 파일 동기 정리 |
| **회원 관리** | 등록/수정/상세/목록, 탈퇴/복구 2단계 분리, 논리 삭제(soft delete) |
| **대출 관리** | 대출 등록/반납/삭제, 재고 자동 차감/증가, 중복 대출 방지, 반납 여부 필터 |
| **인증** | 관리자 세션 로그인 (BCrypt 비밀번호), 인터셉터 기반 접근 제어 |
| **공통** | 키워드 검색 + 페이지네이션, 전역 예외 처리, 404/500 에러 페이지 |

---

## 기술 스택

| 구분 | 기술 |
|---|---|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.4.6 |
| **View** | Thymeleaf (SSR), Bootstrap 5 |
| **Persistence** | MyBatis 3.0.4 (XML Mapper) |
| **Database** | MySQL 8.x |
| **Security** | Spring Security (BCrypt) + 세션 인터셉터 |
| **File** | Thumbnailator 0.4.20 (썸네일 생성) |
| **Build** | Gradle |

---

## ERD

> 테이블 4개 · 대출(Loan)이 도서(Book)와 회원(Member)을 참조하는 구조

```
  ┌──────────────┐                              ┌──────────────┐
  │    Admin     │                              │    Book      │
  ├──────────────┤                              ├──────────────┤
  │ id (PK)      │                              │ id (PK)      │
  │ loginId      │                              │ title        │
  │ password     │     ┌──────────────┐         │ author       │
  │ name         │     │    Loan      │         │ publisher    │
  └──────────────┘     ├──────────────┤         │ isbn (UK)    │
                       │ id (PK)      │         │ stock        │
  ┌──────────────┐     │ bookId (FK) ─┼────────▶│ imageFilename│
  │   Member     │     │ memberId(FK) │         │ thumbnail    │
  ├──────────────┤◀────┼──────────────┤         │ deleted      │
  │ id (PK)      │     │ loanDate     │         └──────────────┘
  │ name         │     │ returnDate   │
  │ email        │     └──────────────┘
  │ phone        │      returnDate = null
  │ address      │      → 미반납 상태
  │ createdDate  │
  │ withdrawn    │
  │ deleted      │
  └──────────────┘
```

### 엔티티 필드 상세

#### Admin

| 필드 | 타입 | 설명 |
|---|---|---|
| id | Long (PK) | |
| loginId | String | 로그인 ID |
| password | String | BCrypt 해시 |
| name | String | 관리자 이름 |

#### Book

| 필드 | 타입 | 설명 |
|---|---|---|
| id | Long (PK) | |
| title | String | @NotBlank |
| author | String | @NotBlank |
| publisher | String | @NotBlank |
| isbn | String | @NotBlank, UNIQUE |
| stock | int | @Positive. 대출 시 차감, 반납 시 증가 |
| imageFilename | String | 원본 이미지 파일명 (UUID). nullable |
| thumbnailFilename | String | 썸네일 파일명. nullable |
| deleted | boolean | soft delete |

#### Member

| 필드 | 타입 | 설명 |
|---|---|---|
| id | Long (PK) | |
| name | String | @NotBlank |
| email | String | @NotBlank, @Email |
| phone | String | @NotBlank |
| address | String | @NotBlank |
| createdDate | LocalDateTime | 가입일 |
| withdrawn | boolean | 탈퇴 여부 (복구 가능) |
| deleted | boolean | 논리 삭제 (복구 불가) |

#### Loan

| 필드 | 타입 | 설명 |
|---|---|---|
| id | Long (PK) | |
| bookId | Long (FK) | → Book |
| memberId | Long (FK) | → Member |
| loanDate | LocalDate | 대출일 |
| returnDate | LocalDate | 반납일. null이면 미반납 |

---

## Route Map

> Thymeleaf SSR 기반. 모든 경로는 세션 인증 필요 (로그인/로그아웃 제외).

### Auth (3개)

| Method | URL | 설명 |
|---|---|---|
| GET | `/login` | 로그인 페이지 |
| POST | `/login` | 로그인 처리 (세션 생성) |
| POST | `/logout` | 로그아웃 (세션 무효화) |

### Home (1개)

| Method | URL | 설명 |
|---|---|---|
| GET | `/` | 홈 화면 (미인증 시 /login 리다이렉트) |

### Book (7개)

| Method | URL | 설명 |
|---|---|---|
| GET | `/books` | 도서 목록 (검색/페이징) |
| GET | `/books/{id}` | 도서 상세 |
| GET | `/books/add` | 등록 폼 |
| POST | `/books/add` | 등록 처리 (이미지 업로드) |
| GET | `/books/edit/{id}` | 수정 폼 |
| POST | `/books/edit/{id}` | 수정 처리 (이미지 교체 시 기존 파일 삭제) |
| POST | `/books/delete/{id}` | 논리 삭제 + 이미지 파일 동기 삭제 |

### Member (9개)

| Method | URL | 설명 |
|---|---|---|
| GET | `/members` | 회원 목록 (검색/페이징) |
| GET | `/members/{id}` | 회원 상세 |
| GET | `/members/add` | 등록 폼 |
| POST | `/members/add` | 등록 처리 |
| GET | `/members/edit/{id}` | 수정 폼 |
| POST | `/members/edit/{id}` | 수정 처리 |
| POST | `/members/withdraw/{id}` | 탈퇴 처리 (withdrawn=true) |
| POST | `/members/restore/{id}` | 탈퇴 복구 (withdrawn=false) |
| POST | `/members/delete/{id}` | 논리 삭제 (deleted=true) |

### Loan (6개)

| Method | URL | 설명 |
|---|---|---|
| GET | `/loans` | 대출 목록 (검색/상태 필터/페이징) |
| GET | `/loans/{id}` | 대출 상세 (Loan + Book + Member) |
| GET | `/loans/add` | 등록 폼 (재고 > 0 도서, 미탈퇴 회원만 표시) |
| POST | `/loans/add` | 대출 처리 (재고 차감) |
| POST | `/loans/return/{id}` | 반납 처리 (재고 증가) |
| POST | `/loans/delete/{id}` | 물리 삭제 (반납 완료된 건만) |

> **총 26개** 라우트 — 인증 불필요 `3개` · 인증 필요 `23개`

---

## Technical Decisions (기술적 의사결정)

### 1. 순환 의존성 해결 — InventoryService 분리

도서 삭제 시 대출 여부를 확인해야 하고(BookService → LoanService), 대출 시 재고를 차감해야 해서(LoanService → BookService) 순환 의존이 발생했다. 재고 변경 로직만 **InventoryService**로 분리하여 `LoanService → InventoryService → BookRepository` 구조로 해결했다.

### 2. 논리 삭제 + 탈퇴/삭제 2단계 분리

회원은 `withdrawn`(탈퇴, 복구 가능)과 `deleted`(완전 삭제, 복구 불가)를 별도 플래그로 관리한다. 대출 이력 보존과 복구 가능성을 동시에 확보하기 위함이다. 도서도 `deleted` 플래그로 논리 삭제한다. 대출 기록은 반납 완료 후에만 물리 삭제가 가능하다.

### 3. 삭제 제약 검증

대출 중인 도서는 삭제 불가, 미반납 도서가 있는 회원은 탈퇴/삭제 불가하다. Service 레이어에서 `isBookCurrentlyLoaned()`, `isMemberCurrentlyLoaning()`으로 검증하여 데이터 정합성을 보장한다.

### 4. 세션 인터셉터 기반 접근 제어

Spring Security는 BCryptPasswordEncoder 빈 제공용으로만 사용하고(permitAll), 실제 접근 제어는 `LoginCheckInterceptor`가 세션의 `loginAdmin` 존재 여부로 수행한다. 관리자 전용 시스템이라 단순한 세션 방식으로 충분하다고 판단했다.

### 5. MyBatis 동적 SQL

검색/페이징/상태 필터를 `<where>`, `<if>`, `<choose>/<when>` 동적 SQL로 처리한다. `SearchCondition` DTO를 도서/회원/대출에서 공통으로 사용하여 컨트롤러-서비스-리포지토리 간 파라미터를 통일했다.

### 6. LoanDto 조인 조회

대출 목록/상세에서 도서 제목, 회원 이름 등을 함께 보여줘야 한다. MyBatis `resultMap`의 `<association>`으로 Loan + Book + Member 3-way JOIN 결과를 `LoanDto`로 한 번에 매핑하여 N+1 없이 처리한다.

### 7. 이미지 업로드 파이프라인

업로드 → UUID 파일명 생성 → 로컬 디스크 저장 → Thumbnailator로 썸네일 자동 생성. 도서 수정 시 이미지를 교체하면 기존 원본/썸네일을 삭제하고, 도서 삭제 시에도 파일을 동기 정리하여 고아 파일을 방지한다.

### 8. 전역 예외 처리

`@ControllerAdvice`에서 DuplicateKeyException(ISBN/이메일 중복), IllegalArgumentException, IllegalStateException을 잡아 사용자 친화적 에러 페이지로 렌더링한다. 별도 ErrorCode enum 없이 표준 Java 예외를 활용하는 간결한 구조다.

---

## Test (테스트)

`@SpringBootTest` + `@Transactional` 통합 테스트. BookService와 MemberService의 기본 CRUD를 검증한다.

| 테스트 파일 | 메서드 수 |
|---|---|
| BookServiceTest | 3 |
| MemberServiceTest | 3 |
| SecurityConfigTest | 1 (BCrypt 해시 생성 유틸) |

---

## Project Structure (프로젝트 구조)

```
com.changhak.bookmanager
├── config/          SecurityConfig, WebConfig
├── controller/      BookController, MemberController, LoanController,
│                    LoginController, HomeController
├── domain/          Admin, Book, Member, Loan
├── dto/             LoanDto, LoginForm, SearchCondition
├── exception/       GlobalExceptionHandler
├── interceptor/     LoginCheckInterceptor
├── repository/      AdminRepository, BookRepository, MemberRepository,
│                    LoanRepository (MyBatis @Mapper)
├── service/         BookService, MemberService, LoanService,
│                    LoginService, InventoryService
└── util/            FileStore

resources/
├── mapper/          AdminMapper.xml, BookMapper.xml, MemberMapper.xml,
│                    LoanMapper.xml
└── templates/       book/, member/, loan/, login/, home/, error/, fragments/
```
