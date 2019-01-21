package com.anguokeji.smartlock.vipclient;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class VipClientImpl implements VipClient {
    private VipConfig vipConfig;

    public VipClientImpl(VipConfig vipConfig) {
        this.vipConfig = vipConfig;
    }

    public static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json");
    public static final String METHOD_POST = "POST";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_DELETE = "DELETE";


    private <T> T callRequest(String url, String method, Object form, Class<T> clz) {
        return callRequest(url, method, form, body -> {
            JSONObject jsonObject = JSON.parseObject(body);
            BaseResult<T> result = new BaseResult<>();
            result.setCode(jsonObject.getInteger("code"));
            result.setError(jsonObject.getString("error"));
            JSONObject data = jsonObject.getJSONObject("data");
            if (data != null) {
                result.setData(data.toJavaObject(clz));
            }
            return result;
        });
    }

    private <T> List<T> callRequestList(String url, String method, Object form, Class<T> clz) {
        return callRequest(url, method, form, body -> {
            JSONObject jsonObject = JSON.parseObject(body);
            BaseResult<List<T>> result = new BaseResult<>();
            result.setCode(jsonObject.getInteger("code"));
            result.setError(jsonObject.getString("error"));
            JSONArray data = jsonObject.getJSONArray("data");
            List<T> resultData = new ArrayList<>(data.size());
            for (int i = 0; i < data.size(); i++) {
                resultData.add(data.getJSONObject(i).toJavaObject(clz));
            }
            result.setData(resultData);
            return result;

        });
    }

    private <T> T callRequest(String url, String method, Object form, Function<String, BaseResult<T>> converter) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
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
                BaseResult<T> result = converter.apply(response.body().string());
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
        callRequest("/lock/new_lock", METHOD_PUT, addLockForm, String.class);
    }

    public void resetKey(SetLockKeyForm setLockKeyForm) {
        callRequest("/lock/reset_key", METHOD_PUT, setLockKeyForm, String.class);
    }

    public void resetName(SetLockNameForm setLockNameForm) {
        callRequest("/lock/set_name", METHOD_PUT, setLockNameForm, String.class);
    }

    public void grantLockTo(GrantForm grantForm) {
        callRequest("/grant/grant_to", METHOD_PUT, grantForm, String.class);
    }

    @Override
    public List<GrantVO> listGrant(LockForm lockForm) {
        return callRequestList("/grant/list", METHOD_POST, lockForm, GrantVO.class);
    }

    public void removeGrant(RemoveGrantForm removeGrantForm) {
        callRequest("/grant/remove", METHOD_DELETE, removeGrantForm, String.class);
    }

    public FingerPassVO addFingerPass(AddFingerPassForm addFingerPassForm) {
        return callRequest("/fingerpass/add", METHOD_POST, addFingerPassForm, FingerPassVO.class);
    }

    public void deleteFingerPass(DeleteFingerPassForm deleteFingerPassForm) {
        callRequest("/fingerpass/delete", METHOD_DELETE, deleteFingerPassForm, String.class);
    }

    public List<FingerPassVO> listFingerPass(LockForm lockForm) {
        return callRequestList("/fingerpass/list", METHOD_POST, lockForm, FingerPassVO.class);
    }
}
