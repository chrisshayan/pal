package vietnamworks.com.pal.models;

import java.util.Date;

/**
 * Created by duynk on 9/11/15.
 */
public class RecentThread {
    public final static int STATUS_WAITING      = 0;
    public final static int STATUS_COMPLETED    = 1;

    public String title;
    public Date createdDate;
    public float score;
    public int status;

    protected RecentThread() {}

}
