package com.dat_viet_group.datvietgroup.modules.event.dto.response;

import java.time.LocalDateTime;

import com.dat_viet_group.datvietgroup.modules.event.entity.EventMember;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EventMemberResponse {

    private long id;
    private UserSummaryResponse user;
    private LocalDateTime joinedAt;

    public static EventMemberResponse from(EventMember member) {
        return new EventMemberResponse(member.getId(), UserSummaryResponse.from(member.getUser()),
                member.getJoinedAt());
    }
}
