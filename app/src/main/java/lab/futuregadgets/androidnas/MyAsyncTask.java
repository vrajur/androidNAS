package lab.futuregadgets.androidnas;

import android.os.AsyncTask;
import android.util.Log;

import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by vinay on 7/29/18.
 */

public class MyAsyncTask extends AsyncTask<String, Void, Long> {
    @Override
    protected Long doInBackground(String... strings) {

        SMBClient client = new SMBClient();
        try (Connection connection = client.connect("10.10.1.12")) { // SERVER
            AuthenticationContext ac = new AuthenticationContext("USERNAME", "PASSWORD".toCharArray(), "DOMAIN"); //USERNAME PASSWORD DOMAIN
            Session session = connection.authenticate(ac);

            // Connect to Share
            try (DiskShare share = (DiskShare) session.connectShare("public")) {
                for (FileIdBothDirectoryInformation f : share.list("transport-drive", "*")) {
                    System.out.println("File : " + f.getFileName());
                }
            }
        } catch (IOException e ) {
            e.printStackTrace();
        }

        return Long.valueOf(-1);
    }

    @Override
    protected void onPostExecute(Long aLong) {
        super.onPostExecute(aLong);
        Log.d("POST", "Long: " + aLong.toString());
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
