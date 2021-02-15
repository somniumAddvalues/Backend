package bin.study.memo.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@Document("TotalEvent1")
public class TotalEvent1 {
    @Transient
    public static final String SEQUENCE_NAME = "total_event1_sequence";
    //event id
    private Long eid;
    //이메일(사용자명)
    private String email;
    private Long tid;

    //이벤트 신청 횟수(보상)
    private int request_event_num;
    //비 사용 스탬프 관리
    private int unused_active_stamp;
    private int unused_story_stamp;
    //사용 스탬프 관리
    private int used_active_stamp;
    private int used_story_stamp;
    //바뀔 것으로 보임 -> 이벤트 신청자 정보
    private String user_info;
    private List<Integer> request_event_present;

    @Builder

    public TotalEvent1(Long eid, String email, Long tid, int request_event_num, int unused_active_stamp, int unused_story_stamp, int used_active_stamp, int used_story_stamp, String tel, List<Integer> request_event_present) {
        this.eid = eid;
        this.email = email;
        this.tid = tid;
        this.request_event_num = request_event_num;
        this.unused_active_stamp = unused_active_stamp;
        this.unused_story_stamp = unused_story_stamp;
        this.used_active_stamp = used_active_stamp;
        this.used_story_stamp = used_story_stamp;
        this.user_info = tel;
        this.request_event_present = request_event_present;
    }
}
