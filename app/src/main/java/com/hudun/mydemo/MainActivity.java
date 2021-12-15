package com.hudun.mydemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hudun.mydemo.fontText.FontActivity;
import com.hudun.mydemo.fontText.WordResource;
import com.hudun.mydemo.myBroadcast.BroadcastActivity;
import com.hudun.mydemo.myView.AudioPickActivity;
import com.hudun.mydemo.myView.MyViewTextActivity;
import com.hudun.mydemo.untils.PermissionUntil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "DEMO_TEXT";
    private BroadcastReceiver turnBack = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerTurnBack();
        EventBus.getDefault().register(this);
    }
    public void turnToFont(View view) {
        Intent intent = new Intent(this, FontActivity.class);
        startActivity(intent);
    }

    public void turnToBroadcast(View view) {
        Intent intent = new Intent(this, BroadcastActivity.class);
        startActivity(intent);
    }

    public void turnToMyView(View view) {
        if(PermissionUntil.requestPermission(this) == PermissionUntil.REQUEST_SUCCESS){
            Intent intent = new Intent(this, AudioPickActivity.class);
            startActivity(intent);
        }
    }

    //注册一个广播接收器，可以返回主菜单
    void registerTurnBack(){
        if(turnBack == null){
            turnBack = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Toast.makeText(context,intent.getStringExtra("sd")+"广播信息",Toast.LENGTH_SHORT).show();
                }
            };

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("TURN_BACK");
            LocalBroadcastManager.getInstance(this).registerReceiver(turnBack, intentFilter);
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(WordResource resource){
        Toast.makeText(this,"EvenBus收到信息了" + resource.getMessage(),Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: ");
        super.onStart();
    }
    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart: ");
        super.onRestart();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: ");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(turnBack);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}