package vietnamworks.com.pal.entities;

/**
 * Created by duynk on 10/1/15.
 */
public class BaseEntity {
    protected String created_by;
    protected long created_date;
    protected String last_modified_by;
    protected long last_modified_date;

    public String getCreated_by(){return this.created_by;}
    public long getCreated_date(){return this.created_date;}

    public String getLast_modified_by(){return this.last_modified_by;}
    public long getLast_modified_date(){return this.last_modified_date;}
}
