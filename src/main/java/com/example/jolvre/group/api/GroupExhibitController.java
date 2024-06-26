package com.example.jolvre.group.api;

import com.example.jolvre.auth.PrincipalDetails;
import com.example.jolvre.group.dto.GroupExhibitDTO.GroupExhibitCreateRequest;
import com.example.jolvre.group.dto.GroupExhibitDTO.GroupExhibitInfoResponse;
import com.example.jolvre.group.dto.GroupExhibitDTO.GroupExhibitInfoResponses;
import com.example.jolvre.group.dto.GroupExhibitDTO.GroupExhibitUserResponses;
import com.example.jolvre.group.dto.GroupExhibitDTO.GroupInvitationResponse;
import com.example.jolvre.group.dto.GroupExhibitDTO.GroupUpdateRequest;
import com.example.jolvre.group.service.GroupExhibitService;
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

//todo : update 기능

@Tag(name = "Group Exhibit", description = "단체전시 API")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/groups")
public class GroupExhibitController {
    private final GroupExhibitService groupExhibitService;

    @Operation(summary = "단체 전시 생성")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createGroup(@ModelAttribute GroupExhibitCreateRequest request
            , @AuthenticationPrincipal PrincipalDetails principalDetails) {

        groupExhibitService.createGroupExhibit(principalDetails.getId(), request);
        log.info("[GROUP EXHIBIT] {}님 단체 전시 생성", principalDetails.getUser().getNickname());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "단체 전시 조회 (단체 전시 탭에서)")
    @GetMapping()
    public ResponseEntity<GroupExhibitInfoResponses> getAllGroupExhibit() {
        GroupExhibitInfoResponses responses = groupExhibitService.getAllGroupExhibitInfo();

        return ResponseEntity.ok().body(responses);
    }

    @Operation(summary = "단체 전시 상세 조회")
    @GetMapping("/{groupId}")
    public ResponseEntity<GroupExhibitInfoResponse> getGroupExhibit(@PathVariable Long groupId) {
        GroupExhibitInfoResponse response = groupExhibitService.getGroupExhibitInfo(groupId);

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "유저의 단체 전시 조회(유저 탭에서)")
    @GetMapping("/me")
    public ResponseEntity<GroupExhibitInfoResponses> getAllUserGroupExhibit(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        GroupExhibitInfoResponses responses = groupExhibitService.getAllUserGroupExhibitInfo(principalDetails.getId());

        return ResponseEntity.ok().body(responses);
    }

    @Operation(summary = "단체 전시 전시 추가")
    @PostMapping("/{groupId}/exhibits/{exhibitId}")
    public ResponseEntity<Void> addExhibit(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                           @PathVariable Long groupId, @PathVariable Long exhibitId
    ) {
        groupExhibitService.addExhibit(principalDetails.getId(), groupId, exhibitId);
        log.info("[GROUP EXHIBIT] {}님 단체 전시 전시 추가 , Group Id = {}", principalDetails.getUser().getNickname(), groupId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "단체 전시 회원 조회")
    @GetMapping("{groupId}/members")
    public ResponseEntity<GroupExhibitUserResponses> getGroupExhibitUsers(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long groupId) {
        GroupExhibitUserResponses responses =
                groupExhibitService.getGroupExhibitUsers(principalDetails.getId(), groupId);

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "단체 전시 삭제", description = "단체 전시를 삭제합니다")
    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteGroupExhibit(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                   @PathVariable Long groupId) {

        groupExhibitService.deleteGroup(groupId, principalDetails.getId());
        log.info("[GROUP EXHIBIT] {}님 단체 전시 삭제 , Group Id = {}", principalDetails.getUser().getNickname(), groupId);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "매니저 추가")
    @PostMapping("/{groupId}/manager/{toUserId}")
    public ResponseEntity<Void> addManager(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                           @PathVariable Long toUserId,
                                           @PathVariable Long groupId
    ) {
        groupExhibitService.addManager(principalDetails.getId(), toUserId, groupId);
        log.info("[GROUP EXHIBIT] {}님 매니저 추가 Group Id = {}", principalDetails.getUser().getNickname(), groupId);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "단체 전시 수정", description = "단체 전시를 수정합니다")
    @PatchMapping(path = "/{groupId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateGroupExhibit(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                   @PathVariable Long groupId,
                                                   @ModelAttribute GroupUpdateRequest request) {

        groupExhibitService.updateGroup(groupId, principalDetails.getId(), request);
        log.info("[GROUP EXHIBIT] {}님 단체 전시 수정, Group Id = {}", principalDetails.getUser().getNickname(), groupId);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "단체 전시 초대장 생성", description = "단체 전시 초대장을 생성한다")
    @GetMapping("/{groupId}/invitaion")
    public ResponseEntity<GroupInvitationResponse> createInvitation(@PathVariable Long groupId) {
        GroupInvitationResponse response = groupExhibitService.createInvitation(groupId);

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "키워드를 통한 조회", description = "키워드를 통해 단체 전시회 정보를 가져온다")
    @GetMapping("/keyword")
    public ResponseEntity<Page<GroupExhibitInfoResponse>> searchByKeyword(
            @RequestParam(required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        PageRequest pageable = PageRequest.of(page - 1, size, Sort.by("createdDate").descending());
        Page<GroupExhibitInfoResponse> response = groupExhibitService.getExhibitInfoByKeyword(keyword,
                pageable);

        return ResponseEntity.ok().body(response);
    }
}

