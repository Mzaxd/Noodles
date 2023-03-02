package com.mzaxd.noodles;

import com.alibaba.fastjson.JSONObject;
import com.mzaxd.noodles.util.SystemInfoUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EncodePassword {

    @Resource
    private PasswordEncoder passwordEncoder;

    @Test
    public void test() {
        String password = "200712";
        String encode = passwordEncoder.encode(password);
        System.out.println(encode);
    }

}
