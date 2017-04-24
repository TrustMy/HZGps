package com.sy.hzadministrator;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sy.hzadministrator.bean.RequestDataBean;
import com.sy.hzadministrator.dialog.DialogTool;
import com.sy.hzadministrator.historyrecord.HistoryRecordActivity;
import com.sy.hzadministrator.db.DBHelperLH;
import com.sy.hzadministrator.db.DBManagerLH;
import com.sy.hzadministrator.db.QrBean;
import com.sy.hzadministrator.request.PostRequest;
import com.zxing.activity.CaptureActivity;

import java.util.Map;
import java.util.WeakHashMap;

public class MainActivity extends BaseActivity {
    private static final int SCAN_PIC = 1;
    private DBHelperLH dbHelperLH;
    private DBManagerLH dbManagerLH;
    private Gson gson;
    private PostRequest postRequest;

    private Map<String, Object> daMap;
    private QrBean mQrBean;
    private Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Config.ORDER:
                    if (msg.arg1 == Config.RESULT_SUCCESS) {
                        RequestDataBean bean = (RequestDataBean) msg.obj;
                        if (bean.getConfirmStatus()) {
                            T.showToast(MainActivity.this, "提交成功,订单有效!");
                            saveData(mQrBean, Config.SAVE_STATUS_SUCCESS);

                        } else {
                            T.showToast(MainActivity.this, "未找到相应订单,请确保司机上传!");
                        }

                    } else {
                        T.showToast(MainActivity.this, msg.obj.toString());
                        saveData(mQrBean, Config.SAVE_STATUS_ERROR);
                    }
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initView();
    }

    private void init() {
        postRequest = new PostRequest(MainActivity.this, mainHandler);
    }

    private void initView() {
        dbHelperLH = new DBHelperLH(this);
        dbHelperLH.getWritableDatabase();
        dbManagerLH = new DBManagerLH(this);
        gson = new Gson();
    }

    /**
     * 扫一扫
     * @param v
     */
    public void startQr(View v) {
        Intent intent3 = new Intent(MainActivity.this, CaptureActivity.class);
        startActivityForResult(intent3, SCAN_PIC);
    }

    /**
     * 历史订单
     * @param v
     */
    public void foundQr(View v) {
        startActivity(new Intent(MainActivity.this, HistoryRecordActivity.class));
    }

    /**
     * 扫面结果
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SCAN_PIC:
                    final String resultq = data.getExtras().getString("result");
//                    Toast.makeText(MainActivity.this, "解析结果：SCAN_PIC" + resultq, Toast.LENGTH_LONG).show();

                    try {
                        final QrBean qrBean = gson.fromJson(resultq, QrBean.class);
                        mQrBean = qrBean;
                        if (qrBean != null) {
//

                            Log.d("lhh", "onActivityResult  SCAN_PIC: " + resultq.toString());


                            DialogTool.showDialog(this, R.layout.dialog_qr, qrBean);

                            DialogTool.onClick = new DialogTool.click() {
                                @Override
                                public void onClick() {
                                    T.showToast(MainActivity.this,"提交订单中...");
                                    setHttpData(qrBean);

                                }
                            };
                        }
                    } catch (JsonSyntaxException s) {
                        Toast.makeText(MainActivity.this, "扫描失败请重试!", Toast.LENGTH_SHORT).show();
                    }

                    break;
            }
        }
    }

    /**
     * 保存数据库
     * @param qrBean
     * @param status
     */
    private void saveData(QrBean qrBean, int status) {
        daMap = new WeakHashMap<String, Object>();
        daMap.put("order", qrBean.getOrder());
        daMap.put("termId", qrBean.getTermId());
        daMap.put("startName", qrBean.getStartName());
        daMap.put("endName", qrBean.getEndName());
        daMap.put("time", qrBean.getTime());
        daMap.put("startTime", qrBean.getStartTime());
        daMap.put("endTime", qrBean.getEndTime());
        daMap.put("status", status);
        daMap.put("generatePictureTime", qrBean.getGeneratePictureTime());
        daMap.put("workTime", TimeTool.getSystemTime());
        dbManagerLH.add(daMap);
    }

    /**
     * 保存网络数据库
     * @param qrBean
     */
    private void setHttpData(QrBean qrBean) {
        Map<String, Object> map = new WeakHashMap<>();
        map.put("orderNo", qrBean.getOrder());
        map.put("driverId", qrBean.getTermId());
        map.put("startAddress", qrBean.getStartName());
        map.put("startTime", qrBean.getStartTime());
        map.put("endAddress", qrBean.getEndName());
        map.put("endTime", qrBean.getEndTime());
        map.put("generatePictureTime", qrBean.getGeneratePictureTime());
        map.put("permission", 1);
        postRequest.requestOrder(Server.Server + Server.Order, map, Config.ORDER);
    }
}
