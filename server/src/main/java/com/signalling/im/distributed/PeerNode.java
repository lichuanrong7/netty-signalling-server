package com.example.im.distributed;

import com.example.common.constants.ServerConstants;
import com.example.common.entity.ImNode;
import com.example.common.util.ObjectUtil;
import com.example.common.zk.ZKClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

public class PeerNode {

    private CuratorFramework client = null;
    private String pathRegistered = null;
    private ImNode node = null;
    private static PeerNode singleInstance = null;
    public static PeerNode getInst(){
        if(null==singleInstance){
            singleInstance = new PeerNode();
            singleInstance.client = ZKClient.getInstance().getClient();
            singleInstance.init();
        }
        return singleInstance;
    }

    private PeerNode(){}

    /**
     * 在zookeeper中创建临时节点
     */
    private void init() {
        createParentIfNeeded(ServerConstants.MANAGE_PATH);
        //创建一个 ZNode 节点
        //节点的 payload 为当前worker 实例
        try {
            pathRegistered = client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(ServerConstants.PATH_PREFIX);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean setNode(ImNode node){
        if( null==pathRegistered ){
            throw new RuntimeException("peernode init error");
        }
        //为node 设置id
        node.setId(getId());
        this.node = node;
        // 增加Node信息，并写回zookeeper
        while (true) {
            try {
                byte[] payload = ObjectUtil.Object2JsonBytes(node);
                client.setData().forPath(pathRegistered, payload);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    public long getId(){
        String sid = null;
        if( null==pathRegistered ){
            throw new RuntimeException("Node registration failed");
        }
        int index = pathRegistered.lastIndexOf(ServerConstants.PATH_PREFIX);
        if( index>=0 ){
            index += ServerConstants.PATH_PREFIX.length();
            sid = index <= pathRegistered.length() ? pathRegistered.substring(index) : null;
        }
        if( null==sid ){
            throw new RuntimeException("Node ID generation failed");
        }
        return Long.parseLong(sid);
    }

    public boolean incBalance(){
        if( null==node ){
            throw new RuntimeException("The node node has not been set");
        }
        //增加负载：增加负载，并写回zookeeper
        while (true){
            try {
                node.incrementBalance();
                byte[] payload = ObjectUtil.Object2JsonBytes(node);
                client.setData().forPath(pathRegistered, payload);
                return true;
            } catch (Exception e){
                return false;
            }
        }
    }

    public boolean decrBalance() {
        if (null == node) {
            throw new RuntimeException("The node node has not been set");
        }
        // 增加负载：增加负载，并写回zookeeper
        while (true) {
            try {
                node.decrementBalance();
                byte[] payload = ObjectUtil.Object2JsonBytes(node);
                client.setData().forPath(pathRegistered, payload);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    private void createParentIfNeeded(String managePath){
        try {
            Stat stat = client.checkExists().forPath(managePath);
            if(null == stat ){
                client.create()
                        .creatingParentsIfNeeded()
                        .withProtection()
                        .withMode(CreateMode.PERSISTENT)
                        .forPath(managePath);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }


}
