package com.anguokeji.smartlock.vipclient;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.anguokeji.smartlock.vipclient.forms.*;
import okhttp3.*;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.signers.RSADigestSigner;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import sun.security.rsa.RSAPrivateCrtKeyImpl;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.security.interfaces.RSAPrivateKey;
import java.util.List;

public class VipClientImpl implements VipClient {
    private VipConfig vipConfig;
    private OkHttpClient okHttpClient;

    public VipClientImpl(VipConfig vipConfig) {
        this.vipConfig = vipConfig;
        okHttpClient = new OkHttpClient.Builder().build();
    }

    public static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json");
    public static final String METHOD_POST = "POST";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_DELETE = "DELETE";


    private <T> T callRequest(String url, String method, Object form, Type type) {

        long timestamp = System.currentTimeMillis();
        String content = JSON.toJSONString(form);
        String sign = sign(vipConfig.getPriKeyInPem(), timestamp + content);
        Request request = new Request.Builder()
                .url(vipConfig.getHost() + url)
                .header("App-Code", vipConfig.getVipCode())
                .header("Timestamp", String.valueOf(timestamp))
                .header("Authorization", sign)
                .method(method, RequestBody.create(JSON_MEDIA_TYPE, content))
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                BaseResult<T> result = JSON.parseObject(response.body().string(), type);
                if (result == null) {
                    throw new RuntimeException("server error");
                }
                if (result.getCode() != 0) {
                    throw new RuntimeException(result.getError());
                }
                return result.getData();
            } else {
                throw new RuntimeException(response.body().string());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String sign(String priKey, String content) {
        try {
            PemReader pemReader = new PemReader(new StringReader(priKey));
            PemObject pemObject = pemReader.readPemObject();
            RSAPrivateKey rsaPrivateKey = RSAPrivateCrtKeyImpl.newKey(pemObject.getContent());
            RSADigestSigner signer = new RSADigestSigner(new SHA512Digest());
            signer.init(true, new RSAKeyParameters(true, rsaPrivateKey.getModulus(), rsaPrivateKey.getPrivateExponent()));
            byte[] data = content.getBytes();
            signer.update(data, 0, data.length);
            byte[] signature = signer.generateSignature();
            return Base16.encode(signature);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void newLock(AddLockForm addLockForm) {
        callRequest("/lock/new_lock", METHOD_PUT, addLockForm, new TypeReference<BaseResult<String>>() {
        }.getType());
    }

    public void resetKey(SetLockKeyForm setLockKeyForm) {
        callRequest("/lock/reset_key", METHOD_PUT, setLockKeyForm, new TypeReference<BaseResult<String>>() {
        }.getType());
    }

    public void resetName(SetLockNameForm setLockNameForm) {
        callRequest("/lock/set_name", METHOD_PUT, setLockNameForm, new TypeReference<BaseResult<String>>() {
        }.getType());
    }

    public void grantLockTo(GrantForm grantForm) {
        callRequest("/grant/grant_to", METHOD_PUT, grantForm, new TypeReference<BaseResult<String>>() {
        }.getType());
    }

    @Override
    public List<GrantVO> listGrant(LockForm lockForm) {
        return callRequest("/grant/list", METHOD_POST, lockForm, new TypeReference<BaseResult<List<GrantVO>>>() {
        }.getType());
    }

    public void removeGrant(RemoveGrantForm removeGrantForm) {
        callRequest("/grant/remove", METHOD_DELETE, removeGrantForm, new TypeReference<BaseResult<String>>() {
        }.getType());
    }

    public FingerPassVO addFingerPass(AddFingerPassForm addFingerPassForm) {
        return callRequest("/fingerpass/add", METHOD_PUT, addFingerPassForm, new TypeReference<BaseResult<FingerPassVO>>() {
        }.getType());
    }

    public void deleteFingerPass(DeleteFingerPassForm deleteFingerPassForm) {
        callRequest("/fingerpass/delete", METHOD_DELETE, deleteFingerPassForm, new TypeReference<BaseResult<String>>() {
        }.getType());
    }

    public List<FingerPassVO> listFingerPass(LockForm lockForm) {
        return callRequest("/fingerpass/list", METHOD_POST, lockForm, new TypeReference<BaseResult<List<FingerPassVO>>>() {
        }.getType());
    }
}
