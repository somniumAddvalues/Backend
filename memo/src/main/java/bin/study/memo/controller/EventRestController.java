package bin.study.memo.controller;

import bin.study.memo.domain.EventInfo;
import bin.study.memo.domain.TotalEvent1;
import bin.study.memo.domain.User;
import bin.study.memo.service.server.EventService;
import bin.study.memo.utils.CookieUtil;
import bin.study.memo.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController("/events")
public class EventRestController {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CookieUtil cookieUtil;
    @Autowired
    private EventService eventService;

    @GetMapping("/{eid}")
    public TotalEvent1 getUserEventInfo(@PathVariable Long eid, HttpServletRequest req){
        String refresh_token = cookieUtil.getCookie(req, "refresh_token").getValue();
        String email = jwtUtil.getEmail(refresh_token);
        TotalEvent1 totalEvent1 = eventService.getUserEventInfo(eid,email);
        return totalEvent1;
    }

    @PostMapping("/{eid}")
    public User participantsEvent(Long eid, HttpServletRequest req, Map<String,Object> data){
        String refresh_token = cookieUtil.getCookie(req, "refresh_token").getValue();
        String email = jwtUtil.getEmail(refresh_token);
        TotalEvent1 totalEvent1 = eventService.getUserEventInfo(eid,email);
        EventInfo eventInfo = eventService.getEventInfo(eid);

        int request_event_num = (int) data.get("request_event_num");
        int point = eventInfo.getEvent_point().get(request_event_num);
        String tel = (String)data.get("user_info");

        User user = eventService.updatePoint(eid ,totalEvent1, point, request_event_num,email);
        eventService.useEventRecord(email,eid);
        eventService.updateTotalEvent2(totalEvent1,request_event_num,tel);
        return user;
    }
}
