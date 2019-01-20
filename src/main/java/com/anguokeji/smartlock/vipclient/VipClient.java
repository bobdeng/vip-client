package com.anguokeji.smartlock.vipclient;

import com.anguokeji.smartlock.vipclient.forms.*;

import java.util.List;

public interface VipClient {

    void newLock(AddLockForm addLockForm);

    void resetKey(SetLockKeyForm setLockKeyForm);

    void resetName(SetLockNameForm setLockNameForm);

    void grantLockTo(GrantForm grantForm);

    void removeGrant(RemoveGrantForm removeGrantForm);

    FingerPassVO addFingerPass(AddFingerPassForm addFingerPassForm);

    void deleteFingerPass(DeleteFingerPassForm deleteFingerPassForm);

    List<FingerPassVO> listFingerPass(LockForm lockForm);
}
