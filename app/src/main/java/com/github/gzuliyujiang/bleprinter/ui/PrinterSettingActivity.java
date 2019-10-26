package com.github.gzuliyujiang.bleprinter.ui;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;
import com.github.gzuliyujiang.bleprinter.R;
import com.github.gzuliyujiang.bleprinter.utils.BluetoothUtils;
import com.github.gzuliyujiang.bleprinter.utils.PrintUtil;

import java.util.List;

public class PrinterSettingActivity extends BasePrintActivity implements View.OnClickListener {

    ListView mLvPairedDevices;
    Button mBtnJsBridge;
    Button mBtnSetting;
    Button mBtnTest;
    Button mBtnPrint;

    DeviceListAdapter mAdapter;
    int mSelectedPosition = -1;

    final static int TASK_TYPE_CONNECT = 1;
    final static int TASK_TYPE_PRINT = 2;

    public static void start(Context context) {
        Intent starter = new Intent(context, PrinterSettingActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer_setting);
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fillAdapter();
    }

    private void initViews() {
        mLvPairedDevices = findViewById(R.id.lv_paired_devices);
        mBtnJsBridge = findViewById(R.id.btn_js_bridge);
        mBtnSetting = findViewById(R.id.btn_goto_setting);
        mBtnTest = findViewById(R.id.btn_test_conntect);
        mBtnPrint = findViewById(R.id.btn_print);

        mLvPairedDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedPosition = position;
                mAdapter.notifyDataSetChanged();
            }
        });

        mBtnJsBridge.setOnClickListener(this);
        mBtnSetting.setOnClickListener(this);
        mBtnTest.setOnClickListener(this);
        mBtnPrint.setOnClickListener(this);

        mAdapter = new DeviceListAdapter(this);
        mLvPairedDevices.setAdapter(mAdapter);
    }

    /**
     * 从所有已配对设备中找出打印设备并显示
     */
    private void fillAdapter() {
        //推荐使用 BluetoothUtils.getPairedPrinterDevices()
        List<BluetoothDevice> printerDevices = BluetoothUtils.getBondedDevices();
        mAdapter.clear();
        mAdapter.addAll(printerDevices);
        refreshButtonText(printerDevices);
    }

    private void refreshButtonText(List<BluetoothDevice> printerDevices) {
        if (printerDevices.size() > 0) {
            mBtnSetting.setText("配对更多设备");
        } else {
            mBtnSetting.setText("还未配对打印机，去设置");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_js_bridge:
                JsBridgeActivity.start(this, "http://qqtheme.cn");
                break;
            case R.id.btn_goto_setting:
                try {
                    startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                } catch (Exception e) {
                    ToastUtils.showShort("无法打开蓝牙设置");
                }
                break;
            case R.id.btn_test_conntect:
                connectDevice(TASK_TYPE_CONNECT);
                break;
            case R.id.btn_print:
                connectDevice(TASK_TYPE_PRINT);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }

    private void connectDevice(int taskType) {
        if (mSelectedPosition >= 0) {
            BluetoothDevice device = mAdapter.getItem(mSelectedPosition);
            if (device != null) {
                super.connectDevice(device, taskType);
            }
        } else {
            Toast.makeText(this, "还未选择打印设备", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnected(BluetoothSocket socket, int taskType) {
        if (taskType == TASK_TYPE_PRINT) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            PrintUtil.printTest(socket, bitmap);
        }
    }


    class DeviceListAdapter extends ArrayAdapter<BluetoothDevice> {

        public DeviceListAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            BluetoothDevice device = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_bluetooth_device, parent, false);
            }

            TextView tvDeviceName = (TextView) convertView.findViewById(R.id.tv_device_name);
            CheckBox cbDevice = (CheckBox) convertView.findViewById(R.id.cb_device);

            tvDeviceName.setText(device.getName());

            cbDevice.setChecked(position == mSelectedPosition);

            return convertView;
        }
    }
}
