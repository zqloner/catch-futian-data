package com.mgl.utils.selfutil;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author: zhangqi
 * @CreateTime: 2020/7/514:19
 * @Company: MGL
 */
public class MySelfUtil {
    public static Map<String,String> getHandleStr(String string) {
        Map<String, String> map = new HashMap<>();
        if (StringUtils.isNotBlank(string)) {
            map.put("value", string.split(",")[0]);
            map.put("time",string.split(",")[1]);
            return map;
        }
        return null;
    }
}
