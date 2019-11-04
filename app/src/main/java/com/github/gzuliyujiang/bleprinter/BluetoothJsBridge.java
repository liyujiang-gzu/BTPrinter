package com.github.gzuliyujiang.bleprinter;

import android.bluetooth.BluetoothDevice;
import android.view.View;
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
    public void showPairedDevice() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                List<BluetoothDevice> devices = BluetoothUtils.getPairedDevice();
                final List<String> addressList = new ArrayList<>();
                for (BluetoothDevice device : devices) {
                    String address = device.getAddress();
                    if (address == null) {
                        address = "";
                    }
                    addressList.add(address);
                }
                String[] a = new String[addressList.size()];
                String[] objects = addressList.toArray(a);
                AlertDialog.showList(activity, "连接蓝牙", objects, new AlertDialog.OnListListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        printPaper(addressList.get(position));
                    }

                    @Override
                    public void onCancel() {

                    }
                });
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
    public void printPaper(final String address) {
        new PrintPaperTask(activity).execute(address);
    }

}
