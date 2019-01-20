package com.anguokeji.smartlock.vipclient.forms;

public class LockForm {
    private String lockId;

    public LockForm(String lockId) {
        this.lockId = lockId;
    }

    public String getLockId() {
        return lockId;
    }

    public void setLockId(String lockId) {
        this.lockId = lockId;
    }
}
