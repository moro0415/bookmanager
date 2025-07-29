package com.changhak.bookmanager.domain;

import lombok.Data;

@Data
public class Admin {
    private Long id;
    private String loginId;
    private String password;
    private String name;

}
