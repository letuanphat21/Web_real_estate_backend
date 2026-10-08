package com.dat_viet_group.datvietgroup.modules.event.service.serviceimpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.dat_viet_group.datvietgroup.core.cloudinary.service.CloudinaryService;
import com.dat_viet_group.datvietgroup.core.exception.AppException;
import com.dat_viet_group.datvietgroup.core.exception.ErrorCode;
import com.dat_viet_group.datvietgroup.modules.event.dao.CommentEventRepository;
import com.dat_viet_group.datvietgroup.modules.event.dao.EventImageRepository;
import com.dat_viet_group.datvietgroup.modules.event.dao.EventMemberRepository;
import com.dat_viet_group.datvietgroup.modules.event.dao.EventRepository;
import com.dat_viet_group.datvietgroup.modules.event.dto.request.CommentEventRequest;
import com.dat_viet_group.datvietgroup.modules.event.dto.request.EventRequest;
import com.dat_viet_group.datvietgroup.modules.event.dto.response.CommentEventResponse;
import com.dat_viet_group.datvietgroup.modules.event.dto.response.EventImageResponse;
import com.dat_viet_group.datvietgroup.modules.event.dto.response.EventMemberResponse;
import com.dat_viet_group.datvietgroup.modules.event.dto.response.EventResponse;
import com.dat_viet_group.datvietgroup.modules.event.dto.response.UserSummaryResponse;
import com.dat_viet_group.datvietgroup.modules.event.entity.CommentEvent;
import com.dat_viet_group.datvietgroup.modules.event.entity.Event;
import com.dat_viet_group.datvietgroup.modules.event.entity.EventImage;
import com.dat_viet_group.datvietgroup.modules.event.entity.EventMember;
import com.dat_viet_group.datvietgroup.modules.event.enums.EventStatus;
import com.dat_viet_group.datvietgroup.modules.event.service.EventService;
import com.dat_viet_group.datvietgroup.modules.user.entity.User;
import com.dat_viet_group.datvietgroup.modules.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private static final String IMAGE_FOLDER = "events";

    private final EventRepository eventRepository;
    private final EventImageRepository eventImageRepository;
    private final EventMemberRepository eventMemberRepository;
    private final CommentEventRepository commentEventRepository;
    private final CloudinaryService cloudinaryService;
    private final UserService userService;

    // EVENT
    @Override
    @Transactional(readOnly = true)
    public Page<EventResponse> getEvents(EventStatus status, String keyword, Pageable pageable) {
        String kw = StringUtils.hasText(keyword) ? keyword.trim() : null;
        return eventRepository.search(status, kw, pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponse getEvent(long eventId) {
        return toResponse(findEvent(eventId));
    }

    @Override
    @Transactional
    public EventResponse createEvent(EventRequest request, List<MultipartFile> images) {
        validateTime(request);
        User currentUser = getCurrentUser();

        Event event = new Event();
        applyRequest(event, request);
        event.setUser(currentUser);
        event.setStatus(EventStatus.UPCOMING);
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        eventRepository.save(event);

        if (hasFiles(images)) {
            saveImages(event, images);
        }
        return toResponse(event);
    }

    @Override
    @Transactional
    public EventResponse updateEvent(long eventId, EventRequest request) {
        validateTime(request);
        Event event = findEvent(eventId);
        checkOwnerOrAdmin(event.getUser());

        if (request.getMaxAttendees() > 0
                && eventMemberRepository.countByEventId(eventId) > request.getMaxAttendees()) {
            throw new AppException(ErrorCode.EVENT_INVALID_MAX_ATTENDEES);
        }

        applyRequest(event, request);
        event.setUpdatedAt(LocalDateTime.now());
        return toResponse(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventResponse updateStatus(long eventId, EventStatus status) {
        Event event = findEvent(eventId);
        checkOwnerOrAdmin(event.getUser());

        event.setStatus(status);
        event.setUpdatedAt(LocalDateTime.now());
        return toResponse(eventRepository.save(event));
    }

    @Override
    @Transactional
    public void deleteEvent(long eventId) {
        Event event = findEvent(eventId);
        checkOwnerOrAdmin(event.getUser());

        List<EventImage> images = eventImageRepository.findByEventIdOrderByIdAsc(eventId);
        List<String> urls = images.stream().map(EventImage::getImageUrl).toList();
        // Xóa bảng con trước để không vướng khóa ngoại
        commentEventRepository.deleteAllByEventId(eventId);
        eventMemberRepository.deleteAllByEventId(eventId);
        eventImageRepository.deleteAll(images);
        eventRepository.delete(event);

        deleteFromCloudinaryAfterCommit(urls);
    }

    // ẢNH
    @Override
    @Transactional
    public List<EventImageResponse> addImages(long eventId, List<MultipartFile> images) {
        Event event = findEvent(eventId);
        checkOwnerOrAdmin(event.getUser());
        return saveImages(event, images).stream().map(EventImageResponse::from).toList();
    }

    @Override
    @Transactional
    public void deleteImage(long eventId, long imageId) {
        EventImage image = eventImageRepository.findById(imageId)
                .filter(img -> img.getEvent().getId() == eventId)
                .orElseThrow(() -> new AppException(ErrorCode.EVENT_IMAGE_NOT_FOUND));
        checkOwnerOrAdmin(image.getEvent().getUser());

        eventImageRepository.delete(image);
        deleteFromCloudinaryAfterCommit(List.of(image.getImageUrl()));
    }

    // THÀNH VIÊN
    @Override
    @Transactional
    public EventMemberResponse joinEvent(long eventId) {
        Event event = findEvent(eventId);
        User currentUser = getCurrentUser();

        if (event.getStatus() == EventStatus.COMPLETED || event.getStatus() == EventStatus.CANCELLED) {
            throw new AppException(ErrorCode.EVENT_NOT_JOINABLE);
        }
        if (eventMemberRepository.existsByEventIdAndUserId(eventId, currentUser.getId())) {
            throw new AppException(ErrorCode.EVENT_ALREADY_JOINED);
        }
        if (event.getMaxAttendees() > 0
                && eventMemberRepository.countByEventId(eventId) >= event.getMaxAttendees()) {
            throw new AppException(ErrorCode.EVENT_FULL);
        }

        EventMember member = new EventMember();
        member.setEvent(event);
        member.setUser(currentUser);
        member.setJoinedAt(LocalDateTime.now());
        return EventMemberResponse.from(eventMemberRepository.save(member));
    }

    @Override
    @Transactional
    public void leaveEvent(long eventId) {
        findEvent(eventId);
        User currentUser = getCurrentUser();
        EventMember member = eventMemberRepository.findByEventIdAndUserId(eventId, currentUser.getId())
                .orElseThrow(() -> new AppException(ErrorCode.EVENT_NOT_JOINED));
        eventMemberRepository.delete(member);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventMemberResponse> getMembers(long eventId) {
        findEvent(eventId);
        return eventMemberRepository.findByEventIdOrderByJoinedAtAsc(eventId).stream()
                .map(EventMemberResponse::from)
                .toList();
    }

    // BÌNH LUẬN
    @Override
    @Transactional(readOnly = true)
    public Page<CommentEventResponse> getComments(long eventId, Pageable pageable) {
        findEvent(eventId);
        return commentEventRepository.findByEventIdAndActiveTrue(eventId, pageable)
                .map(CommentEventResponse::from);
    }

    @Override
    @Transactional
    public CommentEventResponse addComment(long eventId, CommentEventRequest request) {
        Event event = findEvent(eventId);

        CommentEvent comment = new CommentEvent();
        comment.setEvent(event);
        comment.setUser(getCurrentUser());
        comment.setContent(request.getContent().trim());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setActive(true);
        return CommentEventResponse.from(commentEventRepository.save(comment));
    }

    @Override
    @Transactional
    public void deleteComment(long eventId, long commentId) {
        CommentEvent comment = commentEventRepository.findById(commentId)
                .filter(c -> c.getEvent().getId() == eventId && Boolean.TRUE.equals(c.getActive()))
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
        checkOwnerOrAdmin(comment.getUser());

        // Xóa mềm
        comment.setActive(false);
        commentEventRepository.save(comment);
    }

    // HELPER
    private Event findEvent(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new AppException(ErrorCode.EVENT_NOT_FOUND));
    }

    // Principal name chính là email/SĐT dùng lúc đăng nhập (xem UserServiceImpl.loadUserByUsername) 
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        return userService.findByEmailOrPhone(auth.getName());
    }

    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    // Chỉ người tạo hoặc ADMIN mới được sửa/xóa 
    private void checkOwnerOrAdmin(User owner) {
        if (isAdmin()) {
            return;
        }
        if (owner.getId() != getCurrentUser().getId()) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
    }

    private void validateTime(EventRequest request) {
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new AppException(ErrorCode.EVENT_INVALID_TIME);
        }
    }

    private void applyRequest(Event event, EventRequest request) {
        event.setTitle(request.getTitle().trim());
        event.setContent(request.getContent());
        event.setLocation(request.getLocation());
        event.setMaxAttendees(request.getMaxAttendees());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());
    }

    private boolean hasFiles(List<MultipartFile> files) {
        return files != null && files.stream().anyMatch(f -> f != null && !f.isEmpty());
    }

    // Upload lên Cloudinary rồi lưu DB; nếu lưu DB lỗi thì xóa ảnh vừa upload 
    private List<EventImage> saveImages(Event event, List<MultipartFile> files) {
        List<String> urls = cloudinaryService.uploadImages(files, IMAGE_FOLDER);
        try {
            List<EventImage> images = new ArrayList<>();
            for (String url : urls) {
                EventImage image = new EventImage();
                image.setEvent(event);
                image.setImageUrl(url);
                image.setCreatedAt(LocalDateTime.now());
                images.add(image);
            }
            List<EventImage> saved = eventImageRepository.saveAll(images);
            // Transaction rollback sau đó (vd lỗi ở bước khác) cũng phải dọn ảnh trên Cloudinary
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    if (status == STATUS_ROLLED_BACK) {
                        cloudinaryService.deleteByUrls(urls);
                    }
                }
            });
            return saved;
        } catch (RuntimeException e) {
            cloudinaryService.deleteByUrls(urls);
            throw e;
        }
    }

    // Chỉ xóa ảnh trên Cloudinary khi DB đã commit thành công, tránh mất ảnh khi rollback
    private void deleteFromCloudinaryAfterCommit(List<String> urls) {
        if (urls.isEmpty()) {
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                cloudinaryService.deleteByUrls(urls);
            }
        });
    }

    private EventResponse toResponse(Event event) {
        List<EventImageResponse> images = eventImageRepository.findByEventIdOrderByIdAsc(event.getId()).stream()
                .map(EventImageResponse::from)
                .toList();
        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .content(event.getContent())
                .location(event.getLocation())
                .maxAttendees(event.getMaxAttendees())
                .attendeeCount(eventMemberRepository.countByEventId(event.getId()))
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .status(event.getStatus())
                .createdBy(UserSummaryResponse.from(event.getUser()))
                .images(images)
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }
}
