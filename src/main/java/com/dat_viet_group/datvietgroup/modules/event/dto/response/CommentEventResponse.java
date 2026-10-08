package com.dat_viet_group.datvietgroup.modules.event.dto.response;

import java.time.LocalDateTime;

import com.dat_viet_group.datvietgroup.modules.event.entity.CommentEvent;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentEventResponse {

    private long id;
    private long eventId;
    private UserSummaryResponse user;
    private String content;
    private LocalDateTime createdAt;

    public static CommentEventResponse from(CommentEvent comment) {
        return new CommentEventResponse(comment.getId(), comment.getEvent().getId(),
                UserSummaryResponse.from(comment.getUser()), comment.getContent(), comment.getCreatedAt());
    }
}
