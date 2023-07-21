/*
 * NetworkProxyConfigurationParserTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.proxy.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;


/**
 * Test the {@link NetworkProxyConfigurationParser}.
 * 
 * @author patrick
 */
public class NetworkProxyConfigurationParserTest {

    /**
     * Test empty configuration 
     */
    @Test void test() {
        assertEquals(NetworkProxyConfigurationParser.getInstance().parse((String[])null).size(), 0);        
        assertEquals(NetworkProxyConfigurationParser.getInstance().parse("").size(), 0);        
        assertEquals(NetworkProxyConfigurationParser.getInstance().parse("    ").size(), 0);        
        assertEquals(NetworkProxyConfigurationParser.getInstance().parse("{}").size(), 0);        
        assertEquals(NetworkProxyConfigurationParser.getInstance().parse("{    }").size(), 0);        
    }
    
    
    /**
     * Test empty configuration 
     */
    @Test void testSimple() {
        List<NetworkProxyNode> referenceList = Arrays.asList(new NetworkProxyNode(null, "a", null, Arrays.asList(URI.create("http://localhost:1/k"), URI.create("http://localhost:2/l"))));
        assertEquals(NetworkProxyConfigurationParser.getInstance().parse("a=[http://localhost:1/k,http://localhost:2/l]"), referenceList);
        assertEquals(NetworkProxyConfigurationParser.getInstance().parse("  a = [  http://localhost:1/k , http://localhost:2/l ] "), referenceList);
        assertEquals(NetworkProxyConfigurationParser.getInstance().parse("a=http://localhost:1/k,http://localhost:2/l"), referenceList);
        assertEquals(NetworkProxyConfigurationParser.getInstance().parse("  a  =  http://localhost:1/k , http://localhost:2/l   "), referenceList);
    }


    /**
     * Test empty configuration 
     */
    @Test void testSimpleList() {
        List<NetworkProxyNode> referenceList = Arrays.asList(new NetworkProxyNode(null, "a", null, Arrays.asList(URI.create("http://localhost:1/k"), URI.create("http://localhost:2/l"))),
                                                             new NetworkProxyNode(null, "b", null, Arrays.asList(URI.create("http://localhost:3/k"), URI.create("http://localhost:4/l"))));
        assertEquals(NetworkProxyConfigurationParser.getInstance().parse("a=[http://localhost:1/k,http://localhost:2/l],b=[http://localhost:3/k,http://localhost:4/l]"), referenceList);
        assertEquals(NetworkProxyConfigurationParser.getInstance().parse("   a  =  [  http://localhost:1/k   ,   http://localhost:2/l  ]  ,  b  =  [   http://localhost:3/k  ,  http://localhost:4/l]   "), referenceList);
        assertEquals(NetworkProxyConfigurationParser.getInstance().parse("a=http://localhost:1/k,http://localhost:2/l,b=http://localhost:3/k,http://localhost:4/l"), referenceList);
        assertEquals(NetworkProxyConfigurationParser.getInstance().parse(" a  =  http://localhost:1/k   ,   http://localhost:2/l    ,   b  =  http://localhost:3/k  ,  http://localhost:4/l  "), referenceList);
    }
}
