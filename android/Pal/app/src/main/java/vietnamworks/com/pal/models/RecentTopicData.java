package vietnamworks.com.pal.models;

import java.util.ArrayList;
import java.util.Date;

import vietnamworks.com.pal.entities.RecentTopic;
import vietnamworks.com.pal.utils.Common;

/**
 * Created by duynk on 9/11/15.
 */
public class RecentTopicData extends AbstractContainer<RecentTopic> {
    protected RecentTopicData(){
        super();

        /***
         * This stuff is for testing
         */
        String[] postTitle = {"How to say Hello", "How to say thank you", "How to say Hello", "How to say thank you", "How to say Hello", "How to say thank you", "How to say Hello", "How to say thank you", "How to say Hello", "How to say thank you", "How to say Hello", "How to say thank you", "How to say Hello", "How to say thank you"};
        ArrayList<RecentTopic> data = new ArrayList<>();
        for (int i = 0; i < postTitle.length; i++) {
            RecentTopic d = new RecentTopic();
            d.setTitle(postTitle[i]);
            d.mCreatedDate = new Date();
            d.mScore = Common.randomInt(1, 5);
            d.mStatus = Common.randomInt(1, 10) < 5 ? RecentTopic.STATUS_WAITING: RecentTopic.STATUS_COMPLETED;
            if (d.mStatus == RecentTopic.STATUS_WAITING) {
                d.mScore = -1;
            }
            data.add(d);
        }
        this.setData(data);
    }
}
