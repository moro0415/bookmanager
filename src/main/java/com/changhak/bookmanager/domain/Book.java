package com.changhak.bookmanager.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class Book {
    private Long id;

    @NotBlank(message = "제목은 필수입니다")
    private String title;

    @NotBlank(message = "저자는 필수입니다")
    private String author;

    @NotBlank(message = "출판사는 필수입니다")
    private String publisher;

    @NotBlank(message = "ISBN은 필수입니다")
    private String isbn;

    @Positive(message = "재고 수량은 0보다 커야 합니다")
    private int stock;

    private String imageFilename;

    private String thumbnailFilename;


    private boolean deleted;


    public void increaseStock() {
        this.stock++;
    }


    public void decreaseStock() {
        if (this.stock <= 0) {
            throw new IllegalStateException("재고가 부족합니다.");
        }
        this.stock--;
    }
}
