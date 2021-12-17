package com.hudun.mydemo.myView;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * <pre>
 *      @ClassName MediaItem
 *      @Author  :YMD
 *      @E-mail  :1679423201@qq.com
 *      @Date 2021/12/10 15:35
 *      @Desc    :
 *      @Version :1.0
 * </pre>
 */
public class AudioItem implements Serializable, Parcelable {
    private String name;    //名字
    private long size;      //大小
    private int duration;  //时长
    private String path;    //路径
    private String artist;  //作者

    public AudioItem() {

    }
    public AudioItem(Parcel in) {
        name = in.readString();
        size = in.readLong();
        duration = in.readInt();
        path = in.readString();
        artist = in.readString();
    }


    public static final Creator<AudioItem> CREATOR = new Creator<AudioItem>() {
        @Override
        public AudioItem createFromParcel(Parcel in) {
            return new AudioItem(in);
        }

        @Override
        public AudioItem[] newArray(int size) {
            return new AudioItem[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    @Override
    public String toString() {
        return "MediaItem [name=" + name + ", size=" + size + ", duration="
                + duration + ", data=" + path + ", artist=" + artist + "]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeLong(size);
        dest.writeInt(duration);
        dest.writeString(path);
        dest.writeString(artist);
    }
}
