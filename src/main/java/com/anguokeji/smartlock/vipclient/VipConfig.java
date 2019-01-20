package com.anguokeji.smartlock.vipclient;

public class VipConfig {
    private String host;
    private String vipCode;
    private String priKeyInPem;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getVipCode() {
        return vipCode;
    }

    public void setVipCode(String vipCode) {
        this.vipCode = vipCode;
    }

    public String getPriKeyInPem() {
        return priKeyInPem;
    }

    public void setPriKeyInPem(String priKeyInPem) {
        this.priKeyInPem = priKeyInPem;
    }
}
