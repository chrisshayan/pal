package vietnamworks.com.pal.services;

import android.content.Context;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import vietnamworks.com.pal.R;

/**
 * Created by duynk on 11/16/15.
 */
public class CloudinaryService {
    static Cloudinary cloudinary;
    static Context context;
    public static void init(Context ctx) {
        Map<String, String> config = new HashMap();
        context = ctx;
        config.put("cloud_name", ctx.getString(R.string.cloudinary_cloud_name));
        config.put("api_key", ctx.getString(R.string.cloudinary_api_key));
        config.put("api_secret", ctx.getString(R.string.cloudinary_api_secret));
        cloudinary = new Cloudinary(config);
    }

    public static void upload(final String input, final String public_name, final AsyncCallback callback) {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    File file = new File(input);
                    FileInputStream fileInputStream = new FileInputStream(file);
                    //cloudinary.uploader().upload(fileInputStream, ObjectUtils.asMap("public_id", public_name));
                    Map m = cloudinary.uploader().uploadLarge(fileInputStream, ObjectUtils.asMap("folder", "pal_recorder", "public_id", public_name, "resource_type", "video", "chunk_size", 6000000));
                    callback.onSuccess(context, m);
                } catch (Exception e) {
                    callback.onError(context, 0, "");
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
