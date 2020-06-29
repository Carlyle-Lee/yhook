package com.carlyle.lee.yhook;

import android.content.Context;

import com.carl.hook.utils.HookWrapper;

/**
 * HookFrameworkImpl 参考实现 设置后 lens 支持hook 功能；
 * 设置方式 ：LensUtil.buildConfig().setHookFrameWorkImpl(new HookFramework())
 * 注： 也可以用其他的开源协议更宽松的 hook 框架实现接口。
 */
public class HookFrameworkImpl implements IHookFrameWork {
    @Override
    public void addCustomHookPluginPath(String path) {
        HookWrapper.addCustomHookPluginPath(path);
    }

    @Override
    public void usePluginMode(boolean pluginMode) {
        HookWrapper.usePluginMode(pluginMode);
    }

    @Override
    public void doHookDefault(String className) {
        HookWrapper.doHookDefault(className);
    }

    @Override
    public void setHookPluginInfo(Context context, String cacheDir, String pluginFile) {
        HookWrapper.setHookPluginInfo(context, cacheDir, pluginFile);
    }

    @Override
    public ClassLoader getHookPluginClassLoader() {
        return HookWrapper.getPluginClassLoader();
    }
}
