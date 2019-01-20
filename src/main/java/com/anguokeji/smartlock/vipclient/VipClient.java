package com.anguokeji.smartlock.vipclient;

import com.anguokeji.smartlock.vipclient.forms.AddLockForm;
import com.anguokeji.smartlock.vipclient.forms.SetLockKeyForm;
import com.anguokeji.smartlock.vipclient.forms.SetLockNameForm;

public interface VipClient {

    void newLock(AddLockForm addLockForm);

    void resetKey(SetLockKeyForm setLockKeyForm);

    void resetName(SetLockNameForm setLockNameForm);
}
