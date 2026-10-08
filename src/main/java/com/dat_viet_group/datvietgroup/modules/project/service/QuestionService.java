package com.dat_viet_group.datvietgroup.modules.project.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dat_viet_group.datvietgroup.modules.project.dto.request.QuestionRequest;
import com.dat_viet_group.datvietgroup.modules.project.dto.response.QuestionResponse;

public interface QuestionService {

    QuestionResponse create(QuestionRequest request);

    /** projectId = null thì lấy tất cả câu hỏi */
    Page<QuestionResponse> getAll(Long projectId, Pageable pageable);

    QuestionResponse getById(Long id);

    QuestionResponse update(Long id, QuestionRequest request);

    void delete(Long id);
}
