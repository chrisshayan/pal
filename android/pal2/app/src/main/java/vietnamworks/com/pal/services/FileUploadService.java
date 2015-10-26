package vietnamworks.com.pal.services;

import com.alexbbb.uploadservice.AllCertificatesAndHostsTruster;

/**
 * Created by duynk on 10/26/15.
 */
public class FileUploadService {
    public static void init() {
        AllCertificatesAndHostsTruster.apply();
    }
}
