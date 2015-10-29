package vietnamworks.com.pal.entities;

/**
 * Created by duynk on 10/1/15.
 */
public class Topic extends BaseEntity {
    public final static int TYPE_SPEAKING = 0;
    public final static int TYPE_WRITING = 1;

    private String title;
    private int status;
    private int type;

    public String getTitle() {return this.title;}
    public void setTitle(String title) {this.title = title;}

    public int getStatus() {return this.status;}
    public void setStatus(int status) {this.status = status;}

    public int getType() {
        return type;
    }

    public String getTypeName() {
        return type == 0?"Speaking":"Writing";
    }

    public void setType(int type) {
        this.type = type;
    }
}
