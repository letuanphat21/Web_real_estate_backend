package com.dat_viet_group.datvietgroup.core.common;

import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Dữ liệu phân trang trả về trong trường "data" của ApiResponse.
 * Dùng thay cho Page<T> của Spring để JSON ổn định, không lộ cấu trúc nội bộ.
 *
 * JSON: { "content": [...], "page": 0, "size": 10, "totalElements": 25, "totalPages": 3 }
 *
 * Cách dùng:
 *   Page<Job> page = jobRepository.findAll(spec, pageable);
 *   return PageResponse.from(page, this::toResponse);
 */
@Getter
@AllArgsConstructor
public class PageResponse<T> {

    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;

    /** Chuyển Page<E> (entity) sang PageResponse<T> (DTO) bằng hàm mapper. */
    public static <E, T> PageResponse<T> from(Page<E> page, Function<E, T> mapper) {
        return new PageResponse<>(
                page.getContent().stream().map(mapper).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}