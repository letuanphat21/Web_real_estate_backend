package com.dat_viet_group.datvietgroup.modules.project.service.serviceimpl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dat_viet_group.datvietgroup.core.exception.AppException;
import com.dat_viet_group.datvietgroup.core.exception.ErrorCode;
import com.dat_viet_group.datvietgroup.modules.project.dao.ProjectRepository;
import com.dat_viet_group.datvietgroup.modules.project.dao.ZoneRepository;
import com.dat_viet_group.datvietgroup.modules.project.dto.request.ZoneRequest;
import com.dat_viet_group.datvietgroup.modules.project.dto.response.ZoneResponse;
import com.dat_viet_group.datvietgroup.modules.project.entity.Project;
import com.dat_viet_group.datvietgroup.modules.project.entity.Zone;
import com.dat_viet_group.datvietgroup.modules.project.service.ZoneService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ZoneServiceImpl implements ZoneService {

    private final ZoneRepository zoneRepository;
    private final ProjectRepository projectRepository;

    @Override
    @Transactional
    public ZoneResponse create(ZoneRequest request) {
        Zone zone = new Zone();
        applyRequest(zone, request);
        return toResponse(zoneRepository.save(zone));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ZoneResponse> getAll(Long projectId, Pageable pageable) {
        Page<Zone> zones = projectId == null
                ? zoneRepository.findAll(pageable)
                : zoneRepository.findByProjectId(projectId, pageable);
        return zones.map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ZoneResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    @Transactional
    public ZoneResponse update(Long id, ZoneRequest request) {
        Zone zone = findOrThrow(id);
        applyRequest(zone, request);
        return toResponse(zoneRepository.save(zone));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        zoneRepository.delete(findOrThrow(id));
    }

    private Zone findOrThrow(Long id) {
        return zoneRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ZONE_NOT_FOUND, "Không tìm thấy phân khu với id: " + id));
    }

    private void applyRequest(Zone zone, ZoneRequest request) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND,
                        "Không tìm thấy dự án với id: " + request.getProjectId()));
        zone.setProject(project);
        zone.setName(request.getName());
        zone.setDescription(request.getDescription());
        zone.setStatus(request.getStatus());
        zone.setImageUrl(request.getImageUrl());
    }

    private ZoneResponse toResponse(Zone zone) {
        return new ZoneResponse(
                zone.getId(),
                zone.getProject().getId(),
                zone.getName(),
                zone.getDescription(),
                zone.getStatus(),
                zone.getImageUrl());
    }
}
