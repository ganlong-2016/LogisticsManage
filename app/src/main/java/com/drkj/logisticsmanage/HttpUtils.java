package com.drkj.logisticsmanage;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ganlong on 2017/11/29.
 */

public class HttpUtils {

    public interface MyCallback{
        void success(String result);
        void error(Exception e);
    }
    public static void post(String httpUrl,String parms,MyCallback callback){
        URL url;
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        PrintWriter writer = null;
        InputStream in = null;
        try {
            Log.i("result", "post: 开始+"+parms);
            url = new URL(httpUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setConnectTimeout(3000);
            Log.i("result", "post: 开始+1");
            writer = new PrintWriter(connection.getOutputStream());
            writer.print(parms);
            writer.flush();
            writer.close();
            Log.i("result", "post: 开始+2");
            in = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(in));
            Log.i("result", "post: 开始+3");
            StringBuilder result = new StringBuilder();
            String line;
            Log.i("result", "post: 开始+4");
            while ((line = reader.readLine()) != null) {
                Log.i("result", "post: 开始+TIANJIA");
                result.append(line);
                Log.i("result", "post: 开始+dfgf");
            }
            Log.i("result", "post: 完成");
            callback.success(result.toString());
        } catch (Exception e) {
            Log.i("result", "post: 异常");
            e.printStackTrace();
            callback.error(e);
        } finally {
            if (connection!=null){
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (writer!=null){
                writer.close();
            }
            if (in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
