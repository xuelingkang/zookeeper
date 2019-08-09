package com.xzixi.zookeeper.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "zkclient")
public class ZkClientProperties {

    private String connectionString;
    private String namespace;
    private String charset = "utf8";
    private int sessionTimeoutMs = 60000;
    private int connectionTimeoutMs = 15000;
    private int maxCloseWaitMs = 1000;
    private byte[] defaultData = "".getBytes(Charset.forName(charset));
    private boolean canBeReadOnly = false;
    private boolean useContainerParentsIfAvailable = true;
    private RetryProperties retry = new RetryProperties();
    private List<AuthProperties> auth = new ArrayList<>();

    @Data
    public class RetryProperties {
        private int maxSleepTimeMs = 10000;
        private int baseSleepTimeMs = 1000;
        private int maxRetries = 3;
    }

    @Data
    public class AuthProperties {
        private String scheme;
        private String auth;
    }

}
