package com.mzaxd.noodles.util;

import com.mzaxd.noodles.domain.vo.ContainerVo;
import com.mzaxd.noodles.domain.vo.HostVo;
import com.mzaxd.noodles.domain.vo.VmVo;
import org.springframework.util.StringUtils;

/**
 * @author 13439
 */
public class SshLinkUtil {

    public static boolean isHostSshLinkParamValid(HostVo hostVo) {
        try {
            if (StringUtils.hasText(hostVo.getSshHost()) || StringUtils.hasText(hostVo.getSshUser()) ||
                    StringUtils.hasText(hostVo.getSshPwd()) || StringUtils.hasText(hostVo.getSshPort().toString())) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static boolean isVmSshLinkParamValid(VmVo vmVo) {
        try {
            if (StringUtils.hasText(vmVo.getSshHost()) || StringUtils.hasText(vmVo.getSshUser()) ||
                    StringUtils.hasText(vmVo.getSshPwd()) || StringUtils.hasText(vmVo.getSshPort().toString())) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static boolean isContainerSshLinkParamValid(ContainerVo containerVo) {
        try {
            if (StringUtils.hasText(containerVo.getSshHost()) || StringUtils.hasText(containerVo.getSshUser()) ||
                    StringUtils.hasText(containerVo.getSshPwd()) || StringUtils.hasText(containerVo.getSshPort().toString())) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }


}
