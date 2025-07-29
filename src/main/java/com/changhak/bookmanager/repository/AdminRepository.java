package com.changhak.bookmanager.repository;

import com.changhak.bookmanager.domain.Admin;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminRepository {
    Admin findByLoginId(String loginId);
}
