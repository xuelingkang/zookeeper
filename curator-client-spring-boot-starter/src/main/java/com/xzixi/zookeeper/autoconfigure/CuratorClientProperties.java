package com.xzixi.zookeeper.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "curator-client")
public class CuratorClientProperties {

    private String connectionString;
    private String namespace;
    private String charset = "utf8";
    private int sessionTimeoutMs = 60000;
    private int connectionTimeoutMs = 15000;
    private int maxCloseWaitMs = 1000;
    private String defaultData = "";
    private boolean canBeReadOnly = false;
    private boolean useContainerParentsIfAvailable = true;
    private String threadFactoryClassName;
    private Retry retry = new Retry();
    private Auth auth = new Auth();

    @Data
    public class Retry {
        private int maxSleepTimeMs = 10000;
        private int baseSleepTimeMs = 1000;
        private int maxRetries = 3;
    }

    @Data
    public class Auth {
        private String scheme = "digest";
        private String auth;
    }

}
