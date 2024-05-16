package com.example.jolvre.group.dto;

import com.example.jolvre.exhibition.dto.ExhibitDTO.ExhibitInfoResponses;
import com.example.jolvre.group.entity.GroupExhibit;
import com.example.jolvre.user.dto.UserDTO.UserInfoResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class GroupExhibitDTO {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GroupExhibitCreateRequest {
        private String name;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GroupExhibitAddExhibitRequest {
        private String nickname;
    }


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GroupExhibitInfoResponse {
        private Long id;
        private String name;
        private ExhibitInfoResponses exhibits;

        public static GroupExhibitInfoResponse toDTO(GroupExhibit groupExhibit) {
            return new GroupExhibitInfoResponse(groupExhibit.getId(), groupExhibit.getName(),
                    ExhibitInfoResponses.toDTO(groupExhibit.getExhibits()));
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GroupExhibitInfoResponses {
        private List<GroupExhibitInfoResponse> groupExhibitResponses;

        public static GroupExhibitInfoResponses toDTO(List<GroupExhibit> groupExhibits) {
            List<GroupExhibitInfoResponse> responses = new ArrayList<>();
            groupExhibits.forEach(group -> responses.add(GroupExhibitInfoResponse.toDTO(group)));

            return new GroupExhibitInfoResponses(responses);
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GroupExhibitUserResponse {
        private String role;
        private UserInfoResponse userInfoResponse;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GroupExhibitUserResponses {
        private List<GroupExhibitUserResponse> groupExhibitUserResponses;
    }
}
