package vietnamworks.com.pal.entities;

import vietnamworks.com.pal.utils.Common;

/**
 * Created by duynk on 10/1/15.
 */
public class Post extends BaseEntity {

    public final static int STATUS_NONE                     = 0;                                //0
    public final static int STATUS_USER_PENDING             = STATUS_NONE + 1;                  //1
    public final static int STATUS_USER_ERROR               = STATUS_USER_PENDING + 1;          //2
    public final static int STATUS_READY                    = STATUS_USER_ERROR + 1;            //3
    public final static int STATUS_ADVISOR_PROCESSING       = STATUS_READY + 1;                 //4
    public final static int STATUS_ADVISOR_EVALUATED        = STATUS_ADVISOR_PROCESSING + 1;    //5
    public final static int STATUS_USER_CONVERSATION        = STATUS_ADVISOR_EVALUATED + 1;     //6
    public final static int STATUS_ADVISOR_CONVERSATION     = STATUS_USER_CONVERSATION + 1;     //7
    public final static int STATUS_CLOSED_BY_USER           = STATUS_ADVISOR_CONVERSATION + 1;  //8
    public final static int STATUS_CLOSED_AND_REDO          = STATUS_CLOSED_BY_USER + 1;        //9

    //core
    String title = "";
    String ref_topic = "";
    int status = STATUS_NONE;
    String audio = "";
    String text = "";
    String index_user_status = "";
    String index_user_type = "";
    int type = RecentTopic.TYPE_WRITING;

    //evaluate
    int score = 0;
    int satisfy_score = 0;
    String advisor_id = "";

    //for re-post
    String prev = "";
    String next = "";

    public Post() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRef_topic() {
        return ref_topic;
    }

    public void setRef_topic(String ref_topic) {
        this.ref_topic = ref_topic;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.index_user_status = buildUserStatusIndex(this.getCreated_by(), status);
        this.status = status;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getSatisfy_score() {
        return satisfy_score;
    }

    public void setSatisfy_score(int satisfy_score) {
        this.satisfy_score = satisfy_score;
    }

    public String getAdvisor_id() {
        return advisor_id;
    }

    public void setAdvisor_id(String advisor_id) {
        this.advisor_id = advisor_id;
    }

    public String getPrev() {
        return prev;
    }

    public void setPrev(String prev) {
        this.prev = prev;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.index_user_type = buildUserTypeIndex(this.getCreated_by(), type);
        this.type = type;
    }

    public String getIndex_user_status() {
        return index_user_status;
    }

    public void setIndex_user_status(String channel_status) {
        this.index_user_status = index_user_status;
    }

    public String getIndex_user_type() {
        return index_user_type;
    }

    public void setIndex_user_type(String index_user_type) {
        this.index_user_type = index_user_type;
    }

    public static String buildUserTypeIndex(String userid, int type) {
        return Common.padRight(userid, 48) + Common.padRight(type + "", 4) ;
    }
    public static String buildUserStatusIndex(String userid, int status) {
        return Common.padRight(userid, 48) + Common.padRight(status + "", 4) ;
    }

}
