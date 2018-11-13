package specificstep.com.onus.GlobalClasses;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtility {
    private static int status = 0;

    public static int copyFile(File src, File dst) throws IOException {
        // 1st way to copy database file
        try {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst);

            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            status = 1;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            Log.d("FILE", "Error while copying DB : " + ex.getMessage());
            status = 0;
        }

        // 2nd way to copy database file
//        FileChannel fromChannel = null;
//        FileChannel toChannel = null;
//        try {
//            fromChannel = new FileInputStream(src).getChannel();
//            toChannel = new FileOutputStream(dst).getChannel();
//            fromChannel.transferTo(0, fromChannel.size(), toChannel);
//            status = 1;
//        }
//        catch (Exception ex) {
//            ex.printStackTrace();
//            Log.d("FILE", "Error while copying DB : " + ex.getMessage());
//            status = 0;
//        }
//        finally {
//            try {
//                if (fromChannel != null) {
//                    fromChannel.close();
//                }
//            }
//            finally {
//                if (toChannel != null) {
//                    toChannel.close();
//                }
//            }
//        }

        Log.d("FILE", "File status : " + status);
        return status;
    }
}
