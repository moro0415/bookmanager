package com.changhak.bookmanager.repository;

import com.changhak.bookmanager.domain.Admin;
import org.apache.ibatis.annotations.Mapper;

/**
 * 관리자 계정 Repository
 * - 실제 SQL은 resources/mapper/AdminRepository.xml에 정의
 * - LoginService에서 호출
 */
@Mapper
public interface AdminRepository {
    Admin findByLoginId(String loginId);
}
