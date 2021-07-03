package com.zl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
@EnableJpaAuditing
@SpringBootApplication
public class LibraryApplication {
    public LibraryApplication() throws FileNotFoundException {
    }

    public static void main(String[] args) {
        SpringApplication.run(LibraryApplication.class, args);
    }

}
