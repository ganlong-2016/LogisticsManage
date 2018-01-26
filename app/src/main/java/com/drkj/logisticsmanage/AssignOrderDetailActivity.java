package com.drkj.logisticsmanage;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssignOrderDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private ExpandableListView expandableListView
            ;
    private Button button;
    private ExpandableListAdapter adapter;
    private List<String> list = new ArrayList<>();
    private data data;
    private boolean start = false;
    private Map<String, List<String>> dataset = new HashMap<>();
    ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
    ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
            = new ArrayList<ArrayList<HashMap<String, String>>>();
    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_order_detail);

        expandableListView = findViewById(R.id.list_assign_order_detail);
        button = findViewById(R.id.button_start_assign);
        button.setOnClickListener(this);

        Intent intent = getIntent();
        String result = intent.getStringExtra("result");
        Gson gson = new Gson();
        data = gson.fromJson(result, com.drkj.logisticsmanage.data.class);
        if (data.getData().getAssignState()==1){
            start=true;
            button.setText("结束分拣");
        }
        HashMap<String, String> currentServiceData1 = new HashMap<String, String>();
        currentServiceData1.put(LIST_NAME,"分拣单号");
        currentServiceData1.put(LIST_UUID,data.getData().getAssignOrderNumber());
        gattServiceData.add(currentServiceData1);
        gattCharacteristicData.add(new ArrayList<HashMap<String, String>>());
        HashMap<String, String> currentServiceData2 = new HashMap<String, String>();
        currentServiceData2.put(LIST_NAME,"车牌号");
        currentServiceData2.put(LIST_UUID,data.getData().getCarVo().getCarNumber());
        gattServiceData.add(currentServiceData2);
        gattCharacteristicData.add(new ArrayList<HashMap<String, String>>());

        for (com.drkj.logisticsmanage.data.DataBean.CategoryPrintVoListBean bean:data.getData().getCategoryPrintVoList()){
            HashMap<String, String> currentServiceData3 = new HashMap<String, String>();
            currentServiceData3.put(LIST_NAME,"商品名称");
            currentServiceData3.put(LIST_UUID,bean.getCategoryVo().getGoodsName());
            gattServiceData.add(currentServiceData3);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            HashMap<String, String> currentCharaData = new HashMap<String, String>();
            currentCharaData.put(
                    LIST_NAME, "执行配送数量");
            currentCharaData.put(LIST_UUID, bean.getCategoryCount()+"");
            gattCharacteristicGroupData.add(currentCharaData);

            HashMap<String, String> currentCharaData1 = new HashMap<String, String>();
            currentCharaData1.put(
                    LIST_NAME, "储位");
            currentCharaData1.put(LIST_UUID, bean.getCategoryVo().getAreaName());
            gattCharacteristicGroupData.add(currentCharaData1);

            HashMap<String, String> currentCharaData2 = new HashMap<String, String>();
            currentCharaData2.put(
                    LIST_NAME, "规格");
            currentCharaData2.put(LIST_UUID, bean.getCategoryVo().getAttribute()+"");
            gattCharacteristicGroupData.add(currentCharaData2);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }
        for (com.drkj.logisticsmanage.data.DataBean.OrderVosBean bean1 : data.getData().getOrderVos()){
            for (com.drkj.logisticsmanage.data.DataBean.OrderVosBean.GoodsOrderVoListBean bean:bean1.getGoodsOrderVoList()){
                HashMap<String, String> currentServiceData3 = new HashMap<String, String>();
                currentServiceData3.put(LIST_NAME,"商品名称");
                currentServiceData3.put(LIST_UUID,bean.getCategoryVo().getGoodsName());
                gattServiceData.add(currentServiceData3);

                ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                        new ArrayList<HashMap<String, String>>();
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                currentCharaData.put(
                        LIST_NAME, "执行配送数量");
                currentCharaData.put(LIST_UUID, bean.getDeliverAmount()+"");
                gattCharacteristicGroupData.add(currentCharaData);

                HashMap<String, String> currentCharaData1 = new HashMap<String, String>();
                currentCharaData1.put(
                        LIST_NAME, "储位");
                currentCharaData1.put(LIST_UUID, bean.getCategoryVo().getAreaName());
                gattCharacteristicGroupData.add(currentCharaData1);

                HashMap<String, String> currentCharaData2 = new HashMap<String, String>();
                currentCharaData2.put(
                        LIST_NAME, "规格");
                currentCharaData2.put(LIST_UUID, bean.getCategoryVo().getAttribute()+"");
                gattCharacteristicGroupData.add(currentCharaData2);


                HashMap<String, String> currentCharaData3 = new HashMap<String, String>();
                currentCharaData3.put(
                        LIST_NAME, "所属订单");
                currentCharaData3.put(LIST_UUID, bean.getOrderNumber()+"");
                gattCharacteristicGroupData.add(currentCharaData3);
                gattCharacteristicData.add(gattCharacteristicGroupData);
            }
        }

//        list.add("分拣单号:" + data.getData().getAssignOrderNumber());
//        list.add("车牌号:" + data.getData().getCarVo().getCarNumber());
//        list.add("分拣单号:"+data.getData().getAssignOrderNumber());
//        list.add("分拣单号:"+data.getData().getAssignOrderNumber());
//        list.add("分拣单号:"+data.getData().getAssignOrderNumber());
//        list.add("分拣单号:"+data.getData().getAssignOrderNumber());
//        list.add("分拣单号:"+data.getData().getAssignOrderNumber());
//        list.add("分拣单号:"+data.getData().getAssignOrderNumber());
//        list.add("分拣单号:"+data.getData().getAssignOrderNumber());
//        list.add("分拣单号:"+data.getData().getAssignOrderNumber());
//        list.add("分拣单号:"+data.getData().getAssignOrderNumber());
//        list.add("分拣单号:"+data.getData().getAssignOrderNumber());
        adapter = new SimpleExpandableListAdapter(this,
                gattServiceData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 },
                gattCharacteristicData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 });
        expandableListView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_start_assign:
                if (!start) {

                    showdialog("确定开始分拣",true);

                } else {

                    showdialog("确定结束分拣",false);
                }
                break;
            default:
                break;
        }
    }

    private void showdialog(String s, final boolean flag) {

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(s)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String parm = "assignOrderNumber=" + URLEncoder.encode(data.getData().getAssignOrderNumber(), "UTF-8") + "&phone=" + URLEncoder.encode(BaseApplication.getInstance().getPhoneNumber(), "UTF-8");
                                    HttpUtils.post("http://106.15.57.208:18080/entrance/app/mode/handleAssignOrder", parm, new HttpUtils.MyCallback() {
                                        @Override
                                        public void success(String result) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(AssignOrderDetailActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
                                                    if (flag){
                                                        start=true;
                                                        button.setText("结束分拣");
                                                    }else {
                                                        finish();
                                                    }

                                                }
                                            });
                                        }

                                        @Override
                                        public void error(Exception e) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    Toast.makeText(AssignOrderDetailActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    });
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }

                            }
                        }).start();


                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();
        dialog.show();
    }

}
