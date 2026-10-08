package com.dat_viet_group.datvietgroup.modules.notification.dto.response;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnreadCountResponse {

    // Tổng số chưa đọc, hiện trên chuông
    private long total;

    // Số chưa đọc theo từng loại: key là notificationTypeId, hiện trên từng tab
    private Map<Long, Long> byType;
}
