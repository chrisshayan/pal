package vietnamworks.com.pal;

import android.app.Application;

import vietnamworks.com.pal.services.FileUploadService;
import vietnamworks.com.pal.services.FirebaseService;
import vietnamworks.com.pal.services.LocalStorage;
import vietnamworks.com.pal.services.ParseService;

/**
 * Created by duynk on 10/26/15.
 */
public class Pal extends Application {
    public Pal() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ParseService.init(this);
        FirebaseService.init();
        FileUploadService.init();
        LocalStorage.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        LocalStorage.close();
    }

}
