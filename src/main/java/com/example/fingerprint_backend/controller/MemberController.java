package com.example.fingerprint_backend.controller;

import com.example.fingerprint_backend.ApiResponse;
import com.example.fingerprint_backend.jwt.CustomUserDetails;
import com.example.fingerprint_backend.service.FileService;
import com.example.fingerprint_backend.types.FileType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final FileService fileService;
    private static final String PROFILE_IMAGE_PATH = "images/profile/";
    private static final Long MAX_PROFILE_IMAGE_SIZE = 5 * 1024 * 1024L; // 5MB

    public MemberController(FileService fileService) {
        this.fileService = fileService;
    }

    @PutMapping("/me/profile-image")
    public ResponseEntity<ApiResponse> UpdateProfileImage(@AuthenticationPrincipal CustomUserDetails user,
                                                          @RequestPart("file") MultipartFile file) {

        String path = PROFILE_IMAGE_PATH + user.getUsername();
        String url = fileService.storeFile(FileType.IMAGE, file, path, MAX_PROFILE_IMAGE_SIZE);

//        TODO : DB에 프로필 이미지 URL 저장하는 로직 추가

        return ResponseEntity.ok(new ApiResponse(true, "프로필 이미지 업데이트 성공", null));
    }
}
