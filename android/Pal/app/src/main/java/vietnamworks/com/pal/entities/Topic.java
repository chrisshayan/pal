package vietnamworks.com.pal.entities;

/**
 * Created by duynk on 10/1/15.
 */
public class Topic extends BaseEntity {
    private String title;
    private int status;

    public String getTitle() {return this.title;}
    public void setTitle(String title) {this.title = title;}

    public int getStatus() {return this.status;}
    public void setStatus(int status) {this.status = status;}
}
