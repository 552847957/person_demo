package com.wondersgroup.healthcloud.utils.wonderCloud;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Created by nick on 2017/6/26.
 */
public class PatientKeyMap {

    private static Map<String, Map<String, String>> patientKeyMap = Maps.newConcurrentMap();

    static {
        Map<String, String> doctorKeys = Maps.newHashMap();
        doctorKeys.put("publicKey", "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC6hSJo6PAvAtxaRxQ5qCc6XUL30faXAMsub4PgXLaTcJiDPEm35ERhccU8f4SX5ieWrjBX7CpHfI+LKwNzQoFBhn+ngfE8kmCplepjL1pTK6Y4FRgdKxJVd1uzya9G67KB8K5QQ4Uj3lMwxLez/SDdgPmjy7X2s23mtltthQOSLQIDAQAB");
        doctorKeys.put("privateKey","MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALqFImjo8C8C3FpHFDmoJzpdQvfR9pcAyy5vg+BctpNwmIM8SbfkRGFxxTx/hJfmJ5auMFfsKkd8j4srA3NCgUGGf6eB8TySYKmV6mMvWlMrpjgVGB0rElV3W7PJr0brsoHwrlBDhSPeUzDEt7P9IN2A+aPLtfazbea2W22FA5ItAgMBAAECgYEAhwYoAdBXR4EHqab5AkAznbGz8BkkLO5bKBN8YWhcl2GUVrTHHQN3aR9mTER35Uqs8AzLXGrPtI58j5+k0MSdMmadoCXG9U7wcnCaik5mTf16I5390QGcAAdB3mdY7oITLIKsz4pGMSOpk0CgvkXglJ49LtRzm8iRcboggxAsWXUCQQD2L0Ua9eYF/y7fYU+D3l6LyFf2/CDvDhUkzrL82ybqDb25SD/zzOo8zV9VL7ZAd8T8FRzvbNw8n0/a+EWq6I57AkEAwfTi9WWAvse/KQ3E46s+Wbq97v609mMitUaPk2RvYBn2nCfPOvwEgNMnPxCYdQDMgeIASZmHWndbVipbXQfVdwJAHXRAX15mO/dxAzbgTZWwWCcLJzi5NADKVNIKJiiOOliUh3N2e1Pb/pRPwKBpvMLXpZVdFeQ/YV1qL3ee1jjmuwJAQTx31eglDIYswscx0Q248/8+gRNElJa1htlL01x1pZI2A0HUjtdTQG1FBw4y6S+ymYEFbbvo7cG1g97NShYncwJBALWxgscXRzCumuh1LxAmQBpyRM/Nqf0+YEB3kWDILgGsxL/t8jiFS16dDehpmXmtnvCmkJCiekGr1V7SFlW/0XQ=");
        patientKeyMap.put("all",doctorKeys);
    }

    public static String getPublicKey(String appVersion){
        Map<String, String> keyMap = patientKeyMap.get(appVersion);
        if(keyMap==null){
            return patientKeyMap.get("all").get("publicKey");
        }
        return keyMap.get("publicKey");
    }

    public static String getPrivateKey(String appVersion){
        Map<String, String> keyMap = patientKeyMap.get(appVersion);
        if(keyMap==null){
            return patientKeyMap.get("all").get("privateKey");
        }
        return keyMap.get("privateKey");
    }
}
