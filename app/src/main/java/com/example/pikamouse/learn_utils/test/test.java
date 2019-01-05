package com.example.pikamouse.learn_utils.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * create by jiangfeng 2019/1/4
 */
public class test {

    public static void main(String[]args) {
        Map<String, Boolean> map = new HashMap<>();
        map.put("1", true);
        map.put("1", true);
        map.put("1", false);
        String[] strings = map.keySet().toArray(new String[0]);
    }
}
