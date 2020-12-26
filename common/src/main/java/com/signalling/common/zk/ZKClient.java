package com.example.common.zk;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Slf4j
@Data
public class ZKClient {

    private CuratorFramework client;
    private static final String ZK_ADDRESS = "localhost:2181,localhost:2182,localhost:2183";
    //private static final String ZK_ADDRESS = "localhost:2181";



    private static ZKClient instance = null;

    private ZKClient(){

    }

    public static ZKClient getInstance(){
        if(instance == null){
            instance = new ZKClient();
            instance.init();
        }
        return instance;
    }


    public void init(){
        if( null!=client ){
            return;
        }
        //创建客户端
        //client = ClientFactory.createSimple(ZK_ADDRESS);
        client = ClientFactory.createWithOptions(ZK_ADDRESS,new ExponentialBackoffRetry(1000,3), 1000, 1000);
        client.start();
    }

    public void destroy(){
        CloseableUtils.closeQuietly(client);
    }

    /**
     * 创建节点
     * @param zkPath
     * @param data
     */
    public void createNode(String zkPath,String data){
        try {
            // 创建一个 ZNode 节点
            // 节点的数据为 payload
            String b = "to set content";
            byte[] payload = b.getBytes(StandardCharsets.UTF_8);
            if(data != null){
                payload = data.getBytes(StandardCharsets.UTF_8);
            }
            client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(zkPath,payload);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 删除节点
     * @param zkPath
     */
    public void deleteNode(String zkPath){
        try {
            if(!isNodeExist(zkPath)){
                return;
            }
            client.delete().forPath(zkPath);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 检查节点
     * @param zkPath
     * @return
     */
    public boolean isNodeExist(String zkPath){
        try {
            Stat stat = client.checkExists().forPath(zkPath);
            if( null == stat){
                log.info("Node does not exist:",zkPath);
                return false;
            } else {
                log.info("Node exists, stat is:", stat.toString());
                return true;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 创建 临时 顺序 节点
     * @param srcPath
     * @return
     */
    public String  createEphemeralSeqNode(String srcPath){
        try {
            //创建一个 ZNode 节点
            String path = client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(srcPath);

            return path;
        } catch (Exception e){
            e.printStackTrace();
        }
        return  null;
    }
}
