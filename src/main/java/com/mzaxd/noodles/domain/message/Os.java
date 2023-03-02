package com.mzaxd.noodles.domain.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Mzaxd
 * @since 2023-02-05 13:29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Os {

    /**
     * 系统名
     */
    private String osName;

    /**
     * 启动时间
     */
    private String booted;

    /**
     * 正常运行时间
     */
    private String uptime;

}
