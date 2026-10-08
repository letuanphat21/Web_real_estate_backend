package com.dat_viet_group.datvietgroup.modules.event.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.dat_viet_group.datvietgroup.modules.event.dto.request.CommentEventRequest;
import com.dat_viet_group.datvietgroup.modules.event.dto.request.EventRequest;
import com.dat_viet_group.datvietgroup.modules.event.dto.response.CommentEventResponse;
import com.dat_viet_group.datvietgroup.modules.event.dto.response.EventImageResponse;
import com.dat_viet_group.datvietgroup.modules.event.dto.response.EventMemberResponse;
import com.dat_viet_group.datvietgroup.modules.event.dto.response.EventResponse;
import com.dat_viet_group.datvietgroup.modules.event.enums.EventStatus;

public interface EventService {

    // EVENT 
    Page<EventResponse> getEvents(EventStatus status, String keyword, Pageable pageable);

    EventResponse getEvent(long eventId);

    EventResponse createEvent(EventRequest request, List<MultipartFile> images);

    EventResponse updateEvent(long eventId, EventRequest request);

    EventResponse updateStatus(long eventId, EventStatus status);

    void deleteEvent(long eventId);

    // ẢNH 
    List<EventImageResponse> addImages(long eventId, List<MultipartFile> images);

    void deleteImage(long eventId, long imageId);

    // THÀNH VIÊN 
    EventMemberResponse joinEvent(long eventId);

    void leaveEvent(long eventId);

    List<EventMemberResponse> getMembers(long eventId);

    // BÌNH LUẬN 
    Page<CommentEventResponse> getComments(long eventId, Pageable pageable);

    CommentEventResponse addComment(long eventId, CommentEventRequest request);

    void deleteComment(long eventId, long commentId);
}
