package com.xzixi.zookeeper.autoconfigure;

import com.xzixi.zookeeper.zkclient.ZkClient;
import org.apache.curator.RetryPolicy;
import org.apache.curator.ensemble.EnsembleProvider;
import org.apache.curator.ensemble.fixed.FixedEnsembleProvider;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.framework.api.CompressionProvider;
import org.apache.curator.framework.imps.DefaultACLProvider;
import org.apache.curator.framework.imps.GzipCompressionProvider;
import org.apache.curator.retry.BoundedExponentialBackoffRetry;
import org.apache.curator.utils.DefaultZookeeperFactory;
import org.apache.curator.utils.ZookeeperFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(ZkClient.class)
@EnableConfigurationProperties(ZkClientProperties.class)
public class ZkClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CompressionProvider compressionProvider() {
        return new GzipCompressionProvider();
    }

    @Bean
    @ConditionalOnMissingBean
    public ZookeeperFactory zookeeperFactory() {
        return new DefaultZookeeperFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    public ACLProvider aclProvider() {
        return new DefaultACLProvider();
    }

    @Bean
    @ConditionalOnMissingBean
    public EnsembleProvider ensembleProvider(ZkClientProperties zkClientProperties) {
        return new FixedEnsembleProvider(zkClientProperties.getConnectionString());
    }

    @Bean
    @ConditionalOnMissingBean
    public RetryPolicy retryPolicy(ZkClientProperties zkClientProperties) {
        ZkClientProperties.RetryProperties retry = zkClientProperties.getRetry();
        return new BoundedExponentialBackoffRetry(retry.getBaseSleepTimeMs(), retry.getMaxSleepTimeMs(), retry.getMaxRetries());
    }

}
