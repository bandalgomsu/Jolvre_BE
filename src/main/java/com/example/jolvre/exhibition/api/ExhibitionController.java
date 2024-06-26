package com.example.jolvre.exhibition.api;

import com.example.jolvre.auth.PrincipalDetails;
import com.example.jolvre.exhibition.dto.ExhibitDTO.ExhibitInfoResponse;
import com.example.jolvre.exhibition.dto.ExhibitDTO.ExhibitInfoResponses;
import com.example.jolvre.exhibition.dto.ExhibitDTO.ExhibitInvitationResponse;
import com.example.jolvre.exhibition.dto.ExhibitDTO.ExhibitUpdateRequest;
import com.example.jolvre.exhibition.dto.ExhibitDTO.ExhibitUploadRequest;
import com.example.jolvre.exhibition.dto.ExhibitDTO.ExhibitUploadResponse;
import com.example.jolvre.exhibition.service.ExhibitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Exhibit", description = "졸업 작품 전시 API")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/exhibits")
public class ExhibitionController {
    private final ExhibitService exhibitService;

    @Operation(summary = "전시 업로드")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExhibitUploadResponse> uploadExhibit(@ModelAttribute ExhibitUploadRequest request,
                                                               @AuthenticationPrincipal PrincipalDetails principalDetails) {
        ExhibitUploadResponse response = exhibitService.uploadExhibit(request, principalDetails.getId());
        log.info("[EXHIBIT] {}님 전시 업로드 Exhibit Id = {}", principalDetails.getUser().getEmail(),
                response.getExhibitId());
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "비동기 전시 업로드")
    @PostMapping(path = "/async", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExhibitUploadResponse> uploadAsyncExhibit(@ModelAttribute ExhibitUploadRequest request,
                                                                    @AuthenticationPrincipal PrincipalDetails principalDetails) {
        ExhibitUploadResponse response = exhibitService.uploadExhibitAsync(request, principalDetails.getId());
        log.info("[EXHIBIT] {}님 전시 업로드 Exhibit Id = {}", principalDetails.getUser().getEmail(),
                response.getExhibitId());
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "해당 유저의 모든 전시 조회 (유저탭에서)")
    @GetMapping("/me")
    public ResponseEntity<ExhibitInfoResponses> getAllUserExhibit(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        ExhibitInfoResponses response = exhibitService.getAllUserExhibitInfo(principalDetails.getId());

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "전시 삭제")
    @DeleteMapping("/me/{exhibitId}")
    public void deleteExhibit(@AuthenticationPrincipal PrincipalDetails principalDetails,
                              @PathVariable Long exhibitId) {
        exhibitService.deleteExhibit(exhibitId, principalDetails.getId());
        log.info("[EXHIBIT] {}님 전시 삭제 Exhibit Id = {}", principalDetails.getUser().getNickname(), exhibitId);
    }

    @Operation(summary = "전시 업데이트")
    @PatchMapping(path = "/me/{exhibitId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateExhibit(@AuthenticationPrincipal PrincipalDetails principalDetails,
                              @PathVariable Long exhibitId, @ModelAttribute ExhibitUpdateRequest request) {

        exhibitService.updateExhibit(exhibitId, principalDetails.getId(), request);
        log.info("[EXHIBIT] {}님 전시 업데이트 Exhibit Id = {}", principalDetails.getUser().getEmail(), exhibitId);
    }


    @Operation(summary = "전시 배포")
    @PostMapping("/me/{exhibitId}/distribute")
    public ResponseEntity<Void> distributeExhibit(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                  @PathVariable Long exhibitId) {
        exhibitService.distributeExhibit(exhibitId, principalDetails.getId());
        log.info("[EXHIBIT] {}님 전시 배포 , Exhibit Id = {}", principalDetails.getUser().getEmail(), exhibitId);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "전체 전시 조회 (전시탭에서)")
    @GetMapping
    public ResponseEntity<ExhibitInfoResponses> getAllExhibit() {
        ExhibitInfoResponses responses = exhibitService.getAllExhibitInfo();

        return ResponseEntity.ok().body(responses);
    }

    @Operation(summary = "전체 작품 종류별 전시 조회 (전시탭에서)")
    @GetMapping("/{workType}")
    public ResponseEntity<ExhibitInfoResponses> getAllExhibit(@PathVariable String workType) {
        ExhibitInfoResponses responses = exhibitService.getAllExhibitInfoByWorkType(workType);

        return ResponseEntity.ok().body(responses);
    }

    @Operation(summary = "전시 상세 조회")
    @GetMapping("/{exhibitId}")
    public ResponseEntity<ExhibitInfoResponse> getExhibit(@PathVariable Long exhibitId) {
        ExhibitInfoResponse response = exhibitService.getExhibitInfo(exhibitId);

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "초대장 생성", description = "해당 전시에 맞는 초대장을 생성해준다")
    @GetMapping("/{exhibitId}/invitation")
    public ResponseEntity<ExhibitInvitationResponse> createInvitation(@PathVariable Long exhibitId) {
        ExhibitInvitationResponse response = exhibitService.createInvitation(exhibitId);

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "키워드를 통한 조회", description = "키워드를 통해 전시회 정보를 가져온다")
    @GetMapping("/keyword")
    public ResponseEntity<Page<ExhibitInfoResponse>> searchByKeyword(@RequestParam(required = false) String keyword,
                                                                     @RequestParam(value = "page", defaultValue = "1") int page,
                                                                     @RequestParam(value = "size", defaultValue = "10") int size) {
        PageRequest pageable = PageRequest.of(page - 1, size, Sort.by("createdDate").descending());

        Page<ExhibitInfoResponse> response = exhibitService.getExhibitInfoByKeyword(keyword, pageable);

        return ResponseEntity.ok().body(response);
    }


}
