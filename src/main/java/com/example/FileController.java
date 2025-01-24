package com.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FileController {
	//	ファイルの中身を取得する
	@GetMapping("/files/{fileName}")
	public ResponseEntity<String> getFileContent(@PathVariable String fileName) {
		try {
			// クラスパスからリソースを読み込む
			Resource resource = new ClassPathResource("static/" + fileName);
			Path filePath = resource.getFile().toPath();

			// ファイルの内容を文字列として読み取る
			String content = Files.readString(filePath);

			return new ResponseEntity<>(content, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>("ファイルの読み込みに失敗しました。", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	//	新しくファイルを作成する
	@PostMapping("/files")
	public ResponseEntity<String> createFile(@RequestParam("file_name") String fileName) {
		try {
			// `static`ディレクトリへのパスを作成
			String directory = "src/main/resources/static/";
			Path path = Paths.get(directory, fileName);

			// ファイルが既に存在するか確認
			if (Files.exists(path)) {
				return ResponseEntity.status(HttpStatus.CONFLICT)
						.body("File already exists: " + fileName);
			}

			// ファイルの新規作成
			Files.createFile(path);

			return ResponseEntity.status(HttpStatus.CREATED)
					.body("File created successfully: " + fileName);

		} catch (IOException e) {
			// ファイル作成時のエラーをキャッチ
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to create file: " + e.getMessage());
		} catch (Exception e) {
			// その他のエラーをキャッチ
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An unexpected error occurred: " + e.getMessage());
		}
	}

//	ファイルを削除する
	@DeleteMapping("/files")
	public ResponseEntity<?> deleteFile(@RequestParam("file_name") String fileName) {
		try {
			// `static`ディレクトリへのパスを作成
			String directory = "src/main/resources/static/";
			Path path = Paths.get(directory, fileName);
			if (!Files.exists(path)) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("File has already been deleted or does not exist: "+fileName);
			}
			Files.delete(path);
			return ResponseEntity.status(HttpStatus.OK)
					.body("File successfully deleted:" + fileName);
		} catch (Exception e) {
			// その他のエラーをキャッチ
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An unexpected error occurred: " + e.getMessage());
		}
	}

	//	作成済みのファイルを表示
	@GetMapping("/files/list")
	public ResponseEntity<?> getFilesList() {
		try {
			// ディレクトリのパスを取得
			String directory = "static";
			Path path = new ClassPathResource(directory).getFile().toPath();

			// ファイルリストを取得し、文字列リストに変換
			List<String> fileList;
			try (Stream<Path> filePathStream = Files.list(path)) {
				fileList = filePathStream
						.map(filePath -> filePath.getFileName().toString()) // ファイル名のみ取得
						.collect(Collectors.toList());
			}

			// JSON形式でファイルリストを返す
			return ResponseEntity.ok(fileList);
		} catch (IOException e) {
			// エラー時のレスポンス
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error retrieving file list: " + e.getMessage());
		}
	}

}
