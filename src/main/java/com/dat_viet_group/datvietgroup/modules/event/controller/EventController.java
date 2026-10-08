package com.dat_viet_group.datvietgroup.modules.event.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dat_viet_group.datvietgroup.core.common.ApiResponse;
import com.dat_viet_group.datvietgroup.modules.event.dto.request.CommentEventRequest;
import com.dat_viet_group.datvietgroup.modules.event.dto.request.EventRequest;
import com.dat_viet_group.datvietgroup.modules.event.dto.request.EventStatusRequest;
import com.dat_viet_group.datvietgroup.modules.event.dto.response.CommentEventResponse;
import com.dat_viet_group.datvietgroup.modules.event.dto.response.EventImageResponse;
import com.dat_viet_group.datvietgroup.modules.event.dto.response.EventMemberResponse;
import com.dat_viet_group.datvietgroup.modules.event.dto.response.EventResponse;
import com.dat_viet_group.datvietgroup.modules.event.enums.EventStatus;
import com.dat_viet_group.datvietgroup.modules.event.service.EventService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    // EVENT
    // GET /api/events?status=UPCOMING&keyword=abc&page=0&size=10&sort=startTime,asc 
    @GetMapping
    public ResponseEntity<ApiResponse<Page<EventResponse>>> getEvents(
            @RequestParam(required = false) EventStatus status,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "startTime", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.ok("Lấy danh sách sự kiện thành công", eventService.getEvents(status, keyword, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EventResponse>> getEvent(@PathVariable long id) {
        return ApiResponse.ok("Lấy chi tiết sự kiện thành công", eventService.getEvent(id));
    }


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<EventResponse>> createEvent(
            @Validated @ModelAttribute EventRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        return ApiResponse.created("Tạo sự kiện thành công", eventService.createEvent(request, images));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EventResponse>> updateEvent(@PathVariable long id,
            @Validated @RequestBody EventRequest request) {
        return ApiResponse.ok("Cập nhật sự kiện thành công", eventService.updateEvent(id, request));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<EventResponse>> updateStatus(@PathVariable long id,
            @Validated @RequestBody EventStatusRequest request) {
        return ApiResponse.ok("Cập nhật trạng thái thành công", eventService.updateStatus(id, request.getStatus()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(@PathVariable long id) {
        eventService.deleteEvent(id);
        return ApiResponse.ok("Xóa sự kiện thành công");
    }

    // ẢNH
    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<List<EventImageResponse>>> addImages(@PathVariable long id,
            @RequestPart("images") List<MultipartFile> images) {
        return ApiResponse.created("Thêm ảnh thành công", eventService.addImages(id, images));
    }

    @DeleteMapping("/{id}/images/{imageId}")
    public ResponseEntity<ApiResponse<Void>> deleteImage(@PathVariable long id, @PathVariable long imageId) {
        eventService.deleteImage(id, imageId);
        return ApiResponse.ok("Xóa ảnh thành công");
    }

    // THÀNH VIÊN
    @PostMapping("/{id}/join")
    public ResponseEntity<ApiResponse<EventMemberResponse>> joinEvent(@PathVariable long id) {
        return ApiResponse.ok("Tham gia sự kiện thành công", eventService.joinEvent(id));
    }

    @DeleteMapping("/{id}/join")
    public ResponseEntity<ApiResponse<Void>> leaveEvent(@PathVariable long id) {
        eventService.leaveEvent(id);
        return ApiResponse.ok("Rời sự kiện thành công");
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<ApiResponse<List<EventMemberResponse>>> getMembers(@PathVariable long id) {
        return ApiResponse.ok("Lấy danh sách thành viên thành công", eventService.getMembers(id));
    }

    // BÌNH LUẬN
    @GetMapping("/{id}/comments")
    public ResponseEntity<ApiResponse<Page<CommentEventResponse>>> getComments(@PathVariable long id,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.ok("Lấy bình luận thành công", eventService.getComments(id, pageable));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<ApiResponse<CommentEventResponse>> addComment(@PathVariable long id,
            @Validated @RequestBody CommentEventRequest request) {
        return ApiResponse.created("Bình luận thành công", eventService.addComment(id, request));
    }

    @DeleteMapping("/{id}/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable long id, @PathVariable long commentId) {
        eventService.deleteComment(id, commentId);
        return ApiResponse.ok("Xóa bình luận thành công");
    }
}
