package com.github.gzuliyujiang.btprinter;

import com.github.gzuliyujiang.scaffold.activity.AbsSplashActivity;

import java.util.List;

/**
 * 闪屏启动页
 *
 * @author liyujiang
 */
public class SplashActivity extends AbsSplashActivity {

    @Override
    protected boolean goToNextActivity(List<String> deniedPermissions) {
        JsBridgeActivity.start(this);
        return true;
    }

}
