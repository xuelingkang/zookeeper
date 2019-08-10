package com.xzixi.zookeeper.curatorclientdemo;

import com.xzixi.zookeeper.client.CuratorClient;
import org.apache.zookeeper.CreateMode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CuratorClientDemoApplicationTests {

    @Autowired
    private CuratorClient curatorClient;

    @Test
    public void testCuratorClient() {
        curatorClient.createNode(CreateMode.EPHEMERAL, "/test/abc", "123");
        String data = curatorClient.getNodeData("/test/abc");
        System.out.println(data);
    }

}
