package lab.futuregadgets.androidnas;

import android.Manifest;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.Directory;
import com.hierynomus.smbj.share.DiskShare;
import com.hierynomus.smbj.utils.SmbFiles;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vinay on 7/29/18.
 */

public class MyAsyncTask extends AsyncTask<String, Void, String[]> {

    MainActivity activity;

    public MyAsyncTask(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    protected String[] doInBackground(String... strings) {

        // Files in Directory:
        List<String> dirFiles = new ArrayList<String>();

        SMBClient client = new SMBClient();
        try (Connection connection = client.connect("10.10.1.12")) { // SERVER
            AuthenticationContext ac = new AuthenticationContext("USERNAME", "PASSWORD".toCharArray(), "DOMAIN"); //USERNAME PASSWORD DOMAIN
            Session session = connection.authenticate(ac);

            // Request File Permissions
//            activity.requestPermissions(new String[]{
//                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
//            }, 1);

            // Get Test Video File
            File path = Environment.getExternalStorageDirectory();
            File file = new File(path, "test/test.mp4");
            assert file.exists();
            Log.d("TestVideoFile", "Video File: " + file.getAbsolutePath());

            // Connect to Share
            try (DiskShare share = (DiskShare) session.connectShare("public")) {

                // Copy File:
                String destPath = "transport-drive/test.mp4";
                SmbFiles.copy(file, share, destPath, true);
                Log.d("Copy File", "File copied to NAS");

                for (FileIdBothDirectoryInformation f : share.list("transport-drive", "*")) {
                    dirFiles.add(f.getFileName());
                    System.out.println("File : " + f.getFileName());
                }
            }
        } catch (IOException e ) {
            e.printStackTrace();
        }

        Log.d("Async", "doInBackground Complete");
        return dirFiles.toArray(new String[dirFiles.size()]);
    }

    @Override
    protected void onPostExecute(String[] strings) {
        super.onPostExecute(strings);
        Log.d("Async", "onPostExecute Complete");
        Log.d("POST", "Long: " + strings.toString());
    }

    private static int getFileSize(URL url) {
        URLConnection conn = null;
        try {
            conn = url.openConnection();
            if(conn instanceof HttpURLConnection) {
                ((HttpURLConnection)conn).setRequestMethod("HEAD");
            }
            conn.getInputStream();
            return conn.getContentLength();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if(conn instanceof HttpURLConnection) {
                ((HttpURLConnection)conn).disconnect();
            }
        }
    }
}
