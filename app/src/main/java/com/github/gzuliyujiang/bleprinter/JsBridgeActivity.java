package com.github.gzuliyujiang.bleprinter;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ProcessUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.github.gzuliyujiang.logger.Logger;
import com.github.gzuliyujiang.scaffold.activity.AbsExitActivity;
import com.github.gzuliyujiang.scaffold.dialog.AlertDialog;
import com.imuxuan.floatingview.FloatingMagnetView;
import com.imuxuan.floatingview.FloatingView;
import com.imuxuan.floatingview.MagnetViewListener;

import net.posprinter.posprinterface.IMyBinder;
import net.posprinter.service.PosprinterService;

/**
 * Created by liyujiang on 2019/10/27 01:22
 *
 * @author 大定府羡民
 */
public class JsBridgeActivity extends AbsExitActivity {
    private String url;
    private WebView webView;
    private IMyBinder printerBinder;
    private Intent serviceIntent;

    public static void start(Context context) {
        Intent starter = new Intent(context, JsBridgeActivity.class);
        starter.putExtra("url", AppStorage.getUrl());
        context.startActivity(starter);
    }

    @Override
    protected void handleIntent(@NonNull Intent intent) {
        url = intent.getStringExtra("url");
    }

    @Override
    protected int specifyLayoutRes() {
        return R.layout.activity_js_bridge;
    }

    public final WebView getWebView() {
        return webView;
    }

    public final IMyBinder getPrinterBinder() {
        return printerBinder;
    }

    @Override
    public void onViewCreated(@NonNull View contentView) {
        bindPrinterService();
        initWebView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FloatingView.get().attach(this).add().icon(R.mipmap.ic_developer_mode).listener(new MagnetViewListener() {
            @Override
            public void onRemove(FloatingMagnetView magnetView) {
            }

            @Override
            public void onClick(FloatingMagnetView magnetView) {
                showSettingsDialog();
            }
        });
    }

