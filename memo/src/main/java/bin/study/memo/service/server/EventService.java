package bin.study.memo.service.server;

import bin.study.memo.domain.*;
import bin.study.memo.handler.DBhandler;
import bin.study.memo.repository.event.EventInfoMongodbRepository;
import bin.study.memo.repository.event.EventRecordMongodbRepository;
import bin.study.memo.repository.event.TotalEvent1MongodbRepository;
import bin.study.memo.repository.info.UserMongodbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class EventService {

        @Autowired
        private EventInfoMongodbRepository eventInfoMongodbRepository;
        @Autowired
        private EventRecordMongodbRepository eventRecordMongodbRepository;
        @Autowired
        private TotalEvent1MongodbRepository totalEvent1MongodbRepository;
        @Autowired
        private UserService userService;
        @Autowired
        private  UserMongodbRepository userMongodbRepository;
        @Autowired
        private MongoTemplate mongoTemplate;
        @Autowired
        private MongoOperations mongoOperations;
        @Autowired
        private DBhandler dbHandler;


        @Async
        public void insertReviewEvent(Reviews review) {
                //0. 현재 타입을 통해 가능한 이벤트 목록을 불러온다.
                List<String> attend_typeList = new ArrayList<String>();
                attend_typeList.add("리뷰");
              List<EventInfo> eventList = eventInfoMongodbRepository.findByEventAttendTypeIn(attend_typeList);
                //List<EventInfo> eventList = new ArrayList<EventInfo>();
                // 1 . event record에 기록한다.
                if (eventList.size() != 0){
                        for (EventInfo event : eventList) {
                                TotalEvent1 totalevent1 = totalEvent1MongodbRepository.findByEmail(review.getUsername());
                                insertEventRecord(review,event);
                                if(totalevent1 == null){
                                        //2.  totalevent에 사용자 정보가 없으면 정보를 입력해 준다.
                                        insertTotalEvent1(review,event);

                                }else{
                                        // 3. totablenvt에 사용자 정보가 있다면 업데이트를 해준다.
                                        updateTotalEvent1(review,totalevent1);
                                }
                        }
                }

        }

        public void insertTotalEvent1(Reviews review, EventInfo event) {
                int story_stamp = 0;
                int active_stamp = 0;
                if (review.getType().equals("스토리형")){
                         story_stamp = 1;
                }else if(review.getType().equals("활동형")){
                        active_stamp = 1;
                }
                TotalEvent1 totalEvent1 = TotalEvent1.builder()
                                                                .eid(event.getEid())
                                                                .tid(dbHandler.generateSequence(TotalEvent1.SEQUENCE_NAME))
                                                                .email(review.getUsername())
                                                                .request_event_num(0)
                                                                .request_event_present(new ArrayList<Integer>())
                                                                .unused_active_stamp(active_stamp)
                                                                .unused_story_stamp(story_stamp)
                                                                .used_active_stamp(0)
                                                                .used_story_stamp(0)
                                                                .build();
                totalEvent1MongodbRepository.save(totalEvent1);
        }

        public void updateTotalEvent1(Reviews review, TotalEvent1 event) {
                if (review.getType().equals("스토리형")){
                        if(event.getUnused_story_stamp()+event.getUsed_story_stamp() >= 5){
                                event.setUnused_story_stamp(event.getUnused_story_stamp());
                        }else{
                                event.setUnused_story_stamp(event.getUnused_story_stamp()+1);
                        }
                }else if(review.getType().equals("활동형")){
                        if(event.getUnused_active_stamp()+event.getUsed_active_stamp() >= 5){
                                event.setUnused_active_stamp(event.getUsed_active_stamp());
                        }else{
                                event.setUnused_active_stamp(event.getUsed_active_stamp()+1);
                        }
                }
                try {
                        Query query = new Query();
                        Update update = new Update();
                        // where절 조건
                        query.addCriteria(Criteria.where("tid").is(event.getTid()));
                        update.set("unused_active_stamp", event.getUnused_active_stamp());
                        update.set("unused_story_stamp",event.getUnused_story_stamp());
                        mongoTemplate.updateMulti(query, update, "TotalEvent1");
                } catch (Exception e) {
                        e.getMessage();
                }
        }

        public void insertEventRecord(Reviews review, EventInfo event) {
                EventRecord record = new EventRecord();
                record.setAttendDate(review.getCreate_date());
                record.setAttentEventType(review.getType());
                record.setEmail(review.getUsername());
                record.setEid(event.getEid());
                record.setUsedPointType(1);
                record.setErid(dbHandler.generateSequence(EventRecord.SEQUENCE_NAME));
                eventRecordMongodbRepository.save(record);
        }
        public void useEventRecord(String email,Long eid) {
        //사용했다는 기록 남기기
                SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");
                String attend_date = date.format(new Date());
                EventRecord record = new EventRecord();
                record.setAttendDate(attend_date);
                record.setEmail(email);
                record.setEid(eid);
                record.setUsedPointType(2);
                record.setErid(dbHandler.generateSequence(EventRecord.SEQUENCE_NAME));
                eventRecordMongodbRepository.save(record);
        }


        public TotalEvent1 getUserEventInfo(Long eid, String email){
                Query query = new Query();
                query.addCriteria(
                        new Criteria().andOperator(
                                Criteria.where("email").is(email),
                                Criteria.where("eid").is(eid)
                        )
                );
                TotalEvent1 totalEvent1 = mongoTemplate.findOne(query, TotalEvent1.class, "TotalEvent1");
                return totalEvent1;
        }

        public User updatePoint(Long eid, TotalEvent1 totalEvent1, int point, int request_event_num, String email) {
                //포인트 주기
                userService.userUpdateParticipation(email,"point",point);
                User user = userMongodbRepository.findByEmail(email);
                return user;
        }

        public EventInfo getEventInfo(Long eid) {
                EventInfo eventInfo = eventInfoMongodbRepository.findByEid(eid);
                return eventInfo;
        }

        public void updateTotalEventFirst(TotalEvent1 totalEvent1,String tel) {
                List<Integer> a = totalEvent1.getRequest_event_present();
                a.add(1);

                if(totalEvent1.getUnused_active_stamp() >=5){
                        totalEvent1.setUnused_active_stamp(totalEvent1.getUnused_active_stamp()-5);
                        totalEvent1.setUsed_active_stamp(totalEvent1.getUsed_active_stamp()+5);
                        totalEvent1.setUnused_story_stamp(totalEvent1.getUnused_story_stamp()-1);
                        totalEvent1.setUsed_story_stamp(totalEvent1.getUsed_story_stamp()+1);
                }
                else if (totalEvent1.getUnused_story_stamp() >=5 ){
                        totalEvent1.setUnused_story_stamp(totalEvent1.getUnused_story_stamp()-5);
                        totalEvent1.setUsed_story_stamp(totalEvent1.getUsed_story_stamp()+5);
                        totalEvent1.setUnused_active_stamp(totalEvent1.getUnused_active_stamp()-1);
                        totalEvent1.setUsed_active_stamp(totalEvent1.getUsed_active_stamp()+1);
                }else{
                        if(totalEvent1.getUnused_active_stamp() > totalEvent1.getUnused_story_stamp()){
                                int stamp = 6 - totalEvent1.getUnused_active_stamp();
                                totalEvent1.setUsed_active_stamp(totalEvent1.getUsed_active_stamp()+  totalEvent1.getUnused_active_stamp());
                                totalEvent1.setUnused_active_stamp(0);
                                totalEvent1.setUnused_story_stamp(totalEvent1.getUnused_story_stamp()-stamp);
                                totalEvent1.setUsed_story_stamp(totalEvent1.getUsed_story_stamp()+stamp);
                        }else{
                                int stamp = 6 - totalEvent1.getUnused_story_stamp();
                                totalEvent1.setUsed_story_stamp(totalEvent1.getUsed_story_stamp()+  totalEvent1.getUnused_story_stamp());
                                totalEvent1.setUnused_story_stamp(0);
                                totalEvent1.setUnused_active_stamp(totalEvent1.getUnused_active_stamp()-stamp);
                                totalEvent1.setUsed_active_stamp(totalEvent1.getUsed_active_stamp()+stamp);
                        }
                }

                try {
                        Query query = new Query();
                        Update update = new Update();
                        totalEvent1.setRequest_event_present(a);
                        // where절 조건
                        query.addCriteria(Criteria.where("tid").is(totalEvent1.getTid()));
                        update.set("unused_active_stamp", totalEvent1.getUnused_active_stamp());
                        update.set("unused_story_stamp",totalEvent1.getUnused_story_stamp());
                        update.set("used_active_stamp", totalEvent1.getUsed_active_stamp());
                        update.set("used_story_stamp",totalEvent1.getUsed_story_stamp());
                        update.set("request_event_present",totalEvent1.getRequest_event_present());
                        update.set("tel",tel);
                        mongoTemplate.updateMulti(query, update, "TotalEvent1");
                } catch (Exception e) {
                        e.getMessage();
                }
        }

        public void updateTotalEventSecond(TotalEvent1 totalEvent1) {
                List<Integer> a = totalEvent1.getRequest_event_present();
                a.add(2);
                try {
                        Query query = new Query();
                        Update update = new Update();
                        totalEvent1.setRequest_event_present(a);
                        // where절 조건
                        query.addCriteria(Criteria.where("tid").is(totalEvent1.getTid()));
                        update.set("unused_active_stamp", 0);
                        update.set("unused_story_stamp",0);
                        update.set("used_active_stamp",5);
                        update.set("used_story_stamp",0);
                        update.set("request_event_present",totalEvent1.getRequest_event_present());
                        mongoTemplate.updateMulti(query, update, "TotalEvent1");
                } catch (Exception e) {
                        e.getMessage();
                }

        }
}
