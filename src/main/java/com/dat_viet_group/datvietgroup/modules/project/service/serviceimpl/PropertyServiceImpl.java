package com.dat_viet_group.datvietgroup.modules.project.service.serviceimpl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dat_viet_group.datvietgroup.core.exception.AppException;
import com.dat_viet_group.datvietgroup.core.exception.ErrorCode;
import com.dat_viet_group.datvietgroup.modules.project.dao.PropertyRepository;
import com.dat_viet_group.datvietgroup.modules.project.dao.ZoneRepository;
import com.dat_viet_group.datvietgroup.modules.project.dto.request.PropertyRequest;
import com.dat_viet_group.datvietgroup.modules.project.dto.response.PropertyResponse;
import com.dat_viet_group.datvietgroup.modules.project.entity.Property;
import com.dat_viet_group.datvietgroup.modules.project.entity.Zone;
import com.dat_viet_group.datvietgroup.modules.project.enums.PropertyStatus;
import com.dat_viet_group.datvietgroup.modules.project.service.PropertyService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final ZoneRepository zoneRepository;

    @Override
    @Transactional
    public PropertyResponse create(PropertyRequest request) {
        if (propertyRepository.existsByZoneIdAndPropertyCode(request.getZoneId(), request.getPropertyCode())) {
            throw new AppException(ErrorCode.PROPERTY_CODE_EXISTED);
        }
        Property property = new Property();
        applyRequest(property, request);
        return toResponse(propertyRepository.save(property));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PropertyResponse> getAll(Long zoneId, PropertyStatus status, Pageable pageable) {
        Specification<Property> spec = (root, query, cb) -> cb.conjunction();
        if (zoneId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("zone").get("id"), zoneId));
        }
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        return propertyRepository.findAll(spec, pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PropertyResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    @Transactional
    public PropertyResponse update(Long id, PropertyRequest request) {
        Property property = findOrThrow(id);
        if (propertyRepository.existsByZoneIdAndPropertyCodeAndIdNot(request.getZoneId(), request.getPropertyCode(), id)) {
            throw new AppException(ErrorCode.PROPERTY_CODE_EXISTED);
        }
        applyRequest(property, request);
        return toResponse(propertyRepository.save(property));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        propertyRepository.delete(findOrThrow(id));
        // flush để lỗi ràng buộc khóa ngoại (còn ảnh, booking...) nổ ra ngay trong request này
        propertyRepository.flush();
    }

    private Property findOrThrow(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PROPERTY_NOT_FOUND,
                        "Không tìm thấy bất động sản với id: " + id));
    }

    private void applyRequest(Property property, PropertyRequest request) {
        Zone zone = zoneRepository.findById(request.getZoneId())
                .orElseThrow(() -> new AppException(ErrorCode.ZONE_NOT_FOUND,
                        "Không tìm thấy phân khu với id: " + request.getZoneId()));
        property.setZone(zone);
        property.setPropertyCode(request.getPropertyCode());
        property.setArea(request.getArea());
        property.setPrice(request.getPrice());
        property.setDirection(request.getDirection());
        property.setFloor(request.getFloor());
        property.setStatus(request.getStatus());
        property.setBedrooms(request.getBedrooms());
    }

    private PropertyResponse toResponse(Property property) {
        return new PropertyResponse(
                property.getId(),
                property.getZone().getId(),
                property.getPropertyCode(),
                property.getArea(),
                property.getPrice(),
                property.getDirection(),
                property.getFloor(),
                property.getStatus(),
                property.getBedrooms());
    }
}
