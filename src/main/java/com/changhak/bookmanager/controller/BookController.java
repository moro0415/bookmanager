package com.changhak.bookmanager.controller;

import com.changhak.bookmanager.domain.Book;
import com.changhak.bookmanager.dto.SearchCondition;
import com.changhak.bookmanager.service.BookService;
import com.changhak.bookmanager.util.FileStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final FileStore fileStore;

    //도서 목록 조회
    @GetMapping
    public String list(@ModelAttribute("condition") SearchCondition condition,
                       Model model) {

        if (condition.getPage() < 1) {
            condition.setPage(1);
        }

        List<Book> books = bookService.search(condition);
        int totalCount = bookService.count(condition);
        int totalPages = (int) Math.ceil((double) totalCount / condition.getSize());

        model.addAttribute("books", books);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", condition.getPage());
        model.addAttribute("condition", condition);

        return "book/list";
    }

    //도서 등록 폼
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("book", new Book());
        return "book/form";
    }

    //도서 등록 처리
    @PostMapping("/add")
    public String add(@Validated @ModelAttribute Book book,
                      BindingResult bindingResult,
                      @RequestParam("imageFile") MultipartFile imageFile) {
        if(bindingResult.hasErrors()){
            return "book/form";
        }
        try {
            String savedFilename = fileStore.storeFile(imageFile, true);
            book.setImageFilename(savedFilename);
            book.setThumbnailFilename(savedFilename);
        } catch (IOException e) {
            bindingResult.reject("file.upload.error", "이미지 업로드에 실패했습니다");
            return "book/form";
        }
        Book savedBook = bookService.save(book);
        return "redirect:/books/" + savedBook.getId();
    }

    //도서 상세 조회
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Book book = bookService.findById(id);
        model.addAttribute("book", book);
        return "book/detail";
    }

    //도서 수정 폼
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Book book = bookService.findById(id);
        model.addAttribute("book", book);
        return "book/form";
    }

    //도서 수정 처리
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id,
                       @Validated @ModelAttribute Book book,
                       BindingResult bindingResult,
                       @RequestParam("imageFile") MultipartFile imageFile){
        if (bindingResult.hasErrors()){
            return "book/form";
        }

        Book originalBook = bookService.findById(id);
        book.setId(id);

        try {
            if(!imageFile.isEmpty()){
                if (originalBook.getImageFilename() != null) {
                    fileStore.deleteFile(System.getProperty("user.dir") + "/upload/" + originalBook.getImageFilename());
                }
                if (originalBook.getThumbnailFilename() != null) {
                    fileStore.deleteFile(System.getProperty("user.dir") + "/upload/thumb/" + originalBook.getThumbnailFilename());
                }

                String savedFilename = fileStore.storeFile(imageFile, true);
                book.setImageFilename(savedFilename);
                book.setThumbnailFilename(savedFilename);
            } else {
                book.setImageFilename(originalBook.getImageFilename());
                book.setThumbnailFilename(originalBook.getThumbnailFilename());
            }
        } catch (IOException e){
            bindingResult.reject("file.upload.error", "이미지 업로드에 실패했습니다");
            return "book/form";
        }

        bookService.update(book);
        return "redirect:/books/" + id;
    }

    //도서 삭제
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        Book book = bookService.findById(id);

        fileStore.deleteFile(System.getProperty("user.dir") + "/upload/" + book.getImageFilename());
        fileStore.deleteFile(System.getProperty("user.dir") + "/upload/thumb/" + book.getThumbnailFilename());

        bookService.delete(id);
        return "redirect:/books";
    }
}

