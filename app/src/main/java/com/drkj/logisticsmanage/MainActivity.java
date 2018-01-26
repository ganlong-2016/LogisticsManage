package com.drkj.logisticsmanage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.Result;
import com.vondear.rxtools.RxActivityTool;
import com.vondear.rxtools.activity.ActivityScanerCode;
import com.vondear.rxtools.interfaces.OnRxScanerListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    private int RESULT_OK = 0xA1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button_scan_qrcode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtil.isCameraCanUse()) {
                    ActivityScanerCode.setScanerListener(new OnRxScanerListener() {
                        @Override
                        public void onSuccess(String s, Result result) {
                            RxActivityTool.finishActivity(ActivityScanerCode.class);
                            handleResult(result.toString());
//                            Toast.makeText(MainActivity.this, result.toString(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFail(String s, String s1) {
                            Toast.makeText(MainActivity.this, s1, Toast.LENGTH_SHORT).show();
                        }
                    });
                    Intent intent = new Intent(MainActivity.this, ActivityScanerCode.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "请打开此应用的摄像头权限！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void handleResult(String url) {
        Log.i("result", "handleResult: url" + url);
        try {
            JSONObject object = new JSONObject(url);
            String jiekou = object.getString("interface");
            if (TextUtils.equals(jiekou, "handleAssignOrder")) {

                final String assignOrderNumber = object.getString("assignOrderNumber");

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        postLoadStart(assignOrderNumber);
                    }
                }).start();
            }
        } catch (JSONException e) {
            Log.i("result", "handleResult: ");
            e.printStackTrace();
        }

    }

    private void postLoadStart(String assignOrderNumber) {
        try {
            Log.i("result", "postLoadStart: 查询分拣单" + assignOrderNumber);
            String parm = "assignOrderNumber=" + URLEncoder.encode(assignOrderNumber, "UTF-8");
            HttpUtils.post("http://106.15.57.208:18080/entrance/app/mode/getAssignOrderInfo", parm, new HttpUtils.MyCallback() {
                @Override
                public void success(final String result) {
                    try {
                        Gson gson = new Gson();
                        final data data = gson.fromJson(result, com.drkj.logisticsmanage.data.class);
                        Log.i("result", "success: " + result);
                        if (data.getCode() == 200) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (data.getData().getAssignState() == 2) {
                                        Toast.makeText(MainActivity.this, "该分拣单已完成分拣", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Intent intent = new Intent(MainActivity.this, AssignOrderDetailActivity.class);
                                        intent.putExtra("result", result);
                                        startActivity(intent);
                                    }
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void error(Exception e) {
                    Log.i("result", "error: ");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "连接服务器异常", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

}
