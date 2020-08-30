package com.canye.clipboardcli;

public class Main {
    private static final String TAG = "CANYE" ;
    public static void main(String[] args) {
        final Device device = new Device();
        if (args.length == 0) {
            String text = device.getClipboardText();
            System.out.println(text);
        }
        else{
            String text = "";
            for (int i = 0; i < args.length; i++) {
                text += args[i]+ " ";
            }
            boolean res = device.setClipboardText(text.substring(0, text.length() - 1));
            if (res){
                System.out.println("Successed");
            }
            else{
                System.out.println("Failed");
            }
        }
    }
}
