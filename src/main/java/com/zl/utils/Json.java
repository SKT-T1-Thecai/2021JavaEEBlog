package com.zl.utils;

import com.alibaba.fastjson.JSONObject;

public class Json {
    public static JSONObject EntityToJson(Object o)
    {
        JSONObject jo = JSONObject.parseObject(com.alibaba.fastjson.JSON.toJSONString(o));
        return jo;
    }
}
