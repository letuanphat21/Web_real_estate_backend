package com.dat_viet_group.datvietgroup.modules.project.service.serviceimpl;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.dat_viet_group.datvietgroup.core.cloudinary.service.CloudinaryService;
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

    private static final String IMAGE_FOLDER = "projects";

    private final ProjectRepository projectRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional
    public ProjectResponse create(ProjectRequest request, MultipartFile image) {
        Project project = new Project();
        applyRequest(project, request);
        project.setCreatedAt(LocalDateTime.now());

        String newUrl = hasFile(image) ? cloudinaryService.uploadImage(image, IMAGE_FOLDER) : null;
        project.setOverviewImage(newUrl);
        try {
            return toResponse(projectRepository.save(project));
        } catch (RuntimeException e) {
            // Lưu DB lỗi thì xóa ảnh vừa upload để không bị rác trên Cloudinary
            cloudinaryService.deleteByUrl(newUrl);
            throw e;
        }
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
    public ProjectResponse update(Long id, ProjectRequest request, MultipartFile image) {
        Project project = findOrThrow(id);
        applyRequest(project, request);

        // Không gửi ảnh mới thì giữ ảnh cũ
        String oldUrl = project.getOverviewImage();
        String newUrl = hasFile(image) ? cloudinaryService.uploadImage(image, IMAGE_FOLDER) : null;
        if (newUrl != null) {
            project.setOverviewImage(newUrl);
        }
        try {
            ProjectResponse response = toResponse(projectRepository.save(project));
            if (newUrl != null) {
                cloudinaryService.deleteByUrl(oldUrl);
            }
            return response;
        } catch (RuntimeException e) {
            cloudinaryService.deleteByUrl(newUrl);
            throw e;
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Project project = findOrThrow(id);
        projectRepository.delete(project);
        // flush để lỗi ràng buộc khóa ngoại nổ ra trước khi xóa ảnh trên Cloudinary
        projectRepository.flush();
        cloudinaryService.deleteByUrl(project.getOverviewImage());
    }

    private boolean hasFile(MultipartFile file) {
        return file != null && !file.isEmpty();
    }

    private Project findOrThrow(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND, "Không tìm thấy dự án với id: " + id));
    }

    private void applyRequest(Project project, ProjectRequest request) {
        project.setName(request.getName());
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
