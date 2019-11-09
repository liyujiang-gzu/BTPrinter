package net.printer.printdemo16;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.posprinter.posprinterface.IMyBinder;
import net.posprinter.posprinterface.TaskCallback;
import net.posprinter.service.PosprinterService;
import net.posprinter.utils.PosPrinterDev;
import net.printer.printdemo16.ReceiptPrinter.R58Activity;
import net.printer.printdemo16.ReceiptPrinter.R80Activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener{

    public static IMyBinder myBinder;

    ServiceConnection mSerconnection= new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder= (IMyBinder) service;
            Log.e("myBinder","connect");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("myBinder","disconnect");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //bind service，get imyBinder
        Intent intent =new Intent(this, PosprinterService.class);
        bindService(intent,mSerconnection,BIND_AUTO_CREATE);

        initView();
    }

    private Spinner port;
    private TextView adrress;
    private EditText ip_adrress;
    private Button connect,disconnect,pos58,pos80,tsc80,other;
    private int portType=0;//0是网络，1是蓝牙，2是USB
    public static boolean ISCONNECT=false;

    private void initView(){
        port=findViewById(R.id.sp_port);
        adrress = findViewById(R.id.tv_address);
        ip_adrress = findViewById(R.id.et_address);
        connect = findViewById(R.id.connect);
        disconnect = findViewById(R.id.disconnect);
        pos58 = findViewById(R.id.bt_pos58);
        pos80 = findViewById(R.id.bt_pos80);
//        tsc58 = findViewById(R.id.bt_tsc58);
        tsc80 = findViewById(R.id.bt_tsc80);
        other = findViewById(R.id.bt_other);

        port.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                portType=i;
                switch (i){
                    case 0:
                       ip_adrress.setVisibility(View.VISIBLE);
                       adrress.setVisibility(View.GONE);
                       break;
                    case 1:
                        ip_adrress.setVisibility(View.GONE);
                        adrress.setVisibility(View.VISIBLE);
                        adrress.setText("");
                        break;
                    case 2:
                        ip_adrress.setVisibility(View.GONE);
                        adrress.setVisibility(View.VISIBLE);
                        adrress.setText("");
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        connect.setOnClickListener(this);
        disconnect.setOnClickListener(this);
        pos58.setOnClickListener(this);
        pos80.setOnClickListener(this);
        tsc80.setOnClickListener(this);
//        tsc58.setOnClickListener(this);
        adrress.setOnClickListener(this);
        other.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id== R.id.connect){
            switch (portType){
                case 0:
                    connectNet();
                    break;
                case 1:
                    connectBT();
                    break;
                case 2:
                    connectUSB();
                    break;
            }
        }

        if (id == R.id.disconnect){
            disConnect();
        }

        if (id== R.id.tv_address){

            switch (portType){
                case 1:
                   setBluetooth();
                   break;
                case 2:
                    setUSB();
                    break;

            }
        }

        if(id == R.id.bt_pos80){
            if (ISCONNECT){
                Intent intent = new Intent(this, R80Activity.class);
                startActivity(intent);
            }else {
                Toast.makeText(getApplicationContext(),getString(R.string.connect_first),Toast.LENGTH_SHORT).show();
            }

        }

        if (id == R.id.bt_pos58){
            if (ISCONNECT){
                Intent intent = new Intent(this, R58Activity.class);
                startActivity(intent);
            }else {
                Toast.makeText(getApplicationContext(),getString(R.string.connect_first),Toast.LENGTH_SHORT).show();
            }
        }

        if (id == R.id.bt_tsc80){
            if (ISCONNECT){
                Intent intent = new Intent(this, TscActivity.class);
                startActivity(intent);
            }else {
                Toast.makeText(getApplicationContext(),getString(R.string.connect_first),Toast.LENGTH_SHORT).show();
            }
        }

        if (id == R.id.bt_other){
            if (ISCONNECT){
                Intent intent = new Intent(this, OtherActivity.class);
                startActivity(intent);
            }else {
                Toast.makeText(getApplicationContext(),getString(R.string.connect_first),Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 网络连接
     */
    private void connectNet(){
        String ip = ip_adrress.getText().toString();
        if (ip!=null||ISCONNECT==false){
            myBinder.ConnectNetPort(ip, 9100, new TaskCallback() {
                @Override
                public void OnSucceed() {
                    ISCONNECT = true;
                    Toast.makeText(getApplicationContext(),getString(R.string.con_success),Toast.LENGTH_SHORT).show();
                }

                @Override
                public void OnFailed() {
                    ISCONNECT = false;
                    Toast.makeText(getApplicationContext(),getString(R.string.con_failed),Toast.LENGTH_SHORT).show();
                }
            });

        }else {
            Toast.makeText(getApplicationContext(),getString(R.string.con_failed),Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 连接蓝牙
     */
    private void connectBT(){
        String BtAdress=adrress.getText().toString().trim();
        if (BtAdress.equals(null)||BtAdress.equals("")){
            Toast.makeText(getApplicationContext(),getString(R.string.con_failed),Toast.LENGTH_SHORT).show();
        }else {
            myBinder.ConnectBtPort(BtAdress, new TaskCallback() {
                @Override
                public void OnSucceed() {
                    ISCONNECT=true;
                    Toast.makeText(getApplicationContext(),getString(R.string.con_success),Toast.LENGTH_SHORT).show();
                }

                @Override
                public void OnFailed() {
                    ISCONNECT=false;
                    Toast.makeText(getApplicationContext(),getString(R.string.con_failed),Toast.LENGTH_SHORT).show();
                }
            } );
        }
    }

    /**
     * 连接usb
     */
    private void connectUSB(){
        String usbAddress = adrress.getText().toString().trim();
        if (usbAddress.equals(null)||usbAddress.equals("")){
            Toast.makeText(getApplicationContext(),getString(R.string.discon),Toast.LENGTH_SHORT).show();
        }else {
            myBinder.ConnectUsbPort(getApplicationContext(), usbAddress, new TaskCallback() {
                @Override
                public void OnSucceed() {
                    ISCONNECT = true;
                    Toast.makeText(getApplicationContext(),getString(R.string.connect),Toast.LENGTH_SHORT).show();
                }

                @Override
                public void OnFailed() {
                    ISCONNECT = false;
                    Toast.makeText(getApplicationContext(),getString(R.string.discon),Toast.LENGTH_SHORT).show();
                }
            } );
        }
    }

    /**
     * 断开连接
     */
    private void disConnect(){
        if (ISCONNECT){
            myBinder.DisconnectCurrentPort(new TaskCallback() {
                @Override
                public void OnSucceed() {
                    ISCONNECT = false;
                    Toast.makeText(getApplicationContext(),"disconnect ok",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void OnFailed() {
                    ISCONNECT = true;
                    Toast.makeText(getApplicationContext(),"disconnect failed",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private List<String> btList = new ArrayList<>();
    private ArrayList<String> btFoundList = new ArrayList<>();
    private ArrayAdapter<String> BtBoudAdapter ,BtfoundAdapter;
    private View BtDialogView;
    private ListView BtBoundLv,BtFoundLv;
    private LinearLayout ll_BtFound;
    private AlertDialog btdialog;
    private Button btScan;
    private DeviceReceiver BtReciever;
    private BluetoothAdapter bluetoothAdapter;

    /*
    选择蓝牙设备
     */

    public void setBluetooth(){
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        //判断时候打开蓝牙设备
        if (!bluetoothAdapter.isEnabled()){
            //请求用户开启
            Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, 1);
        }else {

            showblueboothlist();

        }
    }

    private void showblueboothlist() {
        if (!bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.startDiscovery();
        }
        LayoutInflater inflater=LayoutInflater.from(this);
        BtDialogView=inflater.inflate(R.layout.printer_list, null);
        BtBoudAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, btList);
        BtBoundLv= BtDialogView.findViewById(R.id.listView1);
        btScan= BtDialogView.findViewById(R.id.btn_scan);
        ll_BtFound= BtDialogView.findViewById(R.id.ll1);
        BtFoundLv=(ListView) BtDialogView.findViewById(R.id.listView2);
        BtfoundAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, btFoundList);
        BtBoundLv.setAdapter(BtBoudAdapter);
        BtFoundLv.setAdapter(BtfoundAdapter);
        btdialog=new AlertDialog.Builder(this).setTitle("BLE").setView(BtDialogView).create();
        btdialog.show();

        BtReciever=new DeviceReceiver(btFoundList,BtfoundAdapter,BtFoundLv);

        //注册蓝牙广播接收者
        IntentFilter filterStart=new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter filterEnd=new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(BtReciever, filterStart);
        registerReceiver(BtReciever, filterEnd);

        setDlistener();
        findAvalibleDevice();
    }
    private void setDlistener() {
        // TODO Auto-generated method stub
        btScan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ll_BtFound.setVisibility(View.VISIBLE);
                //btn_scan.setVisibility(View.GONE);
            }
        });
        //已配对的设备的点击连接
        BtBoundLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                try {
                    if(bluetoothAdapter!=null&&bluetoothAdapter.isDiscovering()){
                        bluetoothAdapter.cancelDiscovery();

                    }

                    String mac=btList.get(arg2);
                    mac=mac.substring(mac.length()-17);
//                    String name=msg.substring(0, msg.length()-18);
                    //lv1.setSelection(arg2);
                    btdialog.cancel();
                    adrress.setText(mac);
                    //Log.i("TAG", "mac="+mac);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        //未配对的设备，点击，配对，再连接
        BtFoundLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                try {
                    if(bluetoothAdapter!=null&&bluetoothAdapter.isDiscovering()){
                        bluetoothAdapter.cancelDiscovery();

                    }
                    String mac;
                    String msg=btFoundList.get(arg2);
                    mac=msg.substring(msg.length()-17);
                    String name=msg.substring(0, msg.length()-18);
                    //lv2.setSelection(arg2);
                    btdialog.cancel();
                    adrress.setText(mac);
                    Log.i("TAG", "mac="+mac);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    /*
    找可连接的蓝牙设备
     */
    private void findAvalibleDevice() {
        // TODO Auto-generated method stub
        //获取可配对蓝牙设备
        Set<BluetoothDevice> device=bluetoothAdapter.getBondedDevices();

        btList.clear();
        if(bluetoothAdapter!=null&&bluetoothAdapter.isDiscovering()){
            BtBoudAdapter.notifyDataSetChanged();
        }
        if(device.size()>0){
            //存在已经配对过的蓝牙设备
            for(Iterator<BluetoothDevice> it = device.iterator(); it.hasNext();){
                BluetoothDevice btd=it.next();
                btList.add(btd.getName()+'\n'+btd.getAddress());
                BtBoudAdapter.notifyDataSetChanged();
            }
        }else{  //不存在已经配对过的蓝牙设备
            btList.add("No can be matched to use bluetooth");
            BtBoudAdapter.notifyDataSetChanged();
        }

    }



    View dialogView3;
    private TextView tv_usb;
    private List<String> usbList,usblist;
    private ListView lv_usb;
    private ArrayAdapter<String> adapter3;

    /*
    uSB连接
     */
    private void setUSB(){
        LayoutInflater inflater=LayoutInflater.from(this);
        dialogView3=inflater.inflate(R.layout.usb_link,null);
        tv_usb= (TextView) dialogView3.findViewById(R.id.textView1);
        lv_usb= (ListView) dialogView3.findViewById(R.id.listView1);


        usbList= PosPrinterDev.GetUsbPathNames(this);
        if (usbList==null){
            usbList=new ArrayList<>();
        }
        usblist=usbList;
        tv_usb.setText(getString(R.string.usb_pre_con)+usbList.size());
        adapter3=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,usbList);
        lv_usb.setAdapter(adapter3);


        AlertDialog dialog=new AlertDialog.Builder(this)
                .setView(dialogView3).create();
        dialog.show();

        setUsbLisener(dialog);

    }
    String usbDev="";
    public void setUsbLisener(final AlertDialog dialog) {

        lv_usb.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                usbDev=usbList.get(i);
                adrress.setText(usbDev);
                dialog.cancel();
                Log.e("usbDev: ",usbDev);
            }
        });



    }

}
