package com.canye.clipboardcli.wrappers;
import android.content.ClipData;
import android.os.Build;
import android.os.IInterface;

import com.canye.clipboardcli.Ln;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ClipboardManager {
    private final IInterface manager;
    private Method getPrimaryClipMethod;
    private Method setPrimaryClipMethod;

    public ClipboardManager(IInterface manager) {
        this.manager = manager;
    }

    private Method getGetPrimaryClipMethod() throws NoSuchMethodException {
        if (getPrimaryClipMethod == null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                getPrimaryClipMethod = manager.getClass().getMethod("getPrimaryClip", String.class);
            } else {
                getPrimaryClipMethod = manager.getClass().getMethod("getPrimaryClip", String.class, int.class);
            }
        }
        return getPrimaryClipMethod;
    }

    private Method getSetPrimaryClipMethod() throws NoSuchMethodException {
        if (setPrimaryClipMethod == null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                setPrimaryClipMethod = manager.getClass().getMethod("setPrimaryClip", ClipData.class, String.class);
            } else {
                setPrimaryClipMethod = manager.getClass().getMethod("setPrimaryClip", ClipData.class, String.class, int.class);
            }
        }
        return setPrimaryClipMethod;
    }

    private static ClipData getPrimaryClip(Method method, IInterface manager) throws InvocationTargetException, IllegalAccessException {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return (ClipData) method.invoke(manager, ServiceManager.PACKAGE_NAME);
        }
        return (ClipData) method.invoke(manager, ServiceManager.PACKAGE_NAME, ServiceManager.USER_ID);
    }

    private static void setPrimaryClip(Method method, IInterface manager, ClipData clipData)
            throws InvocationTargetException, IllegalAccessException {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            method.invoke(manager, clipData, ServiceManager.PACKAGE_NAME);
        } else {
            method.invoke(manager, clipData, ServiceManager.PACKAGE_NAME, ServiceManager.USER_ID);
        }
    }

    public CharSequence getText() {
        try {
            Method method = getGetPrimaryClipMethod();
            ClipData clipData = getPrimaryClip(method, manager);
            if (clipData == null || clipData.getItemCount() == 0) {
                return null;
            }
            return clipData.getItemAt(0).getText();
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            Ln.e("Could not invoke method", e);
            return null;
        }
    }

    public boolean setText(CharSequence text) {
        try {
            Method method = getSetPrimaryClipMethod();
            ClipData clipData = ClipData.newPlainText(null, text);
            setPrimaryClip(method, manager, clipData);
            return true;
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            Ln.e("Could not invoke method", e);
            return false;
        }
    }
}