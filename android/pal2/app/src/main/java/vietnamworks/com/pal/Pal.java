package vietnamworks.com.pal;

import android.app.Application;

import com.crittercism.app.Crittercism;

import vietnamworks.com.pal.common.Utils;
import vietnamworks.com.pal.entities.Post;
import vietnamworks.com.pal.services.CloudinaryService;
import vietnamworks.com.pal.services.FileUploadService;
import vietnamworks.com.pal.services.FirebaseService;
import vietnamworks.com.pal.services.GaService;
import vietnamworks.com.pal.services.HttpService;
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
        ParseService.clearAllNotification();
        FirebaseService.init(this);
        FileUploadService.init();
        LocalStorage.init(this);
        GaService.init(this);
        CloudinaryService.init(this);
        Post.init(this);
        HttpService.init(this);

        if (!BuildConfig.DEBUG) {
            Crittercism.initialize(getApplicationContext(), Utils.r13(getString((R.string.crittercism_key))));
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        LocalStorage.close();
    }

}
