package com.example.fingerprint_backend.domain.fingerprint.controller;

import com.example.fingerprint_backend.ApiResult;
import com.example.fingerprint_backend.domain.fingerprint.dto.RequestClassClose;
import com.example.fingerprint_backend.domain.fingerprint.service.classClosingTime.ClassClosingTimeApplicationService;
import com.example.fingerprint_backend.domain.fingerprint.service.log.LogApplicationService;
import com.example.fingerprint_backend.dto.CreateFingerPrintDto;
import com.example.fingerprint_backend.dto.CreateLogDto;
import com.example.fingerprint_backend.domain.fingerprint.entity.ClassClosingTime;
import com.example.fingerprint_backend.domain.fingerprint.entity.FingerPrintEntity;
import com.example.fingerprint_backend.domain.fingerprint.service.FingerPrintService;
import com.example.fingerprint_backend.service.Member.MemberQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Fingerprint - python", description = "지문 등록 및 인식 API / 指紋登録および認識API")
@RestController
@RequestMapping("/api/fingerprint")
@RequiredArgsConstructor
public class FingerPrintController {

    private final FingerPrintService fingerPrintService;
    private final MemberQueryService memberQueryService;
    private final LogApplicationService logApplicationService;
    private final ClassClosingTimeApplicationService classClosingTimeApplicationService;

    @Operation(operationId = "checkFingerprintAvailability", summary = "지문 등록 가능 여부 조회 / 指紋登録可否の確認", description = "입력한 학번에 대해 지문 등록이 가능한지 여부를 조회합니다。<br>"
            +
            "すでに登録されている場合は再登録不可です。<br><br>")
    @GetMapping("/students/{stdNum}")
    public ResponseEntity<ApiResult> check(@PathVariable String stdNum) {

        memberQueryService.getMemberByStudentNumber(stdNum);

        // 해당 학번의 지문이 등록되어 있는지 조회
        Boolean fingerPrintExist = fingerPrintService.isFingerPrintExist(stdNum);

        if (fingerPrintExist) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResult(false, "이미 지문 정보가 등록된 학번입니다. \n재등록은 웹에 로그인 후 지문 정보를 삭제해주세요", null));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResult(true, "지문 등록이 가능한 학번입니다.", null));
    }

    @Operation(operationId = "getAllFingerprints", summary = "모든 지문 정보 조회 / 全指紋情報の取得", description = "등록된 모든 지문 정보를 조회합니다.<br>"
            +
            "登録されているすべての指紋情報を取得します。")
    @GetMapping("/students")
    public ResponseEntity<ApiResult> getAllFingerprint(@RequestHeader HttpHeaders headers) {

        // String key = headers.getFirst("key");
        //
        // if (key != "fweagiaewfsdgbneahpfasdfvweaghaegaew") {
        // return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(false, "지문
        // 정보를 가져올 권한이 없습니다.", null));
        // }

        List<FingerPrintEntity> allFingerprint = fingerPrintService.getAllFingerprint();

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResult(true, "모든 지문 정보를 가져왔습니다.", allFingerprint));

    }

    @Operation(operationId = "registerFingerprint", summary = "지문 등록 / 指紋登録", description = "지문 인식기에서 전달된 학번과 지문 데이터를 이용해 지문을 등록합니다。<br>"
            +
            "指紋認識機から送信された学籍番号と指紋データを使って登録を行います。")
    @PostMapping("/students")
    public ResponseEntity<ApiResult> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "지문 등록 요청 본문 / 指紋登録リクエストボディ<br><br>" +
                    "• `fingerprint1` - 첫 번째 지문 데이터 (Base64 인코딩) / 最初の指紋データ (Base64エンコーディング)<br>" +
                    "• `fingerprint2` - 두 번째 지문 데이터 (Base64 인코딩) / 二番目の指紋データ (Base64エンコーディング)<br>" +
                    "• `std_num` - 학번 / 学籍番号<br>" +
                    "• `salt` - 암호화에 사용된 salt 값 / 暗号化に使用されたsalt値") @RequestBody CreateFingerPrintDto createFingerPrintDto) {

        FingerPrintEntity fingerPrintEntity = fingerPrintService.create(createFingerPrintDto);

        if (fingerPrintEntity.getStudentNumber().equals(createFingerPrintDto.getStd_num())) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResult(true, "지문 등록에 성공하였습니다.", fingerPrintEntity));
        }

        return ResponseEntity.status(HttpStatus.OK).body((new ApiResult(false, "지문 등록에 실패하였습니다. \n다시 시도해주세요.", null)));
    }

    @Operation(operationId = "deleteFingerprint", summary = "지문 삭제 / 指紋削除", description = "학번을 기반으로 등록된 지문 데이터를 삭제합니다。<br>"
            +
            "学籍番号に基づいて登録された指紋データを削除します。")
    @DeleteMapping("/students/{stdNum}")
    public ResponseEntity<ApiResult> delete(@PathVariable String stdNum) {

        Boolean delete = fingerPrintService.delete(stdNum);

        if (!delete) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResult(false, "지문 데이터 삭제 실패", null));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResult(true, "지문 데이터 삭제 완료", null));
    }

    @Operation(operationId = "createLog", summary = "지문 인식 시 로그 생성 / 指紋認識時のログ生成", description = "지문 인식 시 발생하는 로그를 생성합니다。<br>"
            +
            "指紋認識時に発生するログを生成します。")
    @PostMapping("/logs")
    public ResponseEntity<ApiResult> createLog(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "로그 생성 요청 본문 / ログ生成リクエストボディ<br><br>" +
                    "• `std_num` - 학번 / 学籍番号<br>" +
                    "• `action` - 행동 (등교, 하교, 외출 등) / 行動 (登校、下校、外出など)") @RequestBody CreateLogDto createLogDto) {

        ApiResult response = logApplicationService.routeLog(
                createLogDto.getStd_num(),
                createLogDto.getAction(),
                LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(operationId = "closeClass", summary = "문 닫힘 / 門閉め", description = "문 닫힘을 담당하는 학번을 기반으로 문을 닫습니다。<br>" +
            "門閉めを担当する学籍番号に基づいて門を閉めます。")
    @PostMapping("/close")
    public ResponseEntity<ApiResult> close(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "문 닫힘 요청 본문 / 門閉めリクエストボディ<br><br>" +
                    "• `closingMember` - 문 닫힘 담당자 학번 / 門閉め担当者の学籍番号") @RequestBody RequestClassClose requestClassClose) {

        ClassClosingTime closingTime = classClosingTimeApplicationService.createClosingTime(LocalDateTime.now(),
                requestClassClose.closingMember());

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResult(true, "성공적으로 문을 닫았습니다.", closingTime));
    }
}
