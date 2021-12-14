package com.hudun.mydemo.app;

import android.app.Application;
import android.content.Context;

/**
 * <pre>
 *      @ClassName MyApplication
 *      @Author  :YMD
 *      @E-mail  :1679423201@qq.com
 *      @Date 2021/12/14 16:40
 *      @Desc    :
 *      @Version :1.0
 * </pre>
 */
public class MyApplication extends Application {
    private static Application app;

    public static Context getContext() {
        return app;
    }

    @Override
    public void onCreate() {
        app = this;
        super.onCreate();
    }
}
