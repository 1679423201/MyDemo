package com.hudun.mydemo.myView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.hudun.mydemo.R;
import com.hudun.mydemo.app.MyApplication;
import com.hudun.mydemo.untils.AudioUntil;

import java.util.ArrayList;

public class MyViewTextActivity extends AppCompatActivity {

    private AudioCropView audioCropView;
    private static final String TAG = "VIEW_TEXT";
    private SeekBar seekBar;
    private MediaPlayer mediaPlayer;
    private ArrayList<AudioItem> itemArrayList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_view_test);

        audioCropView = findViewById(R.id.mAudioView);
        seekBar = findViewById(R.id.sb_control);
        seekBar.setMax(audioCropView.getTotal());
        setSB();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

            }
        });
    }

    void setSB(){
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioCropView.setPlayingTime(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }



    public void startPlay(View view) {
        requestPower();
        AudioUntil.seekAudios();

    }


    public void stopPlay(View view) {
    }



    /***
     * 权限申请
     */
    public void requestPower(){
        //判断是否已经赋予权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED){
            //判断是否被用户拒绝过权限
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)){

            }
            else {
                //申请权限
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        }, 1);
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
