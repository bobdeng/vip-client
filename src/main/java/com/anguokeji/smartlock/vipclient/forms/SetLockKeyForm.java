package com.anguokeji.smartlock.vipclient.forms;


public class SetLockKeyForm {
    private String lockId;
    private String secret;

    public String getLockId() {
        return lockId;
    }

    public void setLockId(String lockId) {
        this.lockId = lockId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
