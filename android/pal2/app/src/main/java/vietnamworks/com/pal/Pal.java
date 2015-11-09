package vietnamworks.com.pal;

import android.app.Application;

import com.crittercism.app.Crittercism;

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

        Crittercism.initialize(getApplicationContext(), "564080588d4d8c0a00d0817d");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        LocalStorage.close();
    }

}
