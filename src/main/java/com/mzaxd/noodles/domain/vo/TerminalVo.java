package com.mzaxd.noodles.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author 13439
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TerminalVo {

    /**
     * 控制台渲染类型
     */
    private String rendererType;

    /**
     * 字体大小
     */
    private Integer fontSize;

    /**
     * 光标是否闪烁（0关1开）
     */
    private Integer cursorBlink;

    /**
     * 字体颜色
     */
    private String foreground;

    /**
     * 背景色
     */
    private String background;
}
