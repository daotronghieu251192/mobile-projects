package com.summerlab.wishcards.Utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by sev_user on 12/24/2015.
 */
public class Utils {
    public static String[] getFileNamesFromAssets(Context context, String path) throws IOException {
        return context.getAssets().list(path);
    }

    public static int getDPI() {
        int dpi = 0;
        try {
//            ProcessBuilder mProcessBuilder = new ProcessBuilder(new String[]{"adb", "shell", "wm", "density"});
//            Process mProcess = mProcessBuilder.start();
//
//            InputStream mInputStream = mProcess.getInputStream();
//            BufferedReader mBufferedReader = new BufferedReader(new InputStreamReader(mInputStream));

            Process mProcess = Runtime.getRuntime().exec("wm density");
            BufferedReader mBufferedReader = new BufferedReader(
                    new InputStreamReader(mProcess.getInputStream()));

            String result = null;
            // get output result
            while ((result = mBufferedReader.readLine()) != null && !result.contains("Physical density")) {
                System.out.println(result);
            }

            mProcess.waitFor();

            String[] listWords = result.split(" ");
            dpi = Integer.parseInt(listWords[listWords.length - 1]);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return dpi;
    }
}
