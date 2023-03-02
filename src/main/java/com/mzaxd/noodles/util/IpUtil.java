package com.mzaxd.noodles.util;

import cn.hutool.core.net.NetUtil;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ip2region封装工具类
 *
 * @author 13439
 */

@Slf4j
public class IpUtil {

    /**
     * 获取客户端IP地址
     *
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }
        if (ip.split(",").length > 1) {
            ip = ip.split(",")[0];
        }
        return ip;
    }

    public static String getRegionByIp(String ip) throws IOException {
        if (NetUtil.isInnerIP(ip)) {
            return "用户当前为内网访问";
        }
        URL url = IpUtil.class.getClassLoader().getResource("ip2region.db");
        File file;
        if (url != null) {
            file = new File(url.getFile());
        } else {
            return null;
        }
        if (!file.exists()) {
            System.out.println("Error: Invalid ip2region.db file, filePath：" + file.getPath());
            return null;
        }
        String dbPath = file.getPath();
        // 1、创建 searcher 对象
        Searcher searcher = null;
        try {
            searcher = Searcher.newWithFileOnly(dbPath);
        } catch (IOException e) {
            log.error("failed to create searcher with `%s`: %s\n", dbPath, e);
            return "无法识别用户地址";
        }

        // 2、查询
        try {
            long sTime = System.nanoTime();
            String region = searcher.search(ip);
            long cost = TimeUnit.NANOSECONDS.toMicros((long) (System.nanoTime() - sTime));
            log.info("{region: %s, ioCount: %d, took: %d μs}\n", region, searcher.getIOCount(), cost);
            if (!StringUtils.hasText(region)) {
                region = "无法识别用户地址";
            }
            return region;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 3、关闭资源
            searcher.close();
        }
        return "无法识别用户地址";
    }
}
