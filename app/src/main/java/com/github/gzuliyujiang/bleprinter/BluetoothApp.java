package com.github.gzuliyujiang.bleprinter;

import android.app.Application;

import com.github.gzuliyujiang.logger.Logger;
import com.github.gzuliyujiang.scaffold.ScaffoldApp;

/**
 * Created by liyujiang on 2019/11/03 23:37
 *
 * @author 大定府羡民
 */
public class BluetoothApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.ENABLE = BuildConfig.DEBUG;
        ScaffoldApp.initInApplication(this);
    }

}
