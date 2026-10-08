package com.dat_viet_group.datvietgroup.modules.event.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.dat_viet_group.datvietgroup.modules.event.enums.EventStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EventResponse {
    private long id;
    private String title;
    private String content;
    private String location;
    private int maxAttendees;
    private long attendeeCount;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private EventStatus status;
    private UserSummaryResponse createdBy;
    private List<EventImageResponse> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
