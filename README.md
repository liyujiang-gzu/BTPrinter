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
var json = Bluetooth.getPairedDevice();
Bluetooth.showAlert(json);
```
```text
[{"address":"DC:0D:30:60:A1:F3","name":"Printer001"},{"address":"54:DC:1D:57:7D:57","name":"cool1 dual"},{"address":"E4:F8:9C:66:5A:B6","name":"LIYUJIANG"}]
```
连接蓝牙打印机:
```javascript
Bluetooth.connectPrinter('DC:0D:30:60:A1:F3');
```
打印测试:
```javascript
var json = '{}';
Bluetooth.printPaper(json);
```
