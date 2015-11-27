package vietnamworks.com.pal.entities;

import android.content.Context;

import com.firebase.client.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.common.Utils;

/**
 * Created by duynk on 10/1/15.
 */
public class Post extends BaseEntity {

    public final static int STATUS_NONE                     = 0;                                //0
    public final static int STATUS_USER_PENDING             = STATUS_NONE + 1;                  //1
    public final static int STATUS_USER_ERROR               = STATUS_USER_PENDING + 1;          //2
    public final static int STATUS_SYNC                     = STATUS_USER_ERROR + 1;            //3
    public final static int STATUS_READY                    = STATUS_SYNC + 1;                  //4
    public final static int STATUS_ADVISOR_PROCESSING       = STATUS_READY + 1;                 //5
    public final static int STATUS_ADVISOR_EVALUATED        = STATUS_ADVISOR_PROCESSING + 1;    //6
    public final static int STATUS_USER_CONVERSATION        = STATUS_ADVISOR_EVALUATED + 1;     //7
    public final static int STATUS_ADVISOR_CONVERSATION     = STATUS_USER_CONVERSATION + 1;     //8
    public final static int STATUS_CLOSED_BY_USER           = STATUS_ADVISOR_CONVERSATION + 1;  //9
    public final static int STATUS_CLOSED_AND_REDO          = STATUS_CLOSED_BY_USER + 1;        //10

    public static String[] STATUS_TEXT ;

    //core
    String title = "";
    String ref_topic = "";
    int status = STATUS_NONE;
    String audio = "";
    String text = "";
    boolean has_read = true;
    long user_last_requested = 0;
    Object conversation;

    //evaluate
    int score = 0;
    String advisor_id = "";

    //for re-post
    String prev = "";
    String next = "";

    public static void init(Context ctx) {
        STATUS_TEXT = new String[STATUS_CLOSED_AND_REDO + 1];
        STATUS_TEXT[STATUS_NONE] = ctx.getString(R.string.post_status_none);
        STATUS_TEXT[STATUS_USER_PENDING] = ctx.getString(R.string.post_status_pending);
        STATUS_TEXT[STATUS_USER_ERROR] = ctx.getString(R.string.post_status_error);
        STATUS_TEXT[STATUS_SYNC] = ctx.getString(R.string.post_status_sync);
        STATUS_TEXT[STATUS_READY] = ctx.getString(R.string.post_status_ready);
        STATUS_TEXT[STATUS_ADVISOR_PROCESSING] = ctx.getString(R.string.post_status_processing);
        STATUS_TEXT[STATUS_ADVISOR_EVALUATED] = ctx.getString(R.string.post_status_evaluated);
        STATUS_TEXT[STATUS_USER_CONVERSATION] = ctx.getString(R.string.post_status_user_conversation);
        STATUS_TEXT[STATUS_ADVISOR_CONVERSATION] = ctx.getString(R.string.post_status_advisor_conversation);
        STATUS_TEXT[STATUS_CLOSED_BY_USER] = ctx.getString(R.string.post_status_user_closed);
        STATUS_TEXT[STATUS_CLOSED_AND_REDO] = ctx.getString(R.string.post_status_user_closed_and_redo);
    }

    public Post() {}

    public Post(DataSnapshot dataSnapshot) {
        importData(dataSnapshot);
    }

    public Post(HashMap<String, Object> obj) {
        importData(obj);
    }

    @Override
    public Post importData(HashMap<String, Object> obj) {
        super.importData(obj);
        //core
        title = obj.get("title").toString();
        ref_topic =  BaseEntity.safeGetString(obj, "ref_topic");
        status = (int)obj.get("status");
        audio = obj.get("audio").toString();
        text = obj.get("text").toString();
        has_read = (boolean)obj.get("has_read");
        user_last_requested = BaseEntity.safeGetLong(obj, "user_last_requested");
        conversation = obj.get("conversation");

        //evaluate
        score = (int)obj.get("score");
        advisor_id = BaseEntity.safeGetString(obj, "advisor_id");

        //for re-post
        prev = BaseEntity.safeGetString(obj, "prev");
        next = BaseEntity.safeGetString(obj, "next");

        return this;
    }


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

    public String statusString() {
        return STATUS_TEXT[status];
    }

    public void setStatus(int status) {
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

    public boolean isHas_read() {
        return has_read;
    }

    public void setHas_read(boolean has_read) {
        this.has_read = has_read;
    }

    public static String buildUserStatusIndex(String userid, int status, String time_padding) {
        return Utils.padRight(userid, 48) + Utils.padLeft(status + "", 4) + Utils.padLeft(time_padding, 16);
    }

    public static String buildUserStatusIndex(String userid, int status) {
        return Utils.padRight(userid, 48) + Utils.padLeft(status + "", 4) + Utils.padLeft(Utils.getMillis() + "", 16);
    }

    public long getUser_last_request() {
        return user_last_requested;
    }

    public void setUser_last_request(long user_last_request) {
        this.user_last_requested = user_last_request;
    }

    public Object getConversation() {
        return conversation;
    }

    public ArrayList<HashMap<String, Object>> getConversationList() {
        if (conversation == null) {
            return new ArrayList<>();
        } else {
            return (ArrayList<HashMap<String, Object>>)conversation;
        }
    }

    public void setConversation(Object conversation) {
        this.conversation = conversation;
    }

    public HashMap exportData() {
        HashMap<String, Object> o = new HashMap<>();
        o.put("created_by", this.getCreated_by());
        o.put("created_date", this.getCreated_date());
        o.put("last_modified_by", this.getLast_modified_by());
        o.put("last_modified_date", this.getLast_modified_date());

        o.put("advisor_id", this.getAdvisor_id());
        //o.put("index_advisior_status", this.get());

        o.put("audio", this.getAudio());
        o.put("text", this.getText());
        o.put("title", this.getTitle());
        o.put("ref_topic", this.getRef_topic());
        o.put("status", this.getStatus());
        o.put("score", this.getScore());
        o.put("conversation", this.getConversation());

        o.put("next", this.getNext());
        o.put("prev", this.getPrev());

        o.put("has_read", this.isHas_read());
        o.put("user_last_request", this.getUser_last_request());

        return o;
    }
}
