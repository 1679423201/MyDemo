package com.hudun.mydemo.untils;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.hudun.mydemo.app.MyApplication;
import com.hudun.mydemo.myView.AudioItem;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *      @ClassName AudioUntil
 *      @Author  :YMD
 *      @E-mail  :1679423201@qq.com
 *      @Date 2021/12/14 15:51
 *      @Desc    :
 *      @Version :1.0
 * </pre>
 */
public class AudioUntil {
    private static final String TAG = "AUDIO_TEXT";
    public static final String MUSIC_FRAG = "MUSIC_F"; //音乐传输标识
    public static List<AudioItem> seekAudios(){
        List<AudioItem> audioItemList = new ArrayList<>();
        //数据表的Uri
        Uri audioUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        //需要获取数据表中的哪几列信息
        String [] projection = new String[]{
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST
        };
        String order = MediaStore.Files.FileColumns.SIZE+"DESC";
        Cursor cursor = MyApplication.getContext().getContentResolver().query(audioUri, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if(cursor != null){
            int nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
            while (cursor.moveToNext()){
                AudioItem item = new AudioItem();
                item.setName(cursor.getString(nameIndex));
                item.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                item.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
                audioItemList.add(item);
            }
            cursor.close();
        }
        return audioItemList;
    }



}
