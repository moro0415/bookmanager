package com.changhak.bookmanager.domain;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class Member {

    private Long id;

    @NotBlank(message = "이름은 필수입니다")
    private String name;

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "이메일 형식이 아닙니다")
    private String email;

    @NotBlank(message = "전화번호는 필수입니다")
    private String phone;

    @NotBlank(message = "주소는 필수입니다")
    private String address;

    private LocalDateTime createdDate;

    private boolean withdrawn;

    private boolean deleted;

}
