package com.hudun.mydemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.hudun.mydemo.fontText.FontActivity;
import com.hudun.mydemo.fontText.WordResource;
import com.hudun.mydemo.myBroadcast.BroadcastActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "DEMO_TEXT";
    private BroadcastReceiver turnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerTurnBack();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    public void turnToFont(View view) {
        Intent intent = new Intent(this, FontActivity.class);
        startActivity(intent);
    }

    public void turnToBroadcast(View view) {
        Intent intent = new Intent(this, BroadcastActivity.class);
        startActivity(intent);
    }
    //注册一个广播接收器，可以返回主菜单
    void registerTurnBack(){
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(WordResource resource){
        Toast.makeText(this,"EvenBus收到信息了" + resource.getMessage(),Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(turnBack);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}