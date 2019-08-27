# 基于curator封装zookeeper常用api和springboot启动器

## 介绍

启动器基于springboot2.1.3.RELEASE

<a href="https://blog.csdn.net/qq_35433926" target="_blank">博客主页</a>

## 使用方法

### 直接使用

项目已经发布到maven中央仓库，直接在pom.xml中引用即可

```xml
<dependencies>
    <dependency>
        <groupId>com.xzixi</groupId>
        <artifactId>curator-client-spring-boot-starter</artifactId>
        <version>1.0</version>
    </dependency>
</dependencies>
```

### 修改后使用
1. 下载项目<br>
打开git bash窗口，执行命令`git clone git@gitee.com:xuelingkang/zookeeper.git`

2. 编译并安装到本地maven仓库<br>
进入工程目录，打开cmd窗口，执行命令`mvn clean install -Dmaven.test.skip=true`

3. 在自己的项目中引用

```xml
<dependencies>
    <dependency>
        <groupId>com.xzixi</groupId>
        <artifactId>curator-client-spring-boot-starter</artifactId>
        <version>1.0</version>
    </dependency>
</dependencies>
```

4. yml配置

```yaml
# curator配置
curator-client:
  # 连接字符串
  connection-string: docker:2181
  # 根节点
  namespace: xzixi
  # 节点数据编码
  charset: utf8
  # session超时时间
  session-timeout-ms: 60000
  # 连接超时时间
  connection-timeout-ms: 15000
  # 关闭连接超时时间
  max-close-wait-ms: 1000
  # 默认数据
  default-data: ""
  # 当半数以上zookeeper服务出现故障仍然提供读服务
  can-be-read-only: false
  # 自动创建父节点
  use-container-parents-if-available: true
  # 重试策略，默认使用BoundedExponentialBackoffRetry
  retry:
    max-sleep-time-ms: 10000
    base-sleep-time-ms: 1000
    max-retries: 3
  # 认证信息
  auth:
    scheme: digest
    auth: xzixi:xzixi
```

具体请看`curator-client-demo`案例工程

## 欢迎issue和star！
