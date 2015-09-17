package vietnamworks.com.pal.models;

import java.util.ArrayList;
import java.util.Date;

import vietnamworks.com.pal.utils.Common;

/**
 * Created by duynk on 9/11/15.
 */
public class RecentThreadData extends AbstractContainer<RecentThread> {
    protected RecentThreadData(){
        super();

        /***
         * This stuff is for testing
         */
        String[] postTitle = {"How to say Hello", "How to say thank you", "How to say Hello", "How to say thank you", "How to say Hello", "How to say thank you", "How to say Hello", "How to say thank you", "How to say Hello", "How to say thank you", "How to say Hello", "How to say thank you", "How to say Hello", "How to say thank you"};
        ArrayList<RecentThread> data = new ArrayList<>();
        for (int i = 0; i < postTitle.length; i++) {
            RecentThread d = new RecentThread();
            d.title = postTitle[i];
            d.createdDate = new Date();
            d.score = Common.randomInt(1, 5);
            d.status = Common.randomInt(1, 10) < 5 ? RecentThread.STATUS_WAITING:RecentThread.STATUS_COMPLETED;
            if (d.status == RecentThread.STATUS_WAITING) {
                d.score = -1;
            }
            data.add(d);
        }
        this.setData(data);
    }
}
