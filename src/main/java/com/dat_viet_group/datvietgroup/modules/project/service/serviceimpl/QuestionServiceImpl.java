package com.dat_viet_group.datvietgroup.modules.project.service.serviceimpl;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dat_viet_group.datvietgroup.core.exception.AppException;
import com.dat_viet_group.datvietgroup.core.exception.ErrorCode;
import com.dat_viet_group.datvietgroup.modules.project.dao.ProjectRepository;
import com.dat_viet_group.datvietgroup.modules.project.dao.QuestionRepository;
import com.dat_viet_group.datvietgroup.modules.project.dto.request.QuestionRequest;
import com.dat_viet_group.datvietgroup.modules.project.dto.response.QuestionResponse;
import com.dat_viet_group.datvietgroup.modules.project.entity.Project;
import com.dat_viet_group.datvietgroup.modules.project.entity.Question;
import com.dat_viet_group.datvietgroup.modules.project.service.QuestionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final ProjectRepository projectRepository;

    @Override
    @Transactional
    public QuestionResponse create(QuestionRequest request) {
        Question question = new Question();
        applyRequest(question, request);
        question.setCreatedAt(LocalDateTime.now());
        return toResponse(questionRepository.save(question));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionResponse> getAll(Long projectId, Pageable pageable) {
        Page<Question> questions = projectId == null
                ? questionRepository.findAll(pageable)
                : questionRepository.findByProjectId(projectId, pageable);
        return questions.map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    @Transactional
    public QuestionResponse update(Long id, QuestionRequest request) {
        Question question = findOrThrow(id);
        applyRequest(question, request);
        return toResponse(questionRepository.save(question));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        questionRepository.delete(findOrThrow(id));
    }

    private Question findOrThrow(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND,
                        "Không tìm thấy câu hỏi với id: " + id));
    }

    private void applyRequest(Question question, QuestionRequest request) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND,
                        "Không tìm thấy dự án với id: " + request.getProjectId()));
        question.setProject(project);
        question.setQuestion(request.getQuestion());
        question.setAnswer(request.getAnswer());
    }

    private QuestionResponse toResponse(Question question) {
        return new QuestionResponse(
                question.getId(),
                question.getProject().getId(),
                question.getQuestion(),
                question.getAnswer(),
                question.getCreatedAt());
    }
}
