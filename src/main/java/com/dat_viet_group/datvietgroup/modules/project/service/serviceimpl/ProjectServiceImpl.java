package com.dat_viet_group.datvietgroup.modules.project.service.serviceimpl;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dat_viet_group.datvietgroup.core.exception.AppException;
import com.dat_viet_group.datvietgroup.core.exception.ErrorCode;
import com.dat_viet_group.datvietgroup.modules.project.dao.ProjectRepository;
import com.dat_viet_group.datvietgroup.modules.project.dto.request.ProjectRequest;
import com.dat_viet_group.datvietgroup.modules.project.dto.response.ProjectResponse;
import com.dat_viet_group.datvietgroup.modules.project.entity.Project;
import com.dat_viet_group.datvietgroup.modules.project.service.ProjectService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    @Override
    @Transactional
    public ProjectResponse create(ProjectRequest request) {
        Project project = new Project();
        applyRequest(project, request);
        project.setCreatedAt(LocalDateTime.now());
        return toResponse(projectRepository.save(project));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectResponse> getAll(Pageable pageable) {
        return projectRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    @Transactional
    public ProjectResponse update(Long id, ProjectRequest request) {
        Project project = findOrThrow(id);
        applyRequest(project, request);
        return toResponse(projectRepository.save(project));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        projectRepository.delete(findOrThrow(id));
    }

    private Project findOrThrow(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND, "Không tìm thấy dự án với id: " + id));
    }

    private void applyRequest(Project project, ProjectRequest request) {
        project.setName(request.getName());
        project.setOverviewImage(request.getOverviewImage());
        project.setLocation(request.getLocation());
        project.setInvestor(request.getInvestor());
        project.setConsultancy(request.getConsultancy());
        project.setDevelopmentModel(request.getDevelopmentModel());
        project.setBuildingType(request.getBuildingType());
        project.setSize(request.getSize());
        project.setTotalInvestment(request.getTotalInvestment());
        project.setOwnershipType(request.getOwnershipType());
    }

    private ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getOverviewImage(),
                project.getLocation(),
                project.getInvestor(),
                project.getConsultancy(),
                project.getDevelopmentModel(),
                project.getBuildingType(),
                project.getSize(),
                project.getTotalInvestment(),
                project.getOwnershipType(),
                project.getCreatedAt());
    }
}
