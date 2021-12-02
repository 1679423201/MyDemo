package com.hudun.mydemo.fontText;

import androidx.annotation.ColorLong;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hudun.mydemo.R;

public class FontActivity extends AppCompatActivity {
    WordResource resource;  //文本资源1
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_font);

        resource = new WordResource();
        SpannableString string = new SpannableString(resource.firstWord);
        string.setSpan(new ForegroundColorSpan(Color.BLUE), 20, 24, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        string.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Toast.makeText(getApplicationContext(),"sd", Toast.LENGTH_SHORT).show();
            }
        },20,24,Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        TextView first = findViewById(R.id.tv_first);
        //要相应点击事件必须加上这一步
        first.setMovementMethod(LinkMovementMethod.getInstance());
        first.setText(string);
    }
}