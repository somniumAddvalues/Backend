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
    private String evet_name;
    //활동주체
    private String event_end_date;
    //회사 링크
    private String event_start_date;
    //이벤트 유형
    private String event_type;
    //진행중
    private int event_end = 1;

    private List<String> event_attend_type;

    private List<Integer> event_point;
    @Builder
    public EventInfo(ObjectId id, Long eid, String evet_name, String event_end_date, String event_start_date, String event_type, int event_end, List<String> event_attend_type, List<Integer> event_point) {
        this.id = id;
        this.eid = eid;
        this.evet_name = evet_name;
        this.event_end_date = event_end_date;
        this.event_start_date = event_start_date;
        this.event_type = event_type;
        this.event_end = event_end;
        this.event_attend_type = event_attend_type;
        this.event_point = event_point;
    }
}
