# 📚 도서 관리 시스템 - BookManager

## 개요
**BookManager**는 실무 관리자 환경을 가정하여 설계된 **Spring Boot + MyBatis 기반의 도서 관리 시스템**입니다.  
도서 등록/수정/삭제부터 회원 관리, 대출 이력 관리, 로그인 및 반납 기능까지 포함한 **실제 업무 흐름 전체**를 구현했습니다.

---

## 주요 기능

### 📘 도서 기능
- 도서 등록/수정/삭제/조회
- 이미지 파일 업로드 (원본 + 썸네일 분리 저장)
- 썸네일 목록 출력, 상세 페이지에 원본 출력
- 이미지 파일 중복 방지 (UUID)
- 이미지 삭제 동기화 처리

### 👤 회원 기능
- 회원 등록/수정/조회
- 논리 삭제 기반 탈퇴 처리 (복구 가능)
- 가입일(createdDate) 정렬 기능
- 탈퇴 상태 출력 및 버튼 분기 처리

### 🔄 대출 기능
- 회원-도서 간 대출 등록
- 대출 내역 목록 확인
- ✅ **반납 기능 포함** (returned 필드)

### 🔍 공통 기능
- 검색 기능 (도서 제목/저자)
- 페이징 처리 (도서, 회원)
- 입력값 유효성 검증 및 오류 메시지
- 관리자 로그인/로그아웃
- 로그인 세션 유지
- 전역 예외 처리
- 404 / 500 오류 페이지 구성
- 공통 레이아웃 구성 (header/footer)

---

## 사용 기술

| 항목 | 기술 |
|------|------|
| Backend | Spring Boot 3.x, Spring Validation |
| ORM/쿼리 | MyBatis + Mapper XML |
| DB | MySQL 8.x |
| View | Thymeleaf, Bootstrap 5 |
| 파일 업로드 | MultipartFile, UUID, Thumbnailator |
| 보안 | BCryptPasswordEncoder, 세션 기반 인증 |
| 기타 | Lombok, SLF4J, Gradle, Java 17 |

---

## DB 설계 (ERD 요약)

- book(id, title, author, publisher, isbn, stock, image_filename, thumbnail_filename)
- member(id, name, email, phone, address, withdrawn, created_date)
- admin(login_id, password)
- loan(id, book_id, member_id, loan_date, returned)

---

## 실행 방법
1. `application.yml`에서 DB 설정 (MySQL 사용자명/비번 포함)
2. Gradle 빌드 후 실행
3. http://localhost:8080 접속
4. 초기 관리자 계정은 이미 등록되어 있음

   ```
   아이디: admin
   비밀번호: changhak123!
   ```

---

## 개발 기간 및 관리 방식
- 개발 기간: 2025.06.23 ~ 07월 초중순 (약 2주)
- 매일 기능별 작업일지 작성
- 모든 구조, 기능, 검증, 예외처리 수작업 구현
- JPA 대신 MyBatis 사용 이유: 쿼리 제어력 및 명확한 SQL 설계

---

## 기타 문서
- `기능명세서.md`: 구현된 기능 목록 요약
- `기술문서.md`: 구조, 트러블슈팅, 설계 방향 정리
