package com.mzaxd.noodles.util;

import com.mzaxd.noodles.constant.UrlConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author root
 */
public class UrlUtil {

    public static String getUrl(String protocol, String ip, String port, String path) {
        return getAddress(protocol, ip, port) + path;
    }

    public static String getAddress(String protocol, String ip, String port) {
        return protocol + "://" + ip + ":" + port;
    }

    public static Map<String, String> resolveUrl(String url) {
        HashMap<String, String> result = new HashMap<>(3);

        String[] protocolAndAddress = url.split("://");
        String protocol = protocolAndAddress[0];
        String[] ipAndPort = protocolAndAddress[1].split(":");
        String ip = ipAndPort[0];
        String port = ipAndPort[1];
        result.put(UrlConstant.PROTOCOL, protocol);
        result.put(UrlConstant.IP, ip);
        result.put(UrlConstant.PORT, port);
        return result;
    }


}
