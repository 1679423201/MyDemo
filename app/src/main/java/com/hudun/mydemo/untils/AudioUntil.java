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
            Log.d(TAG, "seekAudios: 不是空");
            int nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
            System.out.println(nameIndex);
            int dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            Log.d(TAG, "seekAudios: "+ cursor.getCount());
            while (cursor.moveToNext()){
                Log.d(TAG, "seekAudios:  nameIndex = " + nameIndex);
                Log.d(TAG, "seekAudios:  dataIndex = " + dataIndex);
                AudioItem item = new AudioItem();
                item.setName(cursor.getString(nameIndex));
                item.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                if (cursor.getString(nameIndex) == null) {
                    Log.d(TAG, "seekAudios: 获取为空");
                }else Log.d(TAG, "seekAudios:  == "+cursor.getString(nameIndex) );
                System.out.println(cursor.getString(nameIndex));
                Log.d(TAG, "seekAudios: "+ item.getName());
                audioItemList.add(item);
            }
            Log.d(TAG, "seekAudios:  list long ="+audioItemList.size());
            cursor.close();
        }
        Log.d(TAG, "seekAudios: 是空");
        return audioItemList;
    }



}
