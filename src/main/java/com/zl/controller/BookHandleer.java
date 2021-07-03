package com.zl.controller;
import com.zl.entity.Book;
import com.zl.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
@RestController
@RequestMapping("/book")
public class BookHandleer {
    @Autowired
    private BookRepository bookRepository;

    @GetMapping("/findAll")
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @PostMapping("/getAll")
    public List<Book> getAll() {
        return bookRepository.findAll();
    }

    @GetMapping("/getBookByid/{id}")
    public Book getBookByid(@PathVariable String id) {
        return bookRepository.getOne(Integer.parseInt(id));
    }

    @GetMapping("/getBookByName/{name}")
    public List<Book> getBookByName(@PathVariable String name) {
        return bookRepository.findBooksByAuthor(name);
    }

    /**
     * 接收from表单
     *
     * @param title
     * @param author
     * @return
     */
    @PostMapping("/createBook")
    public String createBook(String title, String author) {
        Book newBook = new Book();
        newBook.setAuthor(author);
        newBook.setName(title);
        bookRepository.save(newBook);
        return "success";
    }

    /**
     * @param id
     * @return
     */
    @PostMapping("/deleteBook")
    public String deleteBook(String id) {
        bookRepository.deleteById(Integer.parseInt(id));
        return "success";
    }

    /**
     * @param id
     * @param author
     * @return
     */
    @PostMapping("/changeAuthor")
    public String changeAuthor(String id, String author) {
        Book book = bookRepository.getOne(Integer.parseInt(id));
        book.setAuthor(author);
        bookRepository.save(book);
        return "Success";
    }

    /**
     * @param file
     * @return
     */
//    @PostMapping("/uploadFile")
//    public String uploadFile(MultipartFile file) throws FileNotFoundException {
//        if (!file.isEmpty()) {
//            File path = new File(ResourceUtils.getURL("src/main/resources/").getPath());
//            String baseUrl =  path.getAbsolutePath()+"/HeadImage/";
//            String originFileName = file.getOriginalFilename();
//            String typeStr = "";// with .
//            for (int i = originFileName.length() - 1; i > 0; i--) {
//                if (originFileName.charAt(i) == '.') {
//                    typeStr = originFileName.substring(i, originFileName.length());
//                    break;
//                }
//            }
//            String fileName = "haha" + typeStr;
//            try {
//                FileOutputStream fos = new FileOutputStream(baseUrl + fileName);
//                fos.write(file.getBytes()); // 写入文件
//                //System.out.println("文件上传成功");
//                return "文件上传成功";
//            } catch (Exception e) {
//                e.printStackTrace();
//                return "文件上传失败";
//            }
//        } else return "";
//    }
}

