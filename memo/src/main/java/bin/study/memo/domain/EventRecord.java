package bin.study.memo.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@Document("eventrecord")
public class EventRecord {
    @Transient
    public static final String SEQUENCE_NAME = "event_recode_sequence";
    @Id
    @Field("_id")
    private ObjectId id;

    //event record id
    private Long erid;
    //event id
    private Long eid;
    //이메일(사용자명)
    private String email;
    //활동주체
    private String AttendDate;
    //활동 스토리(후기, 응원) 판단하는 타입
    private String AttentEventType;
    //사용, 적립 확
    private int UsedPointType;

    @Builder

    public EventRecord(ObjectId id, Long erid, Long eid, String email, String attend_date, String attent_event_type, int used_point_type) {
        this.id = id;
        this.erid = erid;
        this.eid = eid;
        this.email = email;
        this.AttendDate = attend_date;
        this.AttentEventType = attent_event_type;
        this.UsedPointType = used_point_type;
    }
}
