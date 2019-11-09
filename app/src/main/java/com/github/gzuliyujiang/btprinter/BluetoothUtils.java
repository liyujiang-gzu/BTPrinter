package com.github.gzuliyujiang.btprinter;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.provider.Settings;

import androidx.fragment.app.FragmentActivity;

import com.github.gzuliyujiang.toolkit.ActivityResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 蓝牙工具类，参阅 https://github.com/4dcity/BluetoothPrinterDemo
 *
 * @author liyujiang
 */
public class BluetoothUtils {

    /**
     * 蓝牙是否打开
     */
    public static boolean isBluetoothEnabled() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null) {
            return defaultAdapter.isEnabled();
        }
        return false;
    }

    public static void enableBluetooth() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter == null) {
            return;
        }
        if (!defaultAdapter.isEnabled()) {
            defaultAdapter.enable();
        }
    }

    public static void disableBluetooth() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter == null) {
            return;
        }
        if (defaultAdapter.isEnabled()) {
            defaultAdapter.disable();
        }
    }

    public static BluetoothDevice getBluetoothDevice(String address) {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter == null) {
            return null;
        }
        return defaultAdapter.getRemoteDevice(address);
    }

    /**
     * 弹出系统对话框，请求打开蓝牙
     */
    public static void requestEnableBluetooth(FragmentActivity activity, ActivityResult.Callback callback) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        ActivityResult.start(activity.getSupportFragmentManager(), intent, callback);
    }

    /**
     * 打开蓝牙设置，进行配对
     */
    public static void startBluetoothSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    /**
     * 获取所有已配对的设备
     */
    public static List<BluetoothDevice> getPairedDevice() {
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
     * 获取所有已配对的打印类设备
     */
    public static List<BluetoothDevice> getPairedPrinter() {
        return getSpecificDevice(BluetoothClass.Device.Major.IMAGING);
    }

    /**
     * 从已配对设配中，删选出某一特定类型的设备展示
     */
    public static List<BluetoothDevice> getSpecificDevice(int deviceClass) {
        List<BluetoothDevice> devices = getPairedDevice();
        List<BluetoothDevice> printerDevices = new ArrayList<>();
        for (BluetoothDevice device : devices) {
            BluetoothClass klass = device.getBluetoothClass();
            // 关于蓝牙设备分类参考 http://stackoverflow.com/q/23273355/4242112
            if (klass.getMajorDeviceClass() == deviceClass)
                printerDevices.add(device);
        }
        return printerDevices;
    }

}