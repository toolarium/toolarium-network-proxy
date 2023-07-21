/*
 * JSONUtilTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.proxy.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;


/**
 * Test the {@link JSONUtil}.
 * 
 * @author patrick
 */
public class JSONUtilTest {

    /**
     * Test convert json array
     */
    @Test
    public void convertJSONArray() {
        List<String> list = Arrays.asList("my", "simple", "Test");
        
        String jsonArray = JSONUtil.getInstance().convert(list);
        assertEquals("[ \"my\", \"simple\", \"Test\" ]", jsonArray);
        
        assertEquals(list, JSONUtil.getInstance().convert(jsonArray));
    }

    
    /**
     * Test convert json array
     */
    @Test
    public void convertEmptyJSONArray() {
        List<String> list = Arrays.asList("");
        
        String jsonArray = JSONUtil.getInstance().convert(list);
        assertEquals("[ \"\" ]", jsonArray);
        
        assertEquals(list, JSONUtil.getInstance().convert(jsonArray));

    
        list = Arrays.asList("", "");        
        jsonArray = JSONUtil.getInstance().convert(list);
        assertEquals("[ \"\", \"\" ]", jsonArray);
        
        assertEquals(list, JSONUtil.getInstance().convert(jsonArray));

    }


    /**
     * Test convert json array
     */
    @Test
    public void convertEmptyListJSONArray() {
        List<String> list = new ArrayList<String>();
        
        String jsonArray = JSONUtil.getInstance().convert(list);
        assertEquals("[ ]", jsonArray);
        
        assertEquals(list, JSONUtil.getInstance().convert(jsonArray));
    }


    /**
     * Test convert json array
     */
    @Test
    public void convertListJNullValuesJSONArray() {
        List<String> list = new ArrayList<String>();
        list.add(null);        

        String jsonArray = JSONUtil.getInstance().convert(list);
        assertEquals("[ ]", jsonArray);
        
        assertEquals(Collections.EMPTY_LIST, JSONUtil.getInstance().convert(jsonArray));
    }


    /**
     * Test convert json array
     * 
     * @throws IOException In case of I/O exception 
     */
    @Test
    public void convertMap() throws IOException {
        Map<String, List<String>> map1 = new LinkedHashMap<>();
        map1.put("/", Arrays.asList("http://localhost/a", "http://localhost/b", "http://localhost/c"));
        map1.put("/abs", Arrays.asList("http://localhost/c", "http://localhost/d", "http://localhost/e"));
        String json = JSONUtil.getInstance().write(map1, false);
        
        @SuppressWarnings("unchecked")
        Map<String, List<String>> map2 = JSONUtil.getInstance().read(Map.class, json);
        assertEquals(map1, map2);
        
        String testJson = "{\"/\":[\"http://localhost/a\",\"http://localhost/b\", \"http://localhost/c\"],\"/abs\":[\"http://localhost/c\",\"http://localhost/d\", \"http://localhost/e\"]}";
        @SuppressWarnings("unchecked")
        Map<String, List<String>> map3 = JSONUtil.getInstance().read(Map.class, testJson);
        assertEquals(map1, map3);
        
        String h = "{\"/tt/\":[\"http:localhost:8080/q/health\", \"http://localhost:8080/\"]}";
        @SuppressWarnings("unchecked")
        Map<String, List<String>> map4 = JSONUtil.getInstance().read(Map.class, h);
        assertEquals("{/tt/=[http:localhost:8080/q/health, http://localhost:8080/]}", map4.toString());
        
        //NetworkProxyNode n = new NetworkProxyNode();
        //("==>" + JSONUtil.getInstance().write(n, false));
        //INetworkProxyNode p = JSONUtil.getInstance().read(NetworkProxyNode.class, "{\"name\":\"\",\"resource\":\"/\",\"methods\":[\"GET\",\"PATCH\",\"PUT\",\"POST\",\"DELETE\"],\"instances\":[]}");
        //("==>" + p);
    }
}
