package vietnamworks.com.pal.entities;

import java.util.Date;

/**
 * Created by duynk on 9/11/15.
 */
public class RecentTopic extends Topic {
    public final static int STATUS_WAITING      = 0;
    public final static int STATUS_COMPLETED    = 1;

    public Date mCreatedDate;
    public float mScore;
    public int mStatus;

    public RecentTopic() {}

}
