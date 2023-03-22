package com.mzaxd.noodles.util;

import com.mzaxd.noodles.constant.UrlConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author root
 */
@Slf4j
@Component
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

    public static String getHostname(String address) {
        String[] parts = address.split(":");
        return parts[0];
    }

    public static boolean isHostOnline(String ipAddress) throws IOException {
        InetAddress inetAddress = InetAddress.getByName(ipAddress);
        // 超时时间为5秒
        return inetAddress.isReachable(5000);
    }

    public static boolean isServiceOnline(String serverName, int port) throws IOException {
        Socket socket = new Socket(serverName, port);
        log.info("[实例状态检测]：" + serverName + "服务已经开启");
        socket.close();
        return true;
    }

    public static int getPort(String address) {
        String[] parts = address.split(":");
        return Integer.parseInt(parts[1]);
    }
}
