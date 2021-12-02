package com.hudun.mydemo.myBroadcast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hudun.mydemo.MainActivity;
import com.hudun.mydemo.R;
import com.hudun.mydemo.fontText.WordResource;

import org.greenrobot.eventbus.EventBus;

public class BroadcastActivity extends AppCompatActivity {
    private static final String TAG = "DEMO_TEXT";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcasr);
    }

    //按钮点击监听，发送广播并且传递信息
    public void sendBroadcast(View view) {
        WordResource resource =  new WordResource();
        resource.setMessage("这是一个信息");
        EventBus.getDefault().post(resource);
//        Intent intent = new Intent("TURN_BACK");
//        intent.putExtra("sd", "传过来的信息");
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, this.getClass().getName()+"++onDestroy: ");
        super.onDestroy();
    }
}