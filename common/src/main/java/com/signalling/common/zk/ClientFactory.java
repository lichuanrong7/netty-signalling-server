package com.example.common.zk;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class ClientFactory {

    /**
     *
     * @param connectString zk的连接地址
     * @return
     */
    public static CuratorFramework createSimple(String connectString){
        // 重试策略:第一次重试等待1s，第二次重试等待2s，第三次重试等待4s
        // 第一个参数：等待时间的基础单位，单位为毫秒
        // 第二个参数：最大重试次数
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000,3);
        // 获取 CuratorFramework 实例的最简单的方式
        // 第一个参数：zk的连接地址
        // 第二个参数：重试策略
        return CuratorFrameworkFactory.newClient(connectString, retryPolicy);
    }

    /**
     *
     * @param connectString zk的连接地址
     * @param retryPolicy 重试策略
     * @param timeoutMs 连接超時
     * @param sessionTimeoutMs
     * @return
     */
    public static CuratorFramework createWithOptions(String connectString,RetryPolicy retryPolicy,int timeoutMs,int sessionTimeoutMs){
        //builder 模式创建 CuratorFramework 实例
        return CuratorFrameworkFactory.builder()
                .connectString(connectString)
                .retryPolicy(retryPolicy)
                .connectionTimeoutMs(timeoutMs)
                .sessionTimeoutMs(sessionTimeoutMs)
                .build();
    }
}
