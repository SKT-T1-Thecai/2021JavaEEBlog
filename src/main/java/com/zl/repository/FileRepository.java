package com.zl.repository;

import com.zl.entity.Collect;
import com.zl.entity.File;
import com.zl.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<File,Integer>{
    List<File> findFilesByUploader(User uploader);
    List<File> findFilesByFilePathContaining(String filePath);

}
