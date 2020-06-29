package com.carl.hook.utils;

import android.content.Context;
import android.os.Build;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import dalvik.system.DexClassLoader;
import lab.galaxy.yahfa.HookClassLoader;
import lab.galaxy.yahfa.HookMain;
import lab.galaxy.yahfa.LL;

/**
 * 封装加载hook 插件写出，加载等的相关逻辑；
 */
public class HookWrapper {
    // 支持自定义的hook plugin 列表
    private static List<String> hookPluginPaths = new LinkedList<>();
    private static String cacheDir;
    private static String pluginFile;
    private static Context mContext;// app context
    private static boolean failed;
    private static final String TAG = "Lens-HookWrapper";
    private static volatile boolean inited;
    private static DexClassLoader dexClassLoader;
    private static ClassLoader thisClassLoader;
    //[无需严格同步]
    private static LinkedList<String> hookList = new LinkedList<>();
    private static final int PLUGIN_VERSION = 19;
    private static boolean usePluginMode = true;


    /**
     * 设置自定义的插件地址
     */
    public static void addCustomHookPluginPath(String path) {
        if (path != null && path.length() > 0 && new File(path).exists()) {
            hookPluginPaths.remove(path);
            hookPluginPaths.add(path);
        }
    }

    /**
     * for lens-oop plug : set to false;
     * for lens sdk reliance set to true
     */
    public static void usePluginMode(boolean usePlugin) {
        usePluginMode = usePlugin;
    }

    public static void doHookDefault(String className) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            if (usePluginMode) {
                loadHookPlugin(className);
            } else {
                try {
                    Class<?> hookItem = Class.forName(className);
                    HookMain.actionHookFunction(hookItem);
                } catch (ClassNotFoundException e) {
                    LL.e("doHookDefault fail " + className);
                    e.printStackTrace();
                }
            }
        } else {
            LL.e("check can hook fail");
        }
    }

    public static void setHookPluginInfo(Context context, String dir, String apkFile) {
        mContext = context;
        cacheDir = dir;
        pluginFile = apkFile;
    }

    public static ClassLoader getPluginClassLoader() {
        return dexClassLoader;
    }

    //[第一次hook 需要异步操作，后续都做同步操作。同步问题]
    private static void loadHookPlugin(String className) {

        LL.d(TAG, "loadHookPlugin " + className + "  " + cacheDir + " " + failed);
        if (cacheDir != null && cacheDir.length() > 0 && !failed) {

            if (!inited) { //[do init]
                // 1） 检车插件版本 2） 安装插件
                //[if is first time init ,  new thread to run]
                LL.d( "loading hook plugin ");

                boolean initCheck = false;
                synchronized (HookMain.class) {
                    //对hook list 进行同步
                    hookList.remove(className);
                    hookList.add(className);
                    if (thisClassLoader == null) {
                        thisClassLoader = HookMain.class.getClassLoader();
                        initCheck = true;
                    }
                }


                //sync class loader
                if (initCheck) {

                    ensureCacheDirectory();

                    File file = new File(pluginFile);
                    //[如果文件不存在或者版本检测失败，则卸载插件]
                    if (!file.exists() || !checkPluginVersion()) {
                        file.delete();

                        new Thread() {
                            public void run() {
                                outputPluginFile();
                                LL.d("out put plugin file!!!");
                                HookClassLoader loader = new HookClassLoader(pluginFile,
                                        cacheDir, null, thisClassLoader);
                                loader.setPluginFilePath(hookPluginPaths);
                                dexClassLoader = loader;
                                LL.d("plugin load finished");
                                synchronized (HookMain.class) {
                                    while (!hookList.isEmpty()) {
                                        String var = hookList.pop();
                                        HookMain.doHookDefault(dexClassLoader, thisClassLoader, var);
                                    }
                                }
                                inited = true;
                            }
                        }.start();

                    } else { //[如果文件和版本本身没有问题 就直接初始化版本]
                        LL.d("create plugin loader " + pluginFile);
                        LL.d("create plugin loader " + cacheDir);
                        HookClassLoader loader = new HookClassLoader(pluginFile,
                                cacheDir, null, thisClassLoader);
                        loader.setPluginFilePath(hookPluginPaths);
                        dexClassLoader = loader;
                        while (!hookList.isEmpty()) {
                            String var = hookList.pop();
                            HookMain.doHookDefault(dexClassLoader, thisClassLoader, var);
                        }
                        inited = true;
                    }
                }

            } else {
                HookMain.doHookDefault(dexClassLoader, thisClassLoader, className);
            }

        }

    }

    /**
     * 保证opt 缓存目录创建
     */
    private static void ensureCacheDirectory() {
        File file = new File(cacheDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        //[make sure file exists]
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
    }

    /**
     * @see com.carl.lee.hooks.Version
     *
     * @return 插件版本号是否验证通过
     */
    private static boolean checkPluginVersion() {
        dexClassLoader = new DexClassLoader(pluginFile,
                cacheDir, null, thisClassLoader);
        try {
            Class versionClass = Class.forName("com.carl.lee.hooks.Version", true, dexClassLoader);
            int version = getStaticFieldInt(versionClass, "HOOK_VERSION");
            LL.d("hook-api version is " + version);
            if (version >= PLUGIN_VERSION) {
                return true;
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        LL.e("check pligin version fail");

        return false;

    }

    //将打在包内的插件，写出到内部目录；
    private static void outputPluginFile() {
        File file = new File(pluginFile);

        if (!file.exists()) {
            //[copy file]
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            FileOutputStream outputStream = null;
            InputStream inputStream = null;


            try {
                outputStream = new FileOutputStream(file);
                try {
                    inputStream = mContext.getAssets().open("hookplug.apk");
                    byte[] b = new byte[1024];
                    int n = 0;
                    while (true) {
                        n = inputStream.read(b);
                        if (n > 0) {
                            outputStream.write(b, 0, n);
                        } else {
                            break;
                        }
                    }
                    outputStream.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                    failed = true;
                }


            } catch (FileNotFoundException e) {
                e.printStackTrace();
                failed = true;
            } finally {

                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (outputStream != null) {

                    try {
                        outputStream.flush();
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

        }
    }

    private static int getStaticFieldInt(Class cls, String name) {
        while (cls != Object.class) {
            try {
                Field fld = cls.getDeclaredField(name);
                try {
                    fld.setAccessible(true);
                    return fld.getInt(cls);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    return 0;
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                cls = cls.getSuperclass();
            }
        }
        return 0;
    }

}
