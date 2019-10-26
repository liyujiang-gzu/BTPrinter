package com.github.gzuliyujiang.bleprinter.utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * 蓝牙工具类
 *
 * @author liyujiang
 */
public class BluetoothUtils {

    /**
     * 蓝牙是否打开
     */
    public static boolean isBluetoothOn() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null) {
            return defaultAdapter.isEnabled();
        }
        return false;
    }

    /**
     * 获取所有已配对的设备
     */
    public static List<BluetoothDevice> getBondedDevices() {
        List<BluetoothDevice> deviceList = new ArrayList<>();
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter == null) {
            //不支持蓝牙，如安卓模拟器
            return deviceList;
        }
        Set<BluetoothDevice> bondedDevices = defaultAdapter.getBondedDevices();
        if (bondedDevices.size() > 0) {
            deviceList.addAll(bondedDevices);
        }
        return deviceList;
    }

    /**
     * 获取所有已配对的打印机
     */
    public static List<BluetoothDevice> getBondedPrinters() {
        List<BluetoothDevice> bondedDevices = getBondedDevices();
        List<BluetoothDevice> printerDevices = new ArrayList<>();
        for (BluetoothDevice device : bondedDevices) {
            int majorDeviceClass = device.getBluetoothClass().getMajorDeviceClass();
            // 关于蓝牙设备分类参考 http://stackoverflow.com/q/23273355/4242112
            if (majorDeviceClass == BluetoothClass.Device.Major.IMAGING) {
                printerDevices.add(device);
            }
        }
        return printerDevices;
    }

    /**
     * 弹出系统对话框，请求打开蓝牙
     */
    public static void openBluetooth(Activity activity) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(intent, 666);
    }

    public static BluetoothSocket connectDevice(BluetoothDevice device) {
        BluetoothSocket socket = null;
        try {
            socket = device.createRfcommSocketToServiceRecord(
                    UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            socket.connect();
            return socket;
        } catch (IOException e) {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException ignore) {
            }
        }
        return null;
    }

}