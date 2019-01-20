package com.anguokeji.smartlock.vipclient;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.anguokeji.smartlock.vipclient.forms.AddLockForm;
import com.anguokeji.smartlock.vipclient.forms.SetLockKeyForm;
import com.anguokeji.smartlock.vipclient.forms.SetLockNameForm;
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
                BaseResult<T> result = JSONObject.parseObject(response.body().string(), new TypeReference<BaseResult<T>>() {
                }.getType());
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
        callRequest("/lock/new_lock",METHOD_PUT,addLockForm,String.class);
    }

    public void resetKey(SetLockKeyForm setLockKeyForm) {
        callRequest("/lock/reset_key",METHOD_PUT,setLockKeyForm,String.class);
    }

    public void resetName(SetLockNameForm setLockNameForm) {
        callRequest("/lock/set_name",METHOD_PUT,setLockNameForm,String.class);
    }
}
