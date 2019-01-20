package com.anguokeji.smartlock.vipclient.forms;


public class AddLockForm {
    private String lockId;
    private String mac;
    private String secret;
    private String name;

    public String getLockId() {
        return lockId;
    }

    public void setLockId(String lockId) {
        this.lockId = lockId;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
