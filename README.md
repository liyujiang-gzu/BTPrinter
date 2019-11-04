# JS调用原生方法

显示消息短提示:
```javascript
Bluetooth.showToast('提示语');
```
显示消息对话框:
```javascript
Bluetooth.showAlert('提示语');
```
显示/关闭等待对话框:
```javascript
Bluetooth.showProgress('提示语');
Bluetooth.closeProgress();
```
检查蓝牙是否开启:
```javascript
if (Bluetooth.isBluetoothEnabled()) {
    Bluetooth.showToast('蓝牙已打开');
} else {
    Bluetooth.showToast('蓝牙未打开');
}
```
开启蓝牙:
```javascript
Bluetooth.enableBluetooth();
```
关闭蓝牙:
```javascript
Bluetooth.disableBluetooth();
```
打开蓝牙设置(配对):
```javascript
Bluetooth.openBluetoothSettings();
```
获取已配对蓝牙设备:
```javascript
Bluetooth.getPairedDevice('getPairedDeviceCallback');
function getPairedDeviceCallback(devices) {
    Bluetooth.showAlert(JSON.stringify(devices));
}
```
打印测试:
```javascript
Bluetooth.printPaper('E4:F8:9C:66:5A:B6');
```
