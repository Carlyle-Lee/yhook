package lab.galaxy.yahfa;

import android.os.Build;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by liuruikai756 on 28/03/2017.
 * First Copied from https://github.com/Tencent/GT android/GT_SDK/yhook
 * Modified by Lens project : Carlyle Lee
 * Todo: need to be optimised
 */

public class HookMain {
    private static final String TAG = "lens-hook-main";
    private static List<Class<?>> hookInfoClasses = new LinkedList<>();

    static {
        System.loadLibrary("yhook");
        init(android.os.Build.VERSION.SDK_INT);
    }


    //[加载插件中的制定类]
    public static void doHookDefault(ClassLoader patchClassLoader, ClassLoader originClassLoader, String thatClass) {
        try {
            LL.d("dohook " + thatClass);
            doHookItemDefault(patchClassLoader, thatClass, patchClassLoader);
        } catch (Exception e) {
            e.printStackTrace();

            LL.e("hook fail ! doHookDefault " + thatClass);
        }
    }

    //[这里是可以改造的： 第一次加载插件的情况下，异步加载。后续可以直接去加载hook了。]
    private static void doHookItemDefault(ClassLoader patchClassLoader, String hookItemName, ClassLoader originClassLoader) {
        try {
            Log.d("hook", "Start hooking with item " + hookItemName);
            Class<?> hookItem = Class.forName(hookItemName, true, patchClassLoader);
            //[切换到使用注解来解析]
            actionHookFunction(hookItem);
        } catch (Exception e) {
            e.printStackTrace();
            LL.e("doHookItemDefault fail " + hookItemName);
        }
    }


    public static void findAndBackupAndHook(Class targetClass, String methodName, String methodSig,
                                            Method hook, Method backup) {
        backupAndHook(findMethod(targetClass, methodName, methodSig), hook, backup);
    }

    public static void hook(Object target, Method hook) {
        backupAndHook(target, hook, null);
    }

    public static void backupAndHook(Object target, Method hook, Method backup) {
        if (target == null) {
            throw new IllegalArgumentException("null target method");
        }
        if (hook == null) {
            throw new IllegalArgumentException("null hook method");
        }

        if (!Modifier.isStatic(hook.getModifiers())) {
            throw new IllegalArgumentException("Hook must be a static method: " + hook);
        }
        checkCompatibleMethods(target, hook, "Original", "Hook");
        if (backup != null) {
            if (!Modifier.isStatic(backup.getModifiers())) {
                throw new IllegalArgumentException("Backup must be a static method: " + hook);
            }
            // backup is just a placeholder and the constraint could be less strict
            checkCompatibleMethods(target, backup, "Original", "Backup");
        }

        if (!backupAndHookNative(target, hook, backup)) {
            throw new RuntimeException("Failed to hook " + target + " with " + hook);
        }
    }

    private static Object findMethod(Class cls, String methodName, String methodSig) {
        if (cls == null) {
            throw new IllegalArgumentException("null class");
        }
        if (methodName == null) {
            throw new IllegalArgumentException("null method name");
        }
        if (methodSig == null) {
            throw new IllegalArgumentException("null method signature");
        }
        return findMethodNative(cls, methodName, methodSig);
    }

