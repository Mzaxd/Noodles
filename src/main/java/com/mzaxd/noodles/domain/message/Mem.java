package com.mzaxd.noodles.domain.message;

/**
 * @author Mzaxd
 * @since 2023-02-05 10:50
 */

import com.mzaxd.noodledetector.util.Arith;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 內存相关信息
 *
 * @author huasheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mem
{
    /**
     * 内存总量
     */
    private double total;

    /**
     * 已用内存
     */
    private double used;

    /**
     * 剩余内存
     */
    private double free;

}



