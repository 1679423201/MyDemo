package com.hudun.mydemo.untils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * <pre>
 *      @ClassName PremissionUntil
 *      @Author  :YMD
 *      @E-mail  :1679423201@qq.com
 *      @Date 2021/12/15 15:12
 *      @Desc    :  权限申请工具类
 *      @Version :1.0
 * </pre>
 */
public class PermissionUntil {
    public static final int REQUEST_SUCCESS = 1;
    public static final int REQUEST_FAIL = 0;
    public static final int REQUEST_FAILED = -1;
    private static int type;
    /***
     * 权限申请
     */
    public static int requestPermission(Activity activity){
        //判断是否已经赋予权限
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED){
            //判断是否被用户拒绝过权限
            if(ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE)){
                type = REQUEST_FAILED;
            }
            else {
                //申请权限
                ActivityCompat.requestPermissions(activity,
                        new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        }, 1);
            }
        }
        else{
            type = REQUEST_SUCCESS;
        }
        if(type != REQUEST_SUCCESS){
            AlertDialog dialog = new AlertDialog.Builder(activity)
                    .setTitle("警告")
                    .setMessage("请前往设置->应用->PermissionDemo->权限中打开相关权限，否则功能无法正常运行！")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .show();
        }
        return type;
    }
}
