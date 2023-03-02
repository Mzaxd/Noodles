package com.mzaxd.noodles;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mzaxd.noodles.constant.SystemConstant;
import com.mzaxd.noodles.domain.entity.*;
import com.mzaxd.noodles.mapper.OsMapper;
import com.mzaxd.noodles.service.*;
import com.mzaxd.noodles.util.RedisCache;
import com.mzaxd.noodles.util.SystemInfoUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NoodlesApplicationTests {

    @Resource
    private OsMapper osMapper;

    @Resource
    private RedisCache redisCache;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private SystemSettingService systemSettingService;

    @Test
    void contextLoads() {
        try {
            HttpRequest.get("https://elizaveta.top:71/").setConnectionTimeout(1000).execute(true);
        } catch (HttpException exception) {
            log.info("建立连接失败");
        }
    }

    @Test
    void test() {
        Set<Integer> containerNotifyIds = redisCache.getCacheSet("ContainerNotifyIds");
        redisTemplate.boundSetOps("setKey");
        redisTemplate.boundSetOps("Notify:ContainerIds").remove(1);
        containerNotifyIds.remove(1);
        redisCache.setCacheSet("ContainerNotifyIds", containerNotifyIds);
    }

    @Test
    void emailTest() {
        SystemSetting systemSetting = systemSettingService.getById(1);
        MailAccount account = new MailAccount();
        account.setFrom(systemSetting.getNotificationEmail());
        account.setUser(systemSetting.getNotificationEmail());
        account.setPass(systemSetting.getEmailPass());
        MailUtil.send(account, CollUtil.newArrayList("mzaxd0712@gmail.com"),"测试5","邮件来自测试",false);
    }

    @Test
    public void testJVMInfo() {
        JSONObject jvmInfo = SystemInfoUtils.getJvmInfo();
        System.out.println(jvmInfo);
    }

    @Resource
    private HostMachineService hostMachineService;

    @Resource
    private ContainerService containerService;

    @Resource
    private ServirService servirService;

    @Resource
    private AuditLogService auditLogService;

    @Resource
    private EveryDayDataService everyDayDataService;

    /**
     * 每天晚上收集今日数据
     */
    @Test
    public void everyDayDataCollect() {
        EveryDayData everyDayData = new EveryDayData();
        LambdaQueryWrapper<AuditLog> auditLogWrapper = new LambdaQueryWrapper<>();
        Map<String, Integer> hostStateInfo = hostMachineService.getHostStateInfo();
        Map<String, Integer> vmStateInfo = hostMachineService.getVmStateInfo();
        Map<String, Integer> containerStateInfo = containerService.getContainerStateInfo();
        //设置主机总数
        everyDayData.setHostCount(hostStateInfo.get("hostCount"));
        //设置主机在线总数
        everyDayData.setHostOnlineCount(hostStateInfo.get("hostOnlineCount"));
        //设置主机离线总数
        everyDayData.setHostOfflineCount(hostStateInfo.get("hostOfflineCount"));
        //设置主机未知总数
        everyDayData.setHostUnknownCount(hostStateInfo.get("hostUnknownCount"));
        //设置虚拟机总数
        everyDayData.setVmCount(vmStateInfo.get("vmCount"));
        //设置虚拟机在线总数
        everyDayData.setVmOnlineCount(vmStateInfo.get("vmOnlineCount"));
        //设置虚拟机离线总数
        everyDayData.setVmOfflineCount(vmStateInfo.get("vmOfflineCount"));
        //设置虚拟机未知总数
        everyDayData.setVmUnknownCount(vmStateInfo.get("vmUnknownCount"));
        //设置容器总数
        everyDayData.setContainerCount(containerStateInfo.get("containerCount"));
        //设置容器在线总数
        everyDayData.setContainerOnlineCount(containerStateInfo.get("containerOnlineCount"));
        //设置容器离线总数
        everyDayData.setContainerOfflineCount(containerStateInfo.get("containerOfflineCount"));
        //设置容器未知总数
        everyDayData.setContainerUnknownCount(containerStateInfo.get("containerUnknownCount"));
        //设置服务总数
        everyDayData.setServirCount(servirService.count());
        //设置操作总数
        auditLogWrapper.ge(AuditLog::getCreateTime, DateUtil.beginOfDay(DateUtil.date()));
        auditLogWrapper.lt(AuditLog::getCreateTime, DateUtil.endOfDay(DateUtil.date()));
        everyDayData.setAuditCount(auditLogService.count(auditLogWrapper));

        System.out.println(everyDayData);
    }
}
