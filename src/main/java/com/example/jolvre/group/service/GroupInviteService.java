package com.example.jolvre.group.service;

import com.example.jolvre.common.error.group.GroupExhibitNotFoundException;
import com.example.jolvre.common.error.group.GroupInviteDuplicateException;
import com.example.jolvre.group.GroupRoleChecker;
import com.example.jolvre.group.dto.GroupInviteDTO.InviteResponses;
import com.example.jolvre.group.entity.GroupExhibit;
import com.example.jolvre.group.entity.GroupInviteState;
import com.example.jolvre.group.entity.GroupRole;
import com.example.jolvre.group.entity.InviteState;
import com.example.jolvre.group.entity.Member;
import com.example.jolvre.group.repository.GroupExhibitRepository;
import com.example.jolvre.group.repository.GroupInviteStateRepository;
import com.example.jolvre.group.repository.MemberRepository;
import com.example.jolvre.notification.entity.NotificationType;
import com.example.jolvre.notification.service.NotificationService;
import com.example.jolvre.user.entity.User;
import com.example.jolvre.user.service.UserService;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupInviteService {
    private final UserService userService;
    private final GroupExhibitRepository groupExhibitRepository;
    private final GroupInviteStateRepository groupInviteStateRepository;
    private final MemberRepository memberRepository;
    private final GroupRoleChecker checker;
    private final NotificationService notificationService;

    @Transactional // 유저 초대
    public void inviteUser(Long fromUser, String toUser, Long groupId) {
        User from = userService.getUserById(fromUser);
        User to = userService.getUserByNickname(toUser);

        GroupExhibit group = groupExhibitRepository.findById(groupId)
                .orElseThrow(GroupExhibitNotFoundException::new);

        if (groupInviteStateRepository.existsByGroupExhibitIdAndUserId(groupId, to.getId())) {
            throw new GroupInviteDuplicateException();
        }

        checker.isManager(group, from); // 초대 보내는 사람 -> 매니저
        checker.isNotMember(group, to); // 초대 받는 사람 -> 멤버면 안됨

        GroupInviteState inviteState = GroupInviteState.builder()
                .groupExhibit(group)
                .inviteState(InviteState.PEND)
                .user(to).build();

        groupInviteStateRepository.save(inviteState);

        notificationService.commentNotificationCreate(fromUser, to.getId(),
                group.getName() + "에서 초대요청이 도착했습니다",
                NotificationType.GROUP_EXHIBIT_INVITE);
    }

    @Transactional //초대 리스트 조회
    public InviteResponses getAllInvite(Long userId) {
        List<GroupInviteState> invites = groupInviteStateRepository.findAllByUserId(userId);

        return InviteResponses.toDTO(invites);
    }

    @Transactional //초대 상태 체크 (수락 OR 거절)
    public void checkInviteStatus(Long inviteId, InviteState inviteState, Long loginUserId) {
        GroupInviteState invite = groupInviteStateRepository.findByIdAndUserId(inviteId, loginUserId)
                .orElseThrow(GroupExhibitNotFoundException::new);

        if (InviteState.ACCEPT.equals(inviteState)) {
            User user = invite.getUser();
            GroupExhibit group = invite.getGroupExhibit();

            acceptInviteStatus(invite, user, group);
            return;
        }

        refuseInviteStatus(invite);
    }

    private void acceptInviteStatus(GroupInviteState invite, User user, GroupExhibit group) {
        groupInviteStateRepository.delete(invite);
        Member member = Member.builder()
                .user(user).groupExhibit(group)
                .groupRole(GroupRole.MEMBER)
                .build();
        memberRepository.save(member);

        group.addMember(member);
        groupExhibitRepository.save(group);
    }

    private void refuseInviteStatus(GroupInviteState invite) {
        groupInviteStateRepository.delete(invite);
    }
}
