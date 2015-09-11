package vietnamworks.com.pal.models;

import java.util.ArrayList;
import java.util.Date;

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
            data.add(d);
        }
        this.setData(data);
    }
}
