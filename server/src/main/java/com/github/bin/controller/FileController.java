package com.github.bin.controller;

import com.github.bin.service.FileService;
import com.github.bin.service.RoomService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author bin
 * @since 2023/08/22
 */
@Tag(name = "file")
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
@Slf4j
public class FileController {
    private final FileService fileService;

    @GetMapping("/download/{*path}")
    public ResponseEntity<InputStreamResource> downloadByPath(
            @PathVariable String path, @RequestHeader HttpHeaders headers,
            @RequestParam(required = false, defaultValue = "false") boolean download
    ) {
        try {
            return fileService.downloadFile(path, headers, download);
        } catch (Exception e) {
            log.warn("download file failed", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/upload")
    public String upload(
            @RequestPart MultipartFile mutipartFile,
            @RequestParam String path,
            @RequestParam String name
    ) {
        val roomConfig = RoomService.getSafe(name);
        if (roomConfig == null) {
            return "404";
        }
        val filePath = name + "/" + path;
        try {
            return fileService.uploadFile(filePath, mutipartFile);
        } catch (Exception e) {
            log.warn("upload file failed", e);
            return "500";
        }
    }

}
