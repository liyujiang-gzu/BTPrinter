package com.github.gzuliyujiang.bleprinter;

import android.bluetooth.BluetoothDevice;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.github.gzuliyujiang.scaffold.browser.BrowserKit;
import com.github.gzuliyujiang.scaffold.dialog.AlertDialog;
import com.github.gzuliyujiang.scaffold.dialog.ProgressDialog;
import com.google.gson.Gson;

import net.posprinter.posprinterface.ProcessData;
import net.posprinter.posprinterface.TaskCallback;
import net.posprinter.utils.DataForSendToPrinterPos80;
import net.posprinter.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liyujiang on 2019/11/04 00:22
 *
 * @author 大定府羡民
 */
@SuppressWarnings("unused")
public class BluetoothJsBridge {
    private JsBridgeActivity activity;
    private ProgressDialog progressDialog;
    private boolean btConnected = false;

    public BluetoothJsBridge(JsBridgeActivity activity) {
        activity.getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner lifecycleOwner, @NonNull Lifecycle.Event event) {
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
    public void getPairedDevice(final String callbackName) {
        List<BluetoothDevice> devices = BluetoothUtils.getPairedDevice();
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
    public void connectPrinter(final String address, final String callbackSuccess, final String callbackFailure) {
        activity.getPrinterBinder().ConnectBtPort(address, new TaskCallback() {
            @Override
            public void OnSucceed() {
                btConnected = true;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BrowserKit.evaluateJavascript(activity.getWebView(), callbackSuccess + "()");
                    }
                });
            }

            @Override
            public void OnFailed() {
                btConnected = false;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BrowserKit.evaluateJavascript(activity.getWebView(), callbackFailure + "()");
                    }
                });
            }
        });
    }

    @JavascriptInterface
    public void disconnectPrinter() {
        if (!btConnected) {
            showToast("还未连接打印机");
            return;
        }
        activity.getPrinterBinder().DisconnectCurrentPort(new TaskCallback() {
            @Override
            public void OnSucceed() {
                btConnected = false;
                showToast("打印机连接断开成功");
            }

            @Override
            public void OnFailed() {
                showToast("打印机连接断开失败");
            }
        });
    }

    @JavascriptInterface
    public void printPaper(final String json) {
        if (!btConnected) {
            showToast("还未连接打印机");
            return;
        }
        activity.getPrinterBinder().WriteSendData(new TaskCallback() {
            @Override
            public void OnSucceed() {
                showToast("打印成功");
            }

            @Override
            public void OnFailed() {
                showToast("打印失败");
            }
        }, new ProcessData() {
            @Override
            public List<byte[]> processDataBeforeSend() {
                List<byte[]> list = new ArrayList<>();

                list.add(EscPosUtils.CUT_PAPER);
                list.add(EscPosUtils.RESET);
                list.add(EscPosUtils.LINE_SPACING_DEFAULT);
                list.add(EscPosUtils.ALIGN_CENTER);
                list.add(EscPosUtils.DOUBLE_HEIGHT_WIDTH);
                list.add(StringUtils.strTobytes("发货单\n\n"));
                list.add(EscPosUtils.NORMAL);
                list.add(EscPosUtils.ALIGN_LEFT);
                list.add(StringUtils.strTobytes(EscPosUtils.formatDividerLine()));
                list.add(EscPosUtils.BOLD);
                list.add(StringUtils.strTobytes(EscPosUtils.format3Column("商品名称", "数量", "金额\n")));
                list.add(EscPosUtils.BOLD_CANCEL);
                list.add(StringUtils.strTobytes(EscPosUtils.formatDividerLine()));
                list.add(StringUtils.strTobytes(EscPosUtils.format3Column("地方33地方", "71", "0.00\n")));
                list.add(StringUtils.strTobytes(EscPosUtils.format3Column("f的", "1", "6.00\n")));
                list.add(StringUtils.strTobytes(EscPosUtils.format3Column("人热熔头", "45", "26.00\n")));
                list.add(StringUtils.strTobytes(EscPosUtils.format3Column("一个测试", "15", "226.00\n")));
                list.add(StringUtils.strTobytes(EscPosUtils.format3Column("让他突然", "18", "2226.00\n")));
                list.add(StringUtils.strTobytes(EscPosUtils.format3Column("牛肉面啊啊啊牛肉面啊啊啊", "888", "98886.00\n")));
                list.add(StringUtils.strTobytes(EscPosUtils.formatDividerLine()));
                list.add(EscPosUtils.BOLD);
                list.add(EscPosUtils.ALIGN_CENTER);
                list.add(StringUtils.strTobytes("买家信息\n"));
                list.add(EscPosUtils.BOLD_CANCEL);
                list.add(EscPosUtils.ALIGN_LEFT);
                list.add(StringUtils.strTobytes(EscPosUtils.formatDividerLine()));
                list.add(StringUtils.strTobytes(EscPosUtils.format2Column("姓名", "穿青人\n")));
                list.add(StringUtils.strTobytes(EscPosUtils.format2Column("收货地址", "贵州省贵阳市花溪区xx都是x非得让他\n")));
                list.add(StringUtils.strTobytes(EscPosUtils.format2Column("联系方式", "5449856555556\n")));
                list.add(StringUtils.strTobytes(EscPosUtils.formatDividerLine()));
                list.add(EscPosUtils.BOLD);
                list.add(EscPosUtils.ALIGN_CENTER);
                list.add(StringUtils.strTobytes("卖家信息\n"));
                list.add(EscPosUtils.BOLD_CANCEL);
                list.add(EscPosUtils.ALIGN_LEFT);
                list.add(StringUtils.strTobytes(EscPosUtils.formatDividerLine()));
                list.add(StringUtils.strTobytes(EscPosUtils.format2Column("姓名", "张三\n")));
                list.add(StringUtils.strTobytes(EscPosUtils.format2Column("收货地址", "浙江省杭州市滨江区中威大厦11楼\n")));
                list.add(StringUtils.strTobytes(EscPosUtils.format2Column("联系方式", "5449856555556\n")));
                list.add(StringUtils.strTobytes(EscPosUtils.formatDividerLine()));
                list.add(DataForSendToPrinterPos80.setBarcodeWidth(2));
                list.add(DataForSendToPrinterPos80.setBarcodeHeight(80));
                list.add(DataForSendToPrinterPos80.printBarcode(73, 10, "{B12345678"));
                list.add(StringUtils.strTobytes("\n\n\n\n\n"));

                return list;
            }
        });
    }

    @JavascriptInterface
    public void connectAndPrintPaper(final String address) {
        new PrintPaperTask(activity).execute(address);
    }

}
