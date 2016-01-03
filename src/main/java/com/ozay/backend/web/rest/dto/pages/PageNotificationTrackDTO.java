package com.ozay.backend.web.rest.dto.pages;

import com.ozay.backend.model.NotificationRecord;

import java.util.List;

/**
 * Created by naofumiezaki on 12/20/15.
 */
public class PageNotificationTrackDTO {
    private List<NotificationRecord> notificationRecords;
    private Long numberOfRecords;

    public List<NotificationRecord> getNotificationRecords() {
        return notificationRecords;
    }

    public void setNotificationRecords(List<NotificationRecord> notificationRecords) {
        this.notificationRecords = notificationRecords;
    }

    public Long getNumberOfRecords() {
        return numberOfRecords;
    }

    public void setNumberOfRecords(Long numberOfRecords) {
        this.numberOfRecords = numberOfRecords;
    }
}
