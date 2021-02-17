package bin.study.memo.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@NoArgsConstructor
@Document("eventinfo")
public class EventInfo {
    @Transient
    public static final String SEQUENCE_NAME = "event_info_sequence";
    @Id
    @Field("_id")
    private ObjectId id;

    //event id
    private Long eid;
    //event 제목
    private String EventName;
    //활동주체
    private String EventEndDate;
    //회사 링크
    private String EventStartDate;
    //이벤트 유형
    private String EventType;
    //진행중
    private int EventEnd = 1;

    private String eventAttendType;

    private List<Integer> EventPoint;
    @Builder
    public EventInfo(ObjectId id, Long eid, String evet_name, String event_end_date, String event_start_date, String event_type, int event_end, String event_attend_type, List<Integer> event_point) {
        this.id = id;
        this.eid = eid;
        this.EventName= evet_name;
        this.EventEndDate = event_end_date;
        this.EventStartDate = event_start_date;
        this.EventType = event_type;
        this.EventEnd = event_end;
        this.eventAttendType = event_attend_type;
        this.EventPoint = event_point;
    }
}
