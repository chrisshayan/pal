package vietnamworks.com.pal.entities;

/**
 * Created by duynk on 10/1/15.
 */
public class Post extends BaseEntity {
    private String answer_audio = "";
    private long answer_date = 0;
    private String audio = "";
    private String picked_by = "";
    private long picked_date = 0;
    private int score = 0;
    private int status = 0;
    private String title = "";
    private String topic_ref = null;

    public Post() {
    }

    public Post(Topic t) {
        title = t.getTitle();
        this.topic_ref = t.getId();
    }

    public String getAnswer_audio() {
        return answer_audio;
    }

    public void setAnswer_audio(String answer_audio) {
        this.answer_audio = answer_audio;
    }

    public long getAnswer_date() {
        return answer_date;
    }

    public void setAnswer_date(long answer_date) {
        this.answer_date = answer_date;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getPicked_by() {
        return picked_by;
    }

    public void setPicked_by(String picked_by) {
        this.picked_by = picked_by;
    }

    public long getPicked_date() {
        return picked_date;
    }

    public void setPicked_date(long picked_date) {
        this.picked_date = picked_date;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTopic_ref() {
        return topic_ref;
    }

    public void setTopic_ref(String topic_ref) {
        this.topic_ref = topic_ref;
    }

}
