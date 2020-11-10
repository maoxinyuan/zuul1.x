package com.pacific.apigetway.core;

import java.util.HashMap;

/**
 * @author maoxy
 * @date 20181210
 **/
public class GlobalCache extends HashMap<String, Object> {

    private static class Holder {
        private final static GlobalCache CACHE = new GlobalCache();
    }

    public static GlobalCache instance() {
        return Holder.CACHE;
    }
}
