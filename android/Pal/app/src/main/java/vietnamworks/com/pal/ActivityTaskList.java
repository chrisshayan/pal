package vietnamworks.com.pal;

import android.os.Bundle;

/**
 * Created by duynk on 10/6/15.
 */
public class ActivityTaskList extends ActivityBase {
    public ActivityTaskList() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
    }

}
