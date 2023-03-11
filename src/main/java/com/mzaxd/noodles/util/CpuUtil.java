package com.mzaxd.noodles.util;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;

/**
 * @author Mzaxd
 * @since 2023-03-11 22:17
 */
public class CpuUtil {

    public static int getLogicProcessorCount() {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardware = systemInfo.getHardware();
        CentralProcessor processor = hardware.getProcessor();
        return processor.getLogicalProcessorCount();
    }
}
