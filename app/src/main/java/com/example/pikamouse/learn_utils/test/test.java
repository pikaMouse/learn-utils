package com.example.pikamouse.learn_utils.test;

import java.util.HashMap;
import java.util.Map;

/**
 * create by jiangfeng 2019/1/4
 */
public class test {

    public static void main(String[]args) {
        Map<String, Boolean> map = new HashMap<>();
        map.put("1", true);
        map.put("1", true);
        map.put("1", false);
        System.out.print(map.get("1") + "");
    }
}
