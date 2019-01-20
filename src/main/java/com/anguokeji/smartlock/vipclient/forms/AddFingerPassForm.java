package com.anguokeji.smartlock.vipclient.forms;

public class AddFingerPassForm {
    private String lockId;
    private String user;
    private String data;
    private String name;

    public AddFingerPassForm(String lockId, String user, String data, String name) {
        this.lockId = lockId;
        this.user = user;
        this.data = data;
        this.name = name;
    }
}
