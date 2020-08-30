package com.canye.clipboardcli;

import android.os.Build;

import com.canye.clipboardcli.wrappers.ServiceManager;
import com.canye.clipboardcli.wrappers.ClipboardManager;
import java.util.concurrent.atomic.AtomicBoolean;
public final class Device {
    private final ServiceManager serviceManager = new ServiceManager();
    public static String getDeviceName() {
        return Build.MODEL;
    }

    private final AtomicBoolean isSettingClipboard = new AtomicBoolean();

    public String getClipboardText() {
        ClipboardManager clipboardManager = serviceManager.getClipboardManager();
        if (clipboardManager == null) {
            return null;
        }
        CharSequence s = clipboardManager.getText();
        if (s == null) {
            return null;
        }
        return s.toString();
    }

    public boolean setClipboardText(String text) {
        ClipboardManager clipboardManager = serviceManager.getClipboardManager();
        if (clipboardManager == null) {
            return false;
        }

        String currentClipboard = getClipboardText();
        if (currentClipboard != null && currentClipboard.equals(text)) {
            // The clipboard already contains the requested text.
            // Since pasting text from the computer involves setting the device clipboard, it could be set twice on a copy-paste. This would cause
            // the clipboard listeners to be notified twice, and that would flood the Android keyboard clipboard history. To workaround this
            // problem, do not explicitly set the clipboard text if it already contains the expected content.
            return false;
        }

        isSettingClipboard.set(true);
        boolean ok = clipboardManager.setText(text);
        isSettingClipboard.set(false);
        return ok;
    }
}
