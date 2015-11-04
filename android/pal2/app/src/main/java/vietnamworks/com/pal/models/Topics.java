package vietnamworks.com.pal.models;

import com.firebase.client.Query;

import java.util.ArrayList;

import vietnamworks.com.pal.entities.Topic;
import vietnamworks.com.pal.services.FirebaseService;


/**
 * Created by duynk on 9/16/15.
 */
public class Topics extends AbstractContainer<Topic> {
    protected Topics(){
        super();
        ArrayList<Topic> data = new ArrayList<>();
        this.setData(data);
    }
    public static Query getAllTopicsQuery() {
        return FirebaseService.newRef("topics").orderByChild("status").equalTo(1);
    }
}
