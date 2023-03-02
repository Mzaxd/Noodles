package com.mzaxd.noodles.domain.ssh;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 13439
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SshMessage {

    private String op;

    private Integer cols;

    private Integer rows;

    private String data;

}
