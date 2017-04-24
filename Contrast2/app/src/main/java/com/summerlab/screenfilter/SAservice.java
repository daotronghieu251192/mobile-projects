package com.summerlab.screenfilter;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import android.os.Handler;

import java.util.concurrent.TimeUnit;

/**
 * Created by sev_user on 12/2/2015.
 */
public class SAservice extends Service {
    public static int a, b, c, g;
    static Handler handler;
    static WindowManager manager;
    static WindowManager.LayoutParams params;
    public static int r;
    public static boolean state;
    private static Layer view1, view2, view3, view11, view111;
    ScheduledExecutorService service;

    private static final int NOTIFICATION_ID = 9999;

    static {
        state = false;
        handler = new Handler();

    }

    public static void dim(final int n) {

        handler.post(new Runnable() {

            @Override
            public void run() {
                if (view2 != null) {
                    view2.setColor(n, 0, 0, 0);
                }
            }
        });

    }

    public static void setAlpha(int n) {
        a = n;
        if (view2 == null) {
            return;
        }
        if (a == 0) {
            view2.setVisibility(View.INVISIBLE);
        } else {
            view2.setVisibility(View.VISIBLE);
        }
        if (view2 == null) return;
        view2.setColor(a, 0, 0, 0);
    }

    public static void setContrast(int n) {
        c = n;
        if (view3 == null) {
            return;
        }
        if (c == 0) {
            view3.setVisibility(View.INVISIBLE);
        } else {
            view3.setVisibility(View.VISIBLE);
        }
        view3.setColor(c, 100, 100, 100);

    }

    public static void setRGB(int n, int n2, int n3) {
        r = n;
        g = n2;
        b = n3;
        if (view1 == null || view11 == null || view111 == null) {
            return;
        }
        if (r == 0) {
            view1.setVisibility(View.INVISIBLE);
        } else {
            view1.setVisibility(View.VISIBLE);
        }
        if (g == 0) {
            view11.setVisibility(View.INVISIBLE);
        } else {
            view11.setVisibility(View.VISIBLE);
        }
        if (b == 0) {
            view111.setVisibility(View.INVISIBLE);
        } else {
            view111.setVisibility(View.VISIBLE);
        }

        view1.setColor(r, 255, 0, 0);
        view11.setColor(g, 0, 255, 0);
        view111.setColor(b, 0, 0, 255);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Notify();

        SharedPreferences sharedPreferences = this.getSharedPreferences("pref", 0);
        state = true;
        a = sharedPreferences.getInt("a", 0);
        r = sharedPreferences.getInt("r", 0);
        g = sharedPreferences.getInt("g", 0);
        b = sharedPreferences.getInt("b", 0);
        c = sharedPreferences.getInt("c", 0);
        view1 = new Layer((Context) this);
        view11 = new Layer((Context) this);
        view111 = new Layer((Context) this);
        view2 = new Layer((Context) this);
        view3 = new Layer((Context) this);
        if (a == 0) {
            view2.setVisibility(View.INVISIBLE);
        }
        if (c == 0) {
            view3.setVisibility(View.VISIBLE);
        }
        SAservice.setRGB(r, g, b);
        view3.setColor(c, 100, 100, 100);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-1, -1, 2006, 280, -2);
        WindowManager windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);
        windowManager.addView((View) view111, (ViewGroup.LayoutParams) layoutParams);
        windowManager.addView((View) view11, (ViewGroup.LayoutParams) layoutParams);
        windowManager.addView((View) view1, (ViewGroup.LayoutParams) layoutParams);
        windowManager.addView((View) view3, (ViewGroup.LayoutParams) layoutParams);
        windowManager.addView((View) view2, (ViewGroup.LayoutParams) layoutParams);


    }

    private void Notify() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Screen Filter")
                        .setContentText("Click to open application.");
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    public void stop() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                SAservice.this.service.shutdown();
            }
        });
    }

    @Override
    public void onStart(Intent intent, int n) {
        super.onStart(intent, n);
        if (this.service == null) {
            this.service = Executors.newSingleThreadScheduledExecutor();
            this.service.scheduleAtFixedRate((Runnable) this.new Task(), 0, 4, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void onDestroy() {

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(NOTIFICATION_ID);

        state = false;
        if (view1 != null) {
            ((WindowManager) this.getSystemService(WINDOW_SERVICE)).removeView((View) view1);
            ((WindowManager) this.getSystemService(WINDOW_SERVICE)).removeView((View) view11);
            ((WindowManager) this.getSystemService(WINDOW_SERVICE)).removeView((View) view111);
            ((WindowManager) this.getSystemService(WINDOW_SERVICE)).removeView((View) view2);
            ((WindowManager) this.getSystemService(WINDOW_SERVICE)).removeView((View) view3);
            view1 = null;
            view2 = null;
            view3 = null;
            view11 = null;
            view111 = null;
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class Task implements Runnable {
        int i;

        Task() {
            this.i = 0;
        }

        public void run() {
            this.i = 1 + this.i;
            if (this.i <= SAservice.a) {
                SAservice.dim(this.i);
                return;
            }
            SAservice.this.stop();

        }
    }
}
