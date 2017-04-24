package com.summerlab.screenfilter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by sev_user on 12/14/2015.
 */
public class Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        
        if(intent.getAction().equals((Object)"android.intent.action.BOOT_COMPLETED"))
        {
            if( !PreferenceManager.getDefaultSharedPreferences(context).getBoolean("pref",false)|| context.getSharedPreferences("pref",0).getInt("a",300) >= 200) return;
            {
               context.startService(new Intent(context,(Class)SAservice.class));
                return;
            }
        }
        else {
            if (!intent.getAction().equals((Object)"com.netmanslab.sa.STOPSTART2")) return;
            {
                
                if (SAservice.state)
                {
                    
                    final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
                    scheduledExecutorService.scheduleAtFixedRate(new Runnable() {

                        int i;
                        @Override
                        public void run() {
                            this.i = -1+ this.i;
                            if(this.i >=0)
                            {
                                SAservice.dim(this.i);
                                return;
                            }
                            scheduledExecutorService.shutdown();
                            
                            context.stopService(new Intent(context,SAservice.class));
                        }
                    },0,4, TimeUnit.MILLISECONDS);
                    return;
                }


            }
        }

    }
}
