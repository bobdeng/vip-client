package com.anguokeji.smartlock.vipclient;

import com.alibaba.fastjson.JSON;
import com.anguokeji.smartlock.vipclient.forms.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;
import java.util.UUID;

@RunWith(JUnit4.class)
public class VipClientImplTest {
    public static final String LOCK_ID = "23b9cc1c-6753-4eeb-aae5-dcc15a950499";
    VipClientImpl vipClient;

    @Before
    public void setup() {
        String config = "{\n" +
                "        \"priKeyInPem\": \"-----BEGIN PRIVATE KEY-----\\nMIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJmYb38jljwar8Yh\\nOPQV5lp0EfwlFEYUGO9PyBoH147QPLcqtRstWd5lS4WZaKoqeJykA+5uWr1qM0cG\\nIi/eWF60NTnmOiEXWn8qnTBwxh36iBTowuXgMo3GHlW0wCOyeby7dpZX3YnqCwbA\\n4QyiyKZjftpBRw79wQ5aE/aL07m7AgMBAAECgYEAjM77gFHum+FXPUsdvF8LU+dO\\nyZpwkKJCDbpVVs/TpMBwuPb1GxojZkf0yWDPXBkdg3S1ifMvSrC+m6Ea3El5NsVb\\n5CbqdyZudGE2Y9awyGqVmVZdzQxvsaEDt5amfnCuZXvNh+U4NpDSA1K+qksTwf+y\\n2f/Nyvslp89Tmzv6b5ECQQDJnscfb+ifl9z5knXBolAsPfxnVX/cQQZqrqWYuqbB\\nf4PpFvuonJNKtg8QPKB6DPM7j+Yk4OF9YYbvzka/CMDJAkEAwwWz8Zrv2o9OsRz6\\nf+AJsy5ybZVrZERNDVP2vs4YhPN/Xm8UxyPWMCuzMUz2el9CS5KEwN8OeUaplnXb\\ntMnMYwJALhNkDBImBjrNXBeVY5/1VHcB9Jd8ddhCYCTzYRjevP+oJJ+E+X52xq8e\\neCZywhGtkGINRUZDIcUk8i6gyk10yQJAD50iRgKwl2AH3dhDGf/W5vieArYGzhVF\\n0f5Z7/bSMbrJBMeB81ukJ2EoTJcr2KpNxX0qkcKKBx2oMDqpZ3UMnwJABDBPvgZ+\\n27kQv8ipHm0jUTMPQhQ7oRc8hVlqRnhaD/wA0CpUXNhomDVaA4coAlJaOAdhVRnT\\nRFLeyZCAGKKuUQ==\\n-----END PRIVATE KEY-----\\n\",\n" +
                "        \"vipCode\": \"99999\",\n" +
                "        \"name\": \"测试用\"\n" +
                "    }";
        VipConfig vipConfig = JSON.parseObject(config, VipConfig.class);
        vipConfig.setHost("https://vip.uuzhihui.cn/");
        vipClient = new VipClientImpl(vipConfig);
    }

    @Test
    public void testAddKey() {
        AddLockForm addLockForm = new AddLockForm();
        addLockForm.setLockId(UUID.randomUUID().toString());
        addLockForm.setMac("11:22:33:44:55:02");
        addLockForm.setName("test1");
        addLockForm.setSecret("AAAAAAAAAAAAAAAAAAAAAA==");
        vipClient.newLock(addLockForm);
    }

    @Test
    public void test_reset_key() {
        SetLockKeyForm addLockForm = new SetLockKeyForm();
        addLockForm.setLockId("23b9cc1c-6753-4eeb-aae5-dcc15a950499");
        addLockForm.setSecret("AFAAAAAAAAAAAAAAAAAAAA==");
        vipClient.resetKey(addLockForm);
    }

    @Test
    public void test_reset_name() {
        SetLockNameForm addLockForm = new SetLockNameForm();
        addLockForm.setLockId(LOCK_ID);
        addLockForm.setName("hello1");
        vipClient.resetName(addLockForm);
    }

    @Test
    public void test_grant() {
        GrantForm grantForm = new GrantForm();
        grantForm.setLockId(LOCK_ID);
        grantForm.setTarget("18657124116");
        grantForm.setTargetName("bob");
        grantForm.setLevel(10);
        vipClient.grantLockTo(grantForm);
        List<GrantVO> grantVOS = vipClient.listGrant(new LockForm(LOCK_ID));
        grantVOS.stream()
                .forEach(grantVO -> System.out.println(grantVO));
    }

    @Test
    public void test_grant_remove() {
        RemoveGrantForm grantForm = new RemoveGrantForm();
        grantForm.setLockId(LOCK_ID);
        grantForm.setTarget("18657124116");
        vipClient.removeGrant(grantForm);
    }

    @Test
    public void test_add_finger() {
        AddFingerPassForm addFingerPassForm = new AddFingerPassForm();
        addFingerPassForm.setLockId(LOCK_ID);
        addFingerPassForm.setData("hello");
        addFingerPassForm.setName("test1");
        addFingerPassForm.setUser("18657124116");
        FingerPassVO fingerPassVO = vipClient.addFingerPass(addFingerPassForm);
        System.out.println(fingerPassVO.getId());
    }

    @Test
    public void test_delete_finger() {
        DeleteFingerPassForm deleteFingerPassForm = new DeleteFingerPassForm();
        deleteFingerPassForm.setLockId(LOCK_ID);
        deleteFingerPassForm.setSeq(6869);
        vipClient.deleteFingerPass(deleteFingerPassForm);
    }

    @Test
    public void listFinger() {
        LockForm lockForm = new LockForm(LOCK_ID);
        List<FingerPassVO> list = vipClient.listFingerPass(lockForm);
        list.forEach(fingerPassVO -> System.out.println(fingerPassVO));
    }

}
