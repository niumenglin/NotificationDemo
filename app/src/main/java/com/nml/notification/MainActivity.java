package com.nml.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;

import static android.app.Notification.VISIBILITY_SECRET;

/**
 * @author niumenglin
 */
public class MainActivity extends AppCompatActivity {


    private NotificationManager manager;

    private Button btn_notification_delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_notification_delete = findViewById(R.id.btn_notification_delete);
        btn_notification_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getManager().cancel(3);//通知管理的-删除
            }
        });
//        sendNormalNotification();
//        sendProgressNotification();
        sendCustomNotification();
    }

    /**
     * 发送普通通知
     * 通知管理的-新增
     */
    private void sendNormalNotification(){
        Notification.Builder builder = getNotificationBuilder();
        getManager().notify(1,builder.build());
    }

    /**
     * 发送下载进度通知
     * 通知管理的-更新
     */
    private void sendProgressNotification(){
        final Notification.Builder builder = getNotificationBuilder();
        builder.setDefaults(Notification.FLAG_ONLY_ALERT_ONCE);//多次回调，避免通知栏的频繁声音
        getManager().notify(2,builder.build());
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    try {
                        Thread.sleep(1000);//1秒后更新一下
                        builder.setDefaults(Notification.FLAG_ONLY_ALERT_ONCE);
                        //设置进度，max最大进度值，progress当前进度，indeterminate不确定的
                        builder.setProgress(100,i,false);
                        getManager().notify(2,builder.build());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void sendCustomNotification(){
        Notification.Builder builder = getNotificationBuilder();

        RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.layout_custom_notification);
        remoteViews.setTextViewText(R.id.tv_notification_title,"custom_title");
        remoteViews.setTextViewText(R.id.tv_notification_content,"custom_content");

        Intent intent =  new Intent(this,NotificationIntentActivity.class);
        //即将要发生的意图，它是可以被取消 更新
        PendingIntent pendingIntent = PendingIntent.getActivity(this,-1,
                intent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btn_notification_jump,pendingIntent);

//        remoteViews.setOnClickFillInIntent(R.id.btn_notification_jump,intent);//不推荐使用，比较耗资源

        builder.setCustomContentView(remoteViews);

        getManager().notify(3,builder.build());
    }

    /**
     * 兼容适配android8.0以及之前版本
     * @return
     */
    private Notification.Builder getNotificationBuilder(){
        //系统版本 大于等于android8.0
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("channel_id","channel_name", NotificationManager.IMPORTANCE_DEFAULT);
            channel.canBypassDnd();//可否绕过请勿打扰模式
            channel.setBypassDnd(true);//设置可以绕过 请勿打扰模式
            channel.enableLights(true);//闪光
            channel.setLockscreenVisibility(VISIBILITY_SECRET);//锁屏显示通知
            channel.setLightColor(Color.RED);//指定闪光是的灯光颜色
            channel.canShowBadge();//桌面launcher消息角标
            channel.enableVibration(true);//是否允许震动
            channel.getAudioAttributes();//获取系统通知响铃声音的配置
            channel.getGroup();//获取通知渠道组
            channel.setVibrationPattern(new long[]{100,100,200});//震动的模式
            channel.shouldShowLights();//是否会闪光

            getManager().createNotificationChannel(channel);//向系统服务创建通知渠道
        }
        return new Notification.Builder(this)
                .setAutoCancel(true)
                .setChannelId("channel_id")//必须和NotificationChannel的id一样
                .setContentTitle("新消息来了")
                .setContentText("明天是周六，不上班啊好开心")
                .setSmallIcon(R.mipmap.ic_launcher);
    }

    /**
     * 获取系统服务 NotificationManager对象
     * @return manager
     */
    private NotificationManager getManager(){
        if (manager==null){
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

}
