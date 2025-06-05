package com.example.fingerprint_backend.controller;

import com.example.fingerprint_backend.ApiResult;
import com.example.fingerprint_backend.jwt.CustomUserDetails;
import com.example.fingerprint_backend.service.AccountService;
import com.example.fingerprint_backend.service.FileService;
import com.example.fingerprint_backend.types.FileType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Member", description = "회원 관리 API / 会員管理API")
@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final FileService fileService;
    private final AccountService accountService;
    private static final String PROFILE_IMAGE_PATH = "images/profile/";
    private static final Long MAX_PROFILE_IMAGE_SIZE = 5 * 1024 * 1024L; // 5MB
    private static final String URL = "https://bannote.org/";

    @Value("${custom.aws.s3.image-bucket.name}")
    private String IMAGE_BUCKET_NAME;

    public MemberController(FileService fileService, AccountService accountService) {
        this.fileService = fileService;
        this.accountService = accountService;
    }

    @Operation(operationId = "updateProfileImage", summary = "프로필 이미지 업데이트 / プロフィール画像更新", description = "사용자의 프로필 이미지를 업데이트합니다。/ ユーザーのプロフィール画像を更新します。<br>"
            +
            "파일은 최대 5MB까지 업로드 가능합니다。/ ファイルは最大5MBまでアップロード可能です。")
    @PutMapping("/me/profile-image")
    public ResponseEntity<ApiResult> UpdateProfileImage(@AuthenticationPrincipal CustomUserDetails user,
            @RequestParam("file") MultipartFile file) {

        String path = PROFILE_IMAGE_PATH + user.getUsername();
        String url = fileService.storeFile(FileType.IMAGE, file, path, MAX_PROFILE_IMAGE_SIZE, IMAGE_BUCKET_NAME);
        accountService.setProfileImage(user.getUsername(), url);

        return ResponseEntity.ok(new ApiResult(true, "프로필 이미지 업데이트 성공", url));
    }
}
