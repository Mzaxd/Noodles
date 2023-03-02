package com.mzaxd.noodles.domain.message;

/**
 * @author Mzaxd
 * @since 2023-02-05 10:48
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import oshi.hardware.NetworkIF;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 服务器相关信息
 *
 * @author huasheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Server {

    private static final int OSHI_WAIT_SECOND = 1000;
    /**
     * 网速测速时间2s
     */
    private static final int SLEEP_TIME = 2 * 1000;

    /**
     * 探测器id
     */
    private String detectorId;

    /**
     * Os相关信息
     */
    private com.mzaxd.noodles.domain.message.Os os = new Os();

    /**
     * CPU相关信息
     */
    private Cpu cpu = new Cpu();

    /**
     * 內存相关信息
     */
    private Mem mem = new Mem();

    /**
     * 服务器相关信息
     */
    private Sys sys = new Sys();

    /**
     * 磁盘相关信息
     */
    private List<SysFile> sysFiles = new LinkedList<SysFile>();

    /**
     * 网络相关信息
     */
    private NetWork netWork = new NetWork();

    /**
     * 网络接口相关信息
     */
    private List<NetworkIF> netWorkIf = new ArrayList<>();

}

