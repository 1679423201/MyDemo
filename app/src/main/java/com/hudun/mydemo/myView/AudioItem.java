package com.hudun.mydemo.myView;

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
public class AudioItem implements Serializable {
    private String name;    //名字
    private long size;      //大小
    private long duration;  //时长
    private String data;    //日期
    private String artist;  //作者

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

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
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
                + duration + ", data=" + data + ", artist=" + artist + "]";
    }

}
