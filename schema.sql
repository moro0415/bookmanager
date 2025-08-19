CREATE DATABASE IF NOT EXISTS bookmanager
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_unicode_ci;
USE bookmanager;

-- 관리자
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `login_id` VARCHAR(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` VARCHAR(100) COLLATE utf8mb4_unicode_ci NOT NULL, -- BCrypt hash 저장
  `name` VARCHAR(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_admin_login_id` (`login_id`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

-- 도서
DROP TABLE IF EXISTS `book`;
CREATE TABLE `book` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `author` VARCHAR(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `publisher` VARCHAR(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `isbn` VARCHAR(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `stock` INT DEFAULT 0,
  `deleted` TINYINT(1) DEFAULT 0,
  `image_filename` VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `thumbnail_filename` VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_book_isbn` (`isbn`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

-- 회원
DROP TABLE IF EXISTS `member`;
CREATE TABLE `member` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` VARCHAR(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` VARCHAR(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `address` VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `withdrawn` TINYINT(1) DEFAULT 0,
  `deleted` TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_member_email` (`email`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

-- 대출
DROP TABLE IF EXISTS `loan`;
CREATE TABLE `loan` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `book_id` BIGINT NOT NULL,
  `member_id` BIGINT NOT NULL,
  `loan_date` DATE NOT NULL,
  `return_date` DATE DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_loan_book_id` (`book_id`),
  KEY `idx_loan_member_id` (`member_id`),
  CONSTRAINT `fk_loan_book`
    FOREIGN KEY (`book_id`) REFERENCES `book` (`id`)
    ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT `fk_loan_member`
    FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
    ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;



