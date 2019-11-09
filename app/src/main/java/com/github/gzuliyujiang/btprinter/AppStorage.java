package com.github.gzuliyujiang.btprinter;

import com.blankj.utilcode.util.SPStaticUtils;

/**
 * Created by liyujiang on 2019/11/09 23:35
 *
 * @author 大定府羡民
 */
public class AppStorage {
    public static final String DEFAULT_URL = "file:///android_asset/index.html";
    private static final String SP_KEY = "pk.app_url";

    public static String getUrl() {
        return SPStaticUtils.getString(SP_KEY, DEFAULT_URL);
    }

    public static void saveUrl(String url) {
        SPStaticUtils.put(SP_KEY, url);
    }

}
