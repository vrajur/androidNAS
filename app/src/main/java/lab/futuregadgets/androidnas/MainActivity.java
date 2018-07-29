package lab.futuregadgets.androidnas;

import android.Manifest;
import android.media.tv.TvView;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

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
import java.util.ConcurrentModificationException;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissions(new String[] {Manifest.permission.INTERNET}, 1);

        TextView textView = findViewById(R.id.textview);
        AsyncTask<String, Void, Long> task = new MyAsyncTask();


        String url = "file://home/vinay/Documents/Data/UnifiedLogger/All-Data/GpsLog_2018-07-28_17-11-43.txt";
        task.execute(url);

        while(task.getStatus() != AsyncTask.Status.FINISHED) {
            AsyncTask.Status status = task.getStatus();

            switch (status) {
                case PENDING:
                    textView.setText("Task is PENDING");
                    break;
                case RUNNING:
                    textView.setText("Task is RUNNING");
                    break;
                case FINISHED:
                    textView.setText("Task is FINISHED");
                    break;
            }
        }

        try {
            Long size = task.get();
            textView.setText("File size is: " + size);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
