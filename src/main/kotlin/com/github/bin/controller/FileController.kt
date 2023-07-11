package com.github.bin.controller

import com.github.bin.service.FileService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.core.io.InputStreamResource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

/**
 * @author bin
 * @since 2023/07/11
 */
@Tag(name = "file")
@RestController
@RequestMapping("/api/file")
class FileController(
        private val fileService: FileService
) {
    @GetMapping("/download")
    fun download(@RequestParam filePath: String): ResponseEntity<InputStreamResource> {
        return fileService.downloadFile(filePath)
    }

    @PostMapping("/upload")
    fun upload(@RequestPart mutipartFile: MultipartFile, @RequestParam filePath: String): String {
        return fileService.uploadFile(filePath, mutipartFile)
    }

}
