package com.example;

import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FileGetController {

    @GetMapping("/get-file")
    public ResponseEntity<String> getFileContent() {
        try {
            // クラスパスからリソースを読み込む
            Resource resource = new ClassPathResource("static/testfile.txt");
            Path filePath = resource.getFile().toPath();

            // ファイルの内容を文字列として読み取る
            String content = Files.readString(filePath);

            return new ResponseEntity<>(content, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("ファイルの読み込みに失敗しました。", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
