package com.carlyle.lee.yhook;

import android.content.Context;

/**
 * Lens中hook框架接口定义
 */
public interface IHookFrameWork {
    void addCustomHookPluginPath(String path);
    void usePluginMode(boolean pluginMode);
    void doHookDefault(String className);
    void setHookPluginInfo(Context context, String cacheDir, String pluginFile);
    ClassLoader getHookPluginClassLoader();
}
