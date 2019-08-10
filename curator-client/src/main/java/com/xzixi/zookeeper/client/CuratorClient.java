package com.xzixi.zookeeper.client;

import com.xzixi.zookeeper.exception.CuratorClientException;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.zookeeper.CreateMode;

import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * zookeeper工具
 */
@Data
@NoArgsConstructor
@Slf4j
public class CuratorClient {

    private static final String DEFAULT_CHARSET = "utf8";
    private CuratorFramework client;
    private String charset = DEFAULT_CHARSET;

    public CuratorClient(CuratorFrameworkFactory.Builder builder) {
        if (builder==null) {
            throw new CuratorClientException("builder不能为null");
        }
        client = builder.build();
    }

    public CuratorClient(CuratorFrameworkFactory.Builder builder, String charset) {
        if (builder==null) {
            throw new CuratorClientException("builder不能为null");
        }
        client = builder.build();
        this.charset = charset;
    }

    public void init() {
        client.start();
        client.getConnectionStateListenable().addListener((client, state) -> {
            if (state==ConnectionState.LOST) {
                // 连接丢失
                log.info("lost session with zookeeper");
            } else if (state==ConnectionState.CONNECTED) {
                // 连接新建
                log.info("connected with zookeeper");
            } else if (state==ConnectionState.RECONNECTED) {
                // 重新连接
                log.info("reconnected with zookeeper");
            }
        });
    }

    public void stop() {
        client.close();
    }

    /**
     * 创建节点
     *
     * @param mode     节点类型
     *                 1、PERSISTENT 持久化目录节点，存储的数据不会丢失。
     *                 2、PERSISTENT_SEQUENTIAL顺序自动编号的持久化目录节点，存储的数据不会丢失
     *                 3、EPHEMERAL临时目录节点，一旦创建这个节点的客户端与服务器端口也就是session 超时，这种节点会被自动删除
     *                 4、EPHEMERAL_SEQUENTIAL临时自动编号节点，一旦创建这个节点的客户端与服务器端口也就是session 超时，这种节点会被自动删除，并且根据当前已近存在的节点数自动加 1，然后返回给客户端已经成功创建的目录节点名。
     * @param path     节点名称
     * @param nodeData 节点数据
     */
    public void createNode(CreateMode mode, String path, String nodeData) {
        try {
            // 使用creatingParentContainersIfNeeded()之后Curator能够自动递归创建所有所需的父节点
            client.create().creatingParentsIfNeeded().withMode(mode).forPath(path, nodeData.getBytes(Charset.forName(charset)));
        } catch (Exception e) {
            throw new CuratorClientException("注册出错", e);
        }
    }

    /**
     * 创建节点
     *
     * @param mode 节点类型
     *             1、PERSISTENT 持久化目录节点，存储的数据不会丢失。
     *             2、PERSISTENT_SEQUENTIAL顺序自动编号的持久化目录节点，存储的数据不会丢失
     *             3、EPHEMERAL临时目录节点，一旦创建这个节点的客户端与服务器端口也就是session 超时，这种节点会被自动删除
     *             4、EPHEMERAL_SEQUENTIAL临时自动编号节点，一旦创建这个节点的客户端与服务器端口也就是session 超时，这种节点会被自动删除，并且根据当前已近存在的节点数自动加 1，然后返回给客户端已经成功创建的目录节点名。
     * @param path 节点名称
     */
    public void createNode(CreateMode mode, String path) {
        try {
            // 使用creatingParentContainersIfNeeded()之后Curator能够自动递归创建所有所需的父节点
            client.create().creatingParentsIfNeeded().withMode(mode).forPath(path);
        } catch (Exception e) {
            throw new CuratorClientException("注册出错", e);
        }
    }

    /**
     * 删除节点数据
     *
     * @param path 节点名称
     */
    public void deleteNode(final String path) {
        try {
            deleteNode(path, true);
        } catch (Exception e) {
            throw new CuratorClientException("删除节点失败", e);
        }
    }

    /**
     * 删除节点数据
     *
     * @param path 节点名称
     * @param deleteChildre 是否删除子节点
     */
    public void deleteNode(final String path, Boolean deleteChildre) {
        try {
            if (deleteChildre) {
                // guaranteed()删除一个节点，强制保证删除,
                // 只要客户端会话有效，那么Curator会在后台持续进行删除操作，直到删除节点成功
                client.delete().guaranteed().deletingChildrenIfNeeded().forPath(path);
            } else {
                client.delete().guaranteed().forPath(path);
            }
        } catch (Exception e) {
            throw new CuratorClientException("删除节点失败", e);
        }
    }

    /**
     * 设置指定节点的数据
     *
     * @param path 节点名称
     * @param data 节点数据
     */
    public void setNodeData(String path, String data) {
        try {
            client.setData().forPath(path, data.getBytes(Charset.forName(charset)));
        } catch (Exception ex) {
            throw new CuratorClientException("设置节点数据失败", ex);
        }
    }

    /**
     * 获取指定节点的数据
     *
     * @param path 节点名称
     * @return 节点数据
     */
    public String getNodeData(String path) {
        try {
            return new String(client.getData().forPath(path), Charset.forName(charset));
        } catch (Exception e) {
            throw new CuratorClientException("获取指定节点的数据失败", e);
        }
    }

    /**
     * 获取数据时先同步
     *
     * @param path 节点名称
     * @return 节点数据
     */
    public String synNodeData(String path) {
        client.sync();
        return getNodeData(path);
    }

    /**
     * 判断节点是否存在
     *
     * @param path 节点名称
     * @return true 节点存在，false 节点不存在
     */
    public boolean isExistNode(final String path) {
        client.sync();
        try {
            return null!=client.checkExists().forPath(path);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取节点的子节点
     *
     * @param path 节点名称
     * @return 子节点集合
     */
    public List<String> getChildren(String path) {
        List<String> childrenList;
        try {
            childrenList = client.getChildren().forPath(path);
        } catch (Exception e) {
            throw new CuratorClientException("获取子节点出错", e);
        }
        return childrenList;
    }

    /**
     * 获取读写锁
     *
     * @param path 节点名称
     * @return 读写锁
     */
    public InterProcessReadWriteLock getReadWriteLock(String path) {
        return new InterProcessReadWriteLock(client, path);
    }

    /**
     * 监听数据节点的变化情况
     *
     * @param path 节点名称
     * @param listener 监听器
     * @param pool 线程池
     * @return 监听节点的TreeCache实例
     */
    public TreeCache watch(String path, TreeCacheListener listener, Executor pool) {
        TreeCache cache = new TreeCache(client, path);
        cache.getListenable().addListener(listener, pool);
        try {
            cache.start();
        } catch (Exception e) {
            throw new CuratorClientException("监听节点出错", e);
        }
        return cache;
    }

    /**
     * 监听数据节点的变化情况
     *
     * @param path 节点名称
     * @param listener 监听器
     * @return 监听节点的TreeCache实例
     */
    public TreeCache watch(String path, TreeCacheListener listener) {
        TreeCache cache = new TreeCache(client, path);
        cache.getListenable().addListener(listener);
        try {
            cache.start();
        } catch (Exception e) {
            throw new CuratorClientException("监听节点出错", e);
        }
        return cache;
    }

    /**
     * 取消监听节点
     *
     * @param cache 监听节点的TreeCache实例
     * @param listener 监听器
     */
    public void unwatch(TreeCache cache, TreeCacheListener listener) {
        if (cache==null) {
            throw new CuratorClientException("TreeCache实例不能为null");
        }
        cache.getListenable().removeListener(listener);
    }

}
