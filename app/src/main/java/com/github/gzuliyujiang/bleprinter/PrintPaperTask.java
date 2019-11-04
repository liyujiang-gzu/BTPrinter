package com.github.gzuliyujiang.bleprinter;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;

import com.blankj.utilcode.util.ToastUtils;
import com.github.gzuliyujiang.logger.Logger;
import com.github.gzuliyujiang.scaffold.dialog.ProgressDialog;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by liyujiang on 2019/11/04 07:04
 *
 * @author 大定府羡民
 */
public class PrintPaperTask extends AsyncTask<String, Integer, Void> {
    private WeakReference<Activity> activityWeakReference;
    private ProgressDialog progressDialog;

    public PrintPaperTask(Activity activity) {
        activityWeakReference = new WeakReference<>(activity);
    }

    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(activityWeakReference.get(), "请稍候");
    }

    @Override
    protected Void doInBackground(String... params) {
        if (!BluetoothUtils.isBluetoothEnabled()) {
            BluetoothUtils.enableBluetooth();
        }
        EscPosUtils.tryClose();
        BluetoothSocket socket = null;
        try {
            BluetoothDevice device = BluetoothUtils.getBluetoothDevice(params[0]);
            if (device == null) {
                publishProgress(-3);
                return null;
            }
            try {
                socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                socket.connect();
            } catch (IOException e) {
                Logger.debug(e);
                try {
                    // See https://blog.csdn.net/vic_torsun/article/details/79650865
                    @SuppressWarnings({"RedundantArrayCreation", "SpellCheckingInspection", "JavaReflectionMemberAccess"})
                    Method m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                    socket = (BluetoothSocket) m.invoke(device, 1);
                    socket.connect();
                } catch (Exception e1) {
                    Logger.debug(e1);
                }
            }
            if (socket != null && socket.isConnected()) {
                EscPosUtils.setOutputStream(socket.getOutputStream());
                EscPosUtils.printTest();
                EscPosUtils.tryClose();
                publishProgress(0);
            } else {
                publishProgress(-2);
            }
        } catch (IOException e) {
            Logger.debug(e);
            publishProgress(-1);
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException ignore) {
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (values[0] == 0) {
            ToastUtils.showShort("打印成功");
        } else {
            ToastUtils.showShort("打印失败");
        }
    }

    @Override
    protected void onPostExecute(Void result) {
        progressDialog.dismiss();
    }

}
