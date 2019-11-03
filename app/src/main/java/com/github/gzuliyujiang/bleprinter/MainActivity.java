package com.github.gzuliyujiang.bleprinter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.github.gzuliyujiang.scaffold.activity.AbsExitActivity;

/**
 * 主页
 *
 * @author liyujiang
 */
public class MainActivity extends AbsExitActivity {
    private EditText mEdtJsBridgeUrl;

    public static void start(Context context) {
        Intent starter = new Intent(context, MainActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int specifyLayoutRes() {
        return R.layout.activity_printer_setting;
    }

    @Override
    public void onViewCreated(@NonNull View contentView) {
        mEdtJsBridgeUrl = findViewById(R.id.edt_js_bridge_url);
        findViewById(R.id.btn_js_bridge_go).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_js_bridge_go) {
            String url = mEdtJsBridgeUrl.getText().toString();
            if (!(url.startsWith("file") || url.startsWith("http"))) {
                Toast.makeText(this, "地址无效", Toast.LENGTH_SHORT).show();
                return;
            }
            JsBridgeActivity.start(this, url);
        }
    }

}
