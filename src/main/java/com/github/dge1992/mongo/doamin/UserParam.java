package com.github.dge1992.mongo.doamin;

import lombok.Data;

/**
 * @Author dongganene
 * @Description
 * @Date 2019/6/13
 **/
@Data
public class UserParam extends User{
    private Integer startAge;
    private Integer endAge;
}
