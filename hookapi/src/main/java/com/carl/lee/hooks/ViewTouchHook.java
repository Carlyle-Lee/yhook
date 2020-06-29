package com.carl.lee.hooks;

import android.content.res.Resources;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

import lab.galaxy.yahfa.HookVars;

/**
 * this is used for view touch hook
 * Demo: for how to write your own hook plugin
 */
public class ViewTouchHook {
    private static String TAG = "LensViewTouch";
    public static boolean enableLog = true;
    public static Random fid = new Random();

    /**
     * OnTouchEvent of view
     **/
    @HookVars(
            className = "android.view.View",
            methodName = "onTouchEvent",
            methodSig = "(Landroid/view/MotionEvent;)Z")
    public static boolean onTouchEvent(Object thiz, MotionEvent event) {

        int id = fid.nextInt();
        String tag = thiz.getClass().getSimpleName();
        logEvent((View) thiz, tag, "onTouch(" + id + ")", event);
        boolean var = false;
        try {
            var = onTouchEvent_backup(thiz, event);
        } catch (Throwable throwable) {
//            ExceptionHandle.handleException(throwable);
        }
//        if (event.getAction() == MotionEvent.ACTION_UP && thiz instanceof View) {
//            View view = (View) thiz;
//            Object touchLis = LensReflectionTool.fieldChain(view, new String[]{
//                    "mListenerInfo",
//                    "mOnTouchListener"
//            });
//            if (touchLis != null) {
//                LL.e(TAG, id + " has touch listener  " + touchLis.getClass().getName());
//            }
//        }

        return var;
    }

    public static boolean onTouchEvent_backup(Object thiz, MotionEvent event) {

        //ocupation block
        try {
            Log.d(TAG, "Error when you see this log in yhook!!!");
        } catch (Exception e) {
            Log.d(TAG, "Error when you see this log in yhook!!!");
        }

        return false;
    }

    public static boolean onTouchEvent_tmp(Object thiz, MotionEvent event) {

        //ocupation block
        try {
            Log.d(TAG, "Error when you see this log in yhook!!!");
        } catch (Exception e) {
            Log.d(TAG, "Error when you see this log in yhook!!!");
        }

        return false;
    }

    public static void logEvent(View view, String tag, String key, MotionEvent event) {
        //[日志控制开关]
        if (!enableLog) return;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "Down " + event.getX() + " " + event.getY() + " " + key + " " + tag + makeViewID(view) + "  ");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "Move " + " " + event.getX() + " " + event.getY() + " " + key + "  " + tag);
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, " Action UP>>> " + " " + event.getX() + " " + event.getY() + " " + key + "  " + tag);
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG, "CANCEL " + " " + event.getX() + " " + event.getY() + " " + key + "  " + tag);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.d(TAG, "POINTER DOWN " + key + "  " + tag);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                Log.d(TAG, "POINTER UP " + key + "  " + tag);
                break;
        }
    }

    private static String makeViewID(View view) {
        if (view != null) {
            int id = view.getId();
            if (id > 0) {
                Resources resources = view.getContext().getResources();
                if (resources != null) {
                    StringBuilder out = new StringBuilder();
                    try {
                        String pkgname;
                        switch (id & 0xff000000) {
                            case 0x7f000000:
                                pkgname = "app";
                                break;
                            case 0x01000000:
                                pkgname = "android";
                                break;
                            default:
                                pkgname = resources.getResourcePackageName(id);
                                break;
                        }
                        String typename = resources.getResourceTypeName(id);
                        String entryname = resources.getResourceEntryName(id);
                        out.append(" ");
                        out.append(pkgname);
                        out.append(":");
                        out.append(typename);
                        out.append("/");
                        out.append(entryname);
                        return out.toString();
                    } catch (Resources.NotFoundException e) {
                    }
                }
            }

        }

        return "";
    }
}
