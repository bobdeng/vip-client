package com.anguokeji.smartlock.vipclient;

public class Base16 {
    private static char[] BASE_16_CHARS="0123456789ABCDEF".toCharArray();
    public static String encode(byte[] data){
        String result="";
        for(int i=0;i<data.length;i++){
            result+=BASE_16_CHARS[(data[i]&0xf0)>>4];
            result+=BASE_16_CHARS[(data[i])&0x0f];
        }
        return result;
    }
    public static byte[] decode(String src){
        byte[] data=new byte[src.length()/2];
        for(int i=0;i<data.length;i++){
            data[i]=(byte)Integer.parseInt(src.substring(i*2,i*2+2),16);
        }
        return data;
    }

}
