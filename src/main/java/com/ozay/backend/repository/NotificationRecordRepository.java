package com.ozay.backend.repository;

import com.ozay.backend.model.*;
import com.ozay.backend.resultsetextractor.NotificationRecordResultSetExtractor;
import com.ozay.backend.resultsetextractor.NotificationTrackResultSetExtractor;
import com.ozay.backend.service.MailService;
import org.joda.time.DateTime;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Created by naofumiezaki on 11/24/15.
 */
@Repository
public class NotificationRecordRepository {
    @Inject
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Inject
    MailService mailService;


    public List<NotificationRecord> findAllByNotificationId(Long notificationId){

        String query = "SELECT nr.*, m.first_name, m.last_name, m.unit, n.track FROM notification_record nr INNER JOIN member m ON nr.member_id = m.id INNER JOIN notification n ON nr.notification_id = n.id WHERE nr.notification_id = :notificationId ORDER BY n.created_date DESC";

        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("notificationId", notificationId);

        return (List<NotificationRecord>)namedParameterJdbcTemplate.query(query, params, new NotificationRecordResultSetExtractor());
    }

/*    // Used for notification track
    public List<NotificationTrack> findAllByBuildingId(Long buildingId, Long offset){
        int limit = 20;
        offset = offset * limit;
        String query = "SELECT n.created_date, nr.*, m.first_name, m.last_name, m.unit, n.track FROM notification_record nr INNER JOIN notification n ON nr.notification_id = n.id AND n.track = true INNER JOIN member m ON nr.member_id = m.id ORDER BY n.created_date DESC LIMIT :limit OFFSET :offset";

        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("buildingId", buildingId);
        params.addValue("limit", limit);
        params.addValue("offset", offset);

        return (List<NotificationTrack>)namedParameterJdbcTemplate.query(query, params, new NotificationTrackResultSetExtractor());
    }
 */
 //Old Notification Track using Notification.java and NotificationRecordResultSetExtractor
    public List<NotificationRecord> findAllTrackedByBuildingId(Long buildingId, Long offset, String search){
        int limit = 10;
        offset = offset * limit;
        MapSqlParameterSource params = new MapSqlParameterSource();
        String partialQuery = "";
        if(search != null){
            params.addValue("unit", search);
            partialQuery = " AND (lower(m.unit)=lower(:unit) or lower(m.first_name) = lower(:unit) or lower(m.last_name) = lower(:unit))  ";
        }
        String query = "SELECT n.created_date, n.subject, nr.*, m.first_name, m.last_name, m.unit, n.track FROM notification_record nr INNER JOIN notification n ON nr.notification_id = n.id AND n.track = true INNER JOIN member m ON nr.member_id = m.id " + partialQuery + " WHERE n.building_id = :buildingId ORDER BY nr.track_complete, n.created_date DESC LIMIT :limit OFFSET :offset";


        params.addValue("buildingId", buildingId);
        params.addValue("limit", limit);
        params.addValue("offset", offset);

        return (List<NotificationRecord>)namedParameterJdbcTemplate.query(query, params, new NotificationTrackResultSetExtractor());
    }





    public Long countAllByNotificationId(Long buildingId, String search){
        String partialQuery = "";
        MapSqlParameterSource params = new MapSqlParameterSource();
        if(search != null){
            params.addValue("unit", search);
            partialQuery = " AND m.unit=:unit ";
        }
        String query = "SELECT COUNT(*) FROM notification_record nr INNER JOIN notification n ON nr.notification_id = n.id AND n.track = true INNER JOIN member m ON nr.member_id = m.id " + partialQuery + " WHERE n.building_id = :buildingId";

        params.addValue("buildingId", buildingId);

        return namedParameterJdbcTemplate.queryForObject(query, params, Long.class);
    }

    public void create(NotificationRecord notificationRecord){
        String query = "INSERT INTO notification_record (notification_id, member_id, success, email, note) VALUES(:notificationId, :memberId, :success, :email, :note)";
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("notificationId", notificationRecord.getNotificationId());
        params.addValue("memberId", notificationRecord.getMemberId());
        params.addValue("success", notificationRecord.isSuccess());
        params.addValue("note", notificationRecord.getNote());
        params.addValue("email", notificationRecord.getEmail());

        namedParameterJdbcTemplate.update(query, params);
    }

    public void update(NotificationRecord notificationRecord){
        String query = "UPDATE notification_record SET track_complete=:trackComplete, track_completed_date=(select now()) WHERE notification_id=:notificationId AND member_id=:memberId";
        MapSqlParameterSource params = new MapSqlParameterSource();

        if(notificationRecord.isTrackComplete() == true){
            notificationRecord.setTrackCompletedDate(new DateTime());
            params.addValue("trackCompletedDate", new Timestamp(notificationRecord.getTrackCompletedDate().getMillisOfSecond()));

        } else {
            notificationRecord.setTrackCompletedDate(null);
            params.addValue("trackCompletedDate", null);
        }

        params.addValue("notificationId", notificationRecord.getNotificationId());

        params.addValue("memberId", notificationRecord.getMemberId());
        params.addValue("trackComplete", notificationRecord.isTrackComplete());

        namedParameterJdbcTemplate.update(query, params);
    }


}
