package com.github.gzuliyujiang.bleprinter;

import android.bluetooth.BluetoothDevice;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.github.gzuliyujiang.logger.Logger;
import com.github.gzuliyujiang.scaffold.browser.BrowserKit;
import com.github.gzuliyujiang.scaffold.dialog.AlertDialog;
import com.github.gzuliyujiang.scaffold.dialog.ProgressDialog;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import top.wuhaojie.bthelper.BtHelperClient;
import top.wuhaojie.bthelper.MessageItem;
import top.wuhaojie.bthelper.OnSearchDeviceListener;
import top.wuhaojie.bthelper.OnSendMessageListener;

/**
 * Created by liyujiang on 2019/11/04 00:22
 *
 * @author 大定府羡民
 */
@SuppressWarnings("unused")
public class BluetoothJsBridge {
    private JsBridgeActivity activity;
    private ProgressDialog progressDialog;
    private BtHelperClient btHelperClient;

    public BluetoothJsBridge(JsBridgeActivity activity) {
        btHelperClient = BtHelperClient.from(activity);
        activity.getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner lifecycleOwner, @NonNull Lifecycle.Event event) {
                btHelperClient.close();
                lifecycleOwner.getLifecycle().removeObserver(this);
            }
        });
        this.activity = activity;
    }

    @JavascriptInterface
    public void showToast(final String msg) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, msg == null ? "" : msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @JavascriptInterface
    public void showAlert(final String msg) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.show(activity, msg == null ? "" : msg);
            }
        });
    }

    @JavascriptInterface
    public void showProgress(final String msg) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                progressDialog = ProgressDialog.show(activity, msg == null ? "" : msg);
            }
        });
    }

    @JavascriptInterface
    public void changeProgressText(final String msg) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.setText(msg);
                }
            }
        });
    }

    @JavascriptInterface
    public void closeProgress() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            }
        });
    }

    @JavascriptInterface
    public boolean isBluetoothEnabled() {
        return BluetoothUtils.isBluetoothEnabled();
    }

    @JavascriptInterface
    public void enableBluetooth() {
        BluetoothUtils.enableBluetooth();
    }

    @JavascriptInterface
    public void disableBluetooth() {
        BluetoothUtils.disableBluetooth();
    }

    @JavascriptInterface
    public void openBluetoothSettings() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BluetoothUtils.startBluetoothSettings(activity);
            }
        });
    }

    @JavascriptInterface
    public void getBondedDevice(final String callbackName) {
        List<BluetoothDevice> devices = BluetoothUtils.getBondedDevices();
        List<BluetoothEntity> entities = new ArrayList<>();
        for (BluetoothDevice device : devices) {
            BluetoothEntity entity = new BluetoothEntity();
            String name = device.getName();
            if (name == null) {
                name = "";
            }
            entity.setName(name);
            entity.setAddress(device.getAddress());
            entities.add(entity);
        }
        final String json = new Gson().toJson(entities);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BrowserKit.evaluateJavascript(activity.getWebView(), callbackName + "(" + json + ")");
            }
        });
    }

    @JavascriptInterface
    public void scanBluetooth(final String callbackStart, final String callbackFound, final String callbackStop) {
        if (!isBluetoothEnabled()) {
            enableBluetooth();
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btHelperClient.searchDevices(new OnSearchDeviceListener() {
                    @Override
                    public void onStartDiscovery() {
                        Logger.debug("start scan");
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                BrowserKit.evaluateJavascript(activity.getWebView(), callbackStart + "()");
                            }
                        });
                    }

                    @Override
                    public void onNewDeviceFounded(final BluetoothDevice device) {
                        Logger.debug("scan: " + device);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                BluetoothEntity entity = new BluetoothEntity();
                                String name = device.getName();
                                if (name == null) {
                                    name = "";
                                }
                                entity.setName(name);
                                entity.setAddress(device.getAddress());
                                final String json = new Gson().toJson(entity);
                                BrowserKit.evaluateJavascript(activity.getWebView(), callbackFound + "(" + json + ")");
                            }
                        });
                    }

                    @Override
                    public void onSearchCompleted(List<BluetoothDevice> bondedList, List<BluetoothDevice> newList) {
                        Logger.debug("stop scan");
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                BrowserKit.evaluateJavascript(activity.getWebView(), callbackStop + "()");
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        Logger.debug("stop scan: " + e);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                BrowserKit.evaluateJavascript(activity.getWebView(), callbackStop + "()");
                            }
                        });
                    }
                });
            }
        });
    }

    private int printErrorCount = 0;

    @JavascriptInterface
    public void printTest(final String address, final String callbackSuccess, final String callbackError) {
        if (!isBluetoothEnabled()) {
            enableBluetooth();
        }
        printErrorCount = 0;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sendData(address, EscPosUtils.RESET);
                sendData(address, EscPosUtils.LINE_SPACING_DEFAULT);
                sendData(address, EscPosUtils.ALIGN_CENTER);
                sendData(address, "饭店餐馆名称\n\n");
                sendData(address, EscPosUtils.DOUBLE_HEIGHT_WIDTH);
                sendData(address, "桌号：1号桌\n\n");
                sendData(address, EscPosUtils.NORMAL);
                sendData(address, EscPosUtils.ALIGN_LEFT);
                sendData(address, EscPosUtils.format2Column("订单编号", "201507161515\n"));
                sendData(address, EscPosUtils.format2Column("点菜时间", "2016-02-16 10:46\n"));
                sendData(address, EscPosUtils.format2Column("上菜时间", "2016-02-16 11:46\n"));
                sendData(address, EscPosUtils.format2Column("人数：2人", "收银员：张三\n"));
                sendData(address, "--------------------------------\n");
                sendData(address, EscPosUtils.BOLD);
                sendData(address, EscPosUtils.format3Column("项目", "数量", "金额\n"));
                sendData(address, "--------------------------------\n");
                sendData(address, EscPosUtils.BOLD_CANCEL);
                sendData(address, EscPosUtils.format3Column("面", "1", "0.00\n"));
                sendData(address, EscPosUtils.format3Column("米饭", "1", "6.00\n"));
                sendData(address, EscPosUtils.format3Column("铁板烧", "1", "26.00\n"));
                sendData(address, EscPosUtils.format3Column("一个测试", "1", "226.00\n"));
                sendData(address, EscPosUtils.format3Column("牛肉面啊啊", "1", "2226.00\n"));
                sendData(address, EscPosUtils.format3Column("牛肉面啊啊啊牛肉面啊啊啊", "888", "98886.00\n"));
                sendData(address, "--------------------------------\n");
                sendData(address, EscPosUtils.format2Column("合计", "53.50\n"));
                sendData(address, EscPosUtils.format2Column("抹零", "3.50\n"));
                sendData(address, "--------------------------------\n");
                sendData(address, EscPosUtils.format2Column("应收", "50.00\n"));
                sendData(address, "--------------------------------\n");
                sendData(address, EscPosUtils.ALIGN_LEFT);
                sendData(address, "备注：不要辣、不要香菜");
                sendData(address, "\n\n\n\n\n");
                if (printErrorCount == 0) {
                    BrowserKit.evaluateJavascript(activity.getWebView(), callbackSuccess + "()");
                } else {
                    BrowserKit.evaluateJavascript(activity.getWebView(), callbackError + "()");
                }
            }
        });
    }

    private void sendData(final String address, final String data) {
        sendMessage(address, new MessageItem(data));
    }

    private void sendData(final String address, final byte[] data) {
        char[] chars = EscPosUtils.toChars(data);
        sendMessage(address, new MessageItem(chars));
    }

    private void sendMessage(String address, MessageItem item) {
        btHelperClient.sendMessage(address, item, new OnSendMessageListener() {
            @Override
            public void onSuccess(int status, String response) {
                Logger.debug("发送数据成功：status=" + status);
            }

            @Override
            public void onConnectionLost(Exception e) {
                Logger.debug("发送数据失败：" + e);
                printErrorCount++;
            }

            @Override
            public void onError(Exception e) {
                Logger.debug("发送数据失败：" + e);
                printErrorCount++;
            }
        });
    }

}