    private static void checkCompatibleMethods(Object original, Method replacement, String originalName, String replacementName) {
        ArrayList<Class<?>> originalParams;
        if (original instanceof Method) {
            originalParams = new ArrayList<>(Arrays.asList(((Method) original).getParameterTypes()));
        } else if (original instanceof Constructor) {
            originalParams = new ArrayList<>(Arrays.asList(((Constructor<?>) original).getParameterTypes()));
        } else {
            throw new IllegalArgumentException("Type of target method is wrong");
        }

        ArrayList<Class<?>> replacementParams = new ArrayList<>(Arrays.asList(replacement.getParameterTypes()));

        if (original instanceof Method
                && !Modifier.isStatic(((Method) original).getModifiers())) {
            originalParams.add(0, ((Method) original).getDeclaringClass());
        } else if (original instanceof Constructor) {
            originalParams.add(0, ((Constructor<?>) original).getDeclaringClass());
        }


        if (!Modifier.isStatic(replacement.getModifiers())) {
            replacementParams.add(0, replacement.getDeclaringClass());
        }

        if (original instanceof Method
                && !replacement.getReturnType().isAssignableFrom(((Method) original).getReturnType())) {
            throw new IllegalArgumentException("Incompatible return types. " + originalName + ": " + ((Method) original).getReturnType() + ", " + replacementName + ": " + replacement.getReturnType());
        } else if (original instanceof Constructor) {
            if (replacement.getReturnType().equals(Void.class)) {
                throw new IllegalArgumentException("Incompatible return types. " + "<init>" + ": " + "V" + ", " + replacementName + ": " + replacement.getReturnType());
            }
        }

        if (originalParams.size() != replacementParams.size()) {
            throw new IllegalArgumentException("Number of arguments don't match. " + originalName + ": " + originalParams.size() + ", " + replacementName + ": " + replacementParams.size());
        }

        for (int i = 0; i < originalParams.size(); i++) {
            if (!replacementParams.get(i).isAssignableFrom(originalParams.get(i))) {
                throw new IllegalArgumentException("Incompatible argument #" + i + ": " + originalName + ": " + originalParams.get(i) + ", " + replacementName + ": " + replacementParams.get(i));
            }
        }
    }

    private static native boolean backupAndHookNative(Object target, Method hook, Method backup);

    public static native Object findMethodNative(Class targetClass, String methodName, String methodSig);

    public static native void init(int SDK_version);

    // 直接hook 方案：
    public static HashMap<String, Method> targetMethods = new HashMap<>();


    /**
     * 直接hook
     *
     * @param hookClazz
     */
    public static void actionHookFunction(Class hookClazz) {

        if (hookClazz == null) {
            throw new IllegalArgumentException("Class cannot be null!");
        }
        if (hookInfoClasses.contains(hookClazz)) {
            //already hooked : fix bug : hooked twice cause crash on some devices
            return;
        }
        //[暂时不支持8.0+ 设备使用hook 功能]
        Method[] methods = hookClazz.getDeclaredMethods();
        for (Method method : methods) {
            HookVars annotation = method.getAnnotation(HookVars.class);
            if (annotation != null && isSDKMatach(annotation)) {
                String className = annotation.className();
                String methodName = annotation.methodName();
                String methodSig = annotation.methodSig();
                String backupMethodName = method.getName() + "_backup";
                Method backup = null;
                for (Method method1 : methods) {
                    if (method1.getName().equals(backupMethodName)) {
                        backup = method1;
                        break;
                    }
                }
                Method tmp = null;
                String tmpMethodName = method.getName() + "_tmp";
                for (Method method1 : methods) {
                    if (method1.getName().equals(tmpMethodName)) {
                        tmp = method1;
                        break;
                    }
                }

                try {
                    Class<?> clazz = Class.forName(className, true, HookMain.class.getClassLoader());
                    findAndBackupAndHook(clazz, methodName, methodSig, method, backup);
                    targetMethods.put(methodSig, method);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    LL.e("actionHookFuntion: fail : " + className);
                }
            }

        }
        hookInfoClasses.add(hookClazz);

    }


    /**
     * 判断当前系统版本是否与hook方法匹配
     *
     * @param annotation
     * @return
     */
    static boolean isSDKMatach(HookVars annotation) {
        int sdkVersion = annotation.sdkVersion();
        if (sdkVersion > -1) {
            byte type = annotation.sdkType();
            int current = Build.VERSION.SDK_INT;
            switch (type) {
                case HookVars.MATCH_EQUAL:
                    return current == sdkVersion;
                case HookVars.MATCH_LESS:
                    return current < sdkVersion;
                case HookVars.MATCH_GREATER:
                    return current > sdkVersion;
                default:
                    return false;
            }
        }
        return true;
    }
}
