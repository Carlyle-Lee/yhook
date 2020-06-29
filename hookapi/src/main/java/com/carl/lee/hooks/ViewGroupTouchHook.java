package com.carl.lee.hooks;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import lab.galaxy.yahfa.HookVars;

/**
 * this is used for view touch hook
 */
public class ViewGroupTouchHook {
    private static String TAG = "LensViewTouch";

    /**
     * OnTouchEvent of view
     **/
    @HookVars(
            className = "android.view.ViewGroup",
            methodName = "onTouchEvent",
            methodSig = "(Landroid/view/MotionEvent;)Z")
    public static boolean onTouchEvent(Object thiz, MotionEvent event) {

        String tag = thiz.getClass().getSimpleName();
        int fid = ViewTouchHook.fid.nextInt();
        ViewTouchHook.logEvent((View) thiz, tag, "onTouch(" + fid + ")", event);
        try {
            return onTouchEvent_backup(thiz, event);
        } catch (Throwable throwable) {
//            ExceptionHandle.handleException(throwable);
            return false;
        }
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


    /**
     *
     */
    @HookVars(
            className = "android.view.ViewGroup",
            methodName = "onInterceptTouchEvent",
            methodSig = "(Landroid/view/MotionEvent;)Z")
    public static boolean onInterceptTouchEvent(Object thiz, MotionEvent event) {

        String tag = thiz.getClass().getSimpleName();
        int fid = ViewTouchHook.fid.nextInt();
        ViewTouchHook.logEvent((View) thiz, tag, "onInterceptTouch(" + fid + ")", event);
        try {
            return onInterceptTouchEvent_backup(thiz, event);
        } catch (Throwable throwable) {
//            ExceptionHandle.handleException(throwable);
            return false;
        }
    }

    public static boolean onInterceptTouchEvent_backup(Object thiz, MotionEvent event) {
        //ocupation block
        try {
            Log.d(TAG, "Error when you see this log in yhook!!!");
        } catch (Exception e) {
            Log.d(TAG, "Error when you see this log in yhook!!!");
        }

        return false;
    }

    public static boolean onInterceptTouchEvent_tmp(Object thiz, MotionEvent event) {
        //ocupation block
        try {
            Log.d(TAG, "Error when you see this log in yhook!!!");
        } catch (Exception e) {
            Log.d(TAG, "Error when you see this log in yhook!!!");
        }
        return false;
    }


    /**
     *
     */
    @HookVars(
            className = "android.view.ViewGroup",
            methodName = "dispatchTouchEvent",
            methodSig = "(Landroid/view/MotionEvent;)Z")
    public static boolean dispatchTouchEvent(Object thiz, MotionEvent event) {

        String tag = thiz.getClass().getSimpleName();
        int fid = ViewTouchHook.fid.nextInt();
        ViewTouchHook.logEvent((View) thiz, tag, "dispatchTouch(" + fid + ")", event);
        try {
            return dispatchTouchEvent_backup(thiz, event);
        } catch (Throwable throwable) {
//            ExceptionHandle.handleException(throwable);
            return false;
        }
    }

    public static boolean dispatchTouchEvent_backup(Object thiz, MotionEvent event) {
        try {
            Log.d(TAG, "Error when you see this log in yhook!!!");
        } catch (Exception e) {
            Log.d(TAG, "Error when you see this log in yhook!!!");
        }


        return false;
    }

    public static boolean dispatchTouchEvent_tmp(Object thiz, MotionEvent event) {
        //ocupation block
        try {
            Log.d(TAG, "Error when you see this log in yhook!!!");
        } catch (Exception e) {
            Log.d(TAG, "Error when you see this log in yhook!!!");
        }
        return false;
    }


}
