package com.example.routesapp.AndroidVideoCache;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.danikula.videocache.HttpProxyCacheServer;
import com.example.routesapp.R;

import java.util.ArrayList;
import java.util.List;

public class Main3Activity extends AppCompatActivity {


    private VideoView ADS_VideoView;

    private List<String> VideoList;
    private int currentVideoIndex = 0;

 //   private String VIDEO_URL = "http://routesdashboard.com//UploadedFiles/c255c95b-5a5a-47b1-acb6-19297ae76bd6.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        ADS_VideoView = findViewById(R.id.ADS_VideoView);

        VideoList = new ArrayList<String>();
        VideoList.add("http://routesdashboard.com//UploadedFiles/c255c95b-5a5a-47b1-acb6-19297ae76bd6.mp4");
        VideoList.add("http://routesdashboard.com//UploadedFiles/8ce13e03-a499-4f6d-ab11-3bf6cb0a2708.mp4");
        VideoList.add("http://routesdashboard.com//UploadedFiles/a6b5eb9c-ff6b-44c7-b9e5-99011ceb8fcd.mp4");
        VideoList.add("http://routesdashboard.com//UploadedFiles/8d5cb286-3d2e-4847-9f90-9036478d9831.mp4");
        VideoList.add("http://routesdashboard.com//UploadedFiles/b088d18d-314d-4aa0-b673-8a6b6ccdb6be.mp4");
        VideoList.add("http://routesdashboard.com//UploadedFiles/28ea480b-1666-42b2-86c6-da41e69e1ee8.mp4");
        VideoList.add("http://routesdashboard.com//UploadedFiles/c255c95b-5a5a-47b1-acb6-19297ae76bd6.mp4");
        VideoList.add("http://routesdashboard.com//UploadedFiles/8ce13e03-a499-4f6d-ab11-3bf6cb0a2708.mp4");
        VideoList.add("http://routesdashboard.com//UploadedFiles/a6b5eb9c-ff6b-44c7-b9e5-99011ceb8fcd.mp4");
        VideoList.add("http://routesdashboard.com//UploadedFiles/8d5cb286-3d2e-4847-9f90-9036478d9831.mp4");
        VideoList.add("http://routesdashboard.com//UploadedFiles/b088d18d-314d-4aa0-b673-8a6b6ccdb6be.mp4");
        VideoList.add("http://routesdashboard.com//UploadedFiles/28ea480b-1666-42b2-86c6-da41e69e1ee8.mp4");


        playVideo();



    }

    private void playVideo(){

        Toast.makeText(Main3Activity.this, "Cache Storage Space .... Total:  " + getApplicationContext().getCacheDir().getTotalSpace() + " , Free:  " + getApplicationContext().getCacheDir().getFreeSpace() + " , Used:  " + getApplicationContext().getCacheDir().getUsableSpace(), Toast.LENGTH_SHORT).show();

        if (currentVideoIndex < VideoList.size()){
            HttpProxyCacheServer proxy = App.getProxy(Main3Activity.this);
            String proxyUrl = proxy.getProxyUrl(VideoList.get(currentVideoIndex));
            ADS_VideoView.setVideoPath(proxyUrl);

            MediaController mediaController = new MediaController(Main3Activity.this);
            mediaController.setAnchorView(ADS_VideoView);

            ADS_VideoView.setMediaController(mediaController);


            ADS_VideoView.requestFocus();

            ADS_VideoView.start();

            ADS_VideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    currentVideoIndex++;
                    playVideo();
                }
            });
        }else {
            currentVideoIndex = 0;
            playVideo();
        }

    }

}
