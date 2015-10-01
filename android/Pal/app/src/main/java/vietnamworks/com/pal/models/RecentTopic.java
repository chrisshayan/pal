package vietnamworks.com.pal.models;

import java.util.Date;

import vietnamworks.com.pal.entities.Topic;

/**
 * Created by duynk on 9/11/15.
 */
public class RecentTopic extends Topic {
    public final static int STATUS_WAITING      = 0;
    public final static int STATUS_COMPLETED    = 1;

    public Date mCreatedDate;
    public float mScore;
    public int mStatus;

    protected RecentTopic() {}

}