    private void showSettingsDialog() {
        AlertDialog.showInput(activity, "设置地址", AppStorage.getUrl(), new AlertDialog.OnInputListener() {
            @Override
            public void onInput(String text) {
                if (TextUtils.isEmpty(text)) {
                    return;
                }
                if (!(text.startsWith("file") || text.startsWith("http"))) {
                    ToastUtils.showShort("地址无效");
                    return;
                }
                url = text;
                AppStorage.saveUrl(text);
                webView.loadUrl(url);
            }

            @Override
            public void onCancel() {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopService(serviceIntent);
        FloatingView.get().detach(this).remove();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private void bindPrinterService() {
        try {
            serviceIntent = new Intent(this, PosprinterService.class);
            bindService(serviceIntent, new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    printerBinder = (IMyBinder) service;
                    Logger.debug("printer service connected");
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    Logger.debug("printer service disconnected");
                }
            }, BIND_AUTO_CREATE);
        } catch (Exception e) {
            Logger.debug(e);
        }
    }

    private void initWebView() {
        webView = findViewById(R.id.webView);
        customSettings(webView);
        customWebViewClient();
        customChromeClient();
        webView.addJavascriptInterface(new BluetoothJsBridge(this), "Bluetooth");
        webView.loadUrl(url);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void customSettings(WebView webView) {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        //支持DOM儲存，解决问题: Uncaught TypeError: Cannot call method 'xxx' of null at xxx
        //参见: http://wazai.net/2969/android-webview-error-uncaught-typeerror-cannot-call-method-getitem-of-null-at
        settings.setDomStorageEnabled(true);
        settings.setAppCacheEnabled(true);
        //LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
        //LOAD_DEFAULT: 根据Cache-Control决定是否从网络上取数据。
        //LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
        //LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期或者No-Cache，都使用缓存中的数据。
        //缓存模式因为网络等因素貌似容易出现莫名其妙的问题，故默认不优先使用缓存
        if (isNetworkConnected(webView.getContext())) {
            settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        //支持定位
        settings.setGeolocationEnabled(true);
        //支持数据库
        settings.setDatabaseEnabled(true);
        //支持缩放
        settings.setSupportZoom(true);
        settings.setDisplayZoomControls(false);
        settings.setBuiltInZoomControls(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //允许http及https混合请求，参阅: http://blog.csdn.net/luofen521/article/details/51783914
            settings.setMixedContentMode(0);
        }
        //文字字号，系统默认为16，最小为8
        settings.setDefaultFontSize(16);
        settings.setMinimumFontSize(6);
        //是否自动加载网络图片
        settings.setLoadsImagesAutomatically(true);
        //是否阻断网络图片加载
        settings.setBlockNetworkImage(false);
        //是否允许通过 content:// 加载本地文件html
        settings.setAllowContentAccess(true);
        //是否允许加载本地文件html  如file:///android_asset、file:///android_res
        settings.setAllowFileAccess(true);
        //是否允许通过 file:// 加载的 Javascript 读取其他的本地文件 .建议关闭
        settings.setAllowFileAccessFromFileURLs(false);
        //是否允许通过 file:// 加载的 Javascript 可以访问其他的源，包括其他的文件和 http，https 等其他的源
        settings.setAllowUniversalAccessFromFileURLs(false);
        settings.setSupportMultipleWindows(false);
        String userAgentString = settings.getUserAgentString();
        Logger.debug("userAgentString=" + userAgentString);
        settings.setUserAgentString(userAgentString);
        //使用<meta>标签的ViewPort，适配可视窗口区
        settings.setUseWideViewPort(true);
        //是否以概览模式加载桌面版网页
        settings.setLoadWithOverviewMode(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 在开发阶段开启调试模式，将问题暴露出来。
            WebView.setWebContentsDebuggingEnabled(Logger.ENABLE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            //参阅 https://blog.csdn.net/lvshuchangyin/article/details/89446629
            String processName = ProcessUtils.getCurrentProcessName();
            Context applicationContext = webView.getContext().getApplicationContext();
            if (!applicationContext.getPackageName().equals(processName)) {
                Logger.debug("安卓9.0后不允许多进程使用同一个数据目录，设置前缀：" + processName);
                WebView.setDataDirectorySuffix(processName);
            }
        }
    }

    private boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnected();
    }

    private void customWebViewClient() {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Logger.debug("shouldOverrideUrlLoading: url=" + url);
                if (url.startsWith("file") || url.startsWith("http")) {
                    webView.loadUrl(url);
                    return true;
                }
                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    return this.shouldOverrideUrlLoading(view, request.getUrl().toString());
                }
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Logger.debug("onPageStarted: url=" + url);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Logger.debug("onPageFinished: url=" + url);
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Logger.debug("onReceivedError: url=" + url);
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });
    }

    private void customChromeClient() {
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                Logger.debug("onJsAlert: url=" + url);
                AlertDialog.show(activity, message)
                        .setButton("确定", new AlertDialog.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                result.cancel();
                            }
                        });
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                Logger.debug("onJsConfirm: url=" + url);
                AlertDialog.show(activity, message)
                        .asConfirm()
                        .setLeftButton("确定", new AlertDialog.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                result.confirm();
                            }
                        }).setRightButton("取消", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        result.cancel();
                    }
                });
                return true;
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
                Logger.debug("onJsPrompt: url=" + url);
                AlertDialog.showInput(activity, message, defaultValue, new AlertDialog.OnInputListener() {
                    @Override
                    public void onInput(String text) {
                        result.confirm(text);
                    }

                    @Override
                    public void onCancel() {
                        result.cancel();
                    }
                });
                return true;
            }

            @Override
            public void onConsoleMessage(String message, int lineNumber, String sourceID) {
                Logger.debug("onConsoleMessage: message=" + message + ", " + sourceID + "[" + lineNumber + "]");
                super.onConsoleMessage(message, lineNumber, sourceID);
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                this.onConsoleMessage(consoleMessage.message(), consoleMessage.lineNumber(), consoleMessage.sourceId());
                return true;
            }
        });
    }

}

