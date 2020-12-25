package com.example.gate.balance;

import com.example.common.constants.ServerConstants;
import com.example.common.entity.ImNode;
import com.example.common.util.JsonUtil;
import com.example.common.zk.ZKClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
@Slf4j
@Service
public class ImLoadBalance {
    private CuratorFramework client = null;
    private String managerPath;
    public ImLoadBalance(){
        this.client = ZKClient.getInstance().getClient();
        managerPath = ServerConstants.MANAGE_PATH;
    }

    /**
     * 获取负载最小的IM节点
     * @return
     */
    public ImNode getBestWorker(){
        List<ImNode> workers = getWorkers();
        log.info("全部节点如下：");
        workers.stream().forEach(node -> {
            log.info("节点信息：{}", JsonUtil.pojoToJson(node));
        });
        ImNode best = balance(workers);
        return best;
    }

    /**
     * 按照负载排序
     * @param items 所有的节点
     * @return 负载最小的IM节点
     */
    protected ImNode balance(List<ImNode> items){
        if(items.size()>0){
            Collections.sort(items);//根据balance值由小到大排序
            ImNode node = items.get(0);//返回balance值最小的那个
            log.info("最佳的节点为：{}", JsonUtil.pojoToJson(node));
            return node;
        } else {
            return null;
        }
    }

    /**
     * 从zookeeper中拿到所有IM节点
     * @return
     */
    protected List<ImNode> getWorkers(){
        List<ImNode> workers = new ArrayList<ImNode>();
        List<String> children = null;
        try {
            children = client.getChildren().forPath(managerPath);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
        for (String child : children) {
            log.info("child:", child);
            byte[] payload = null;
            try {
                payload = client.getData().forPath(managerPath+"/"+child);
            } catch (Exception e){
                e.printStackTrace();
            }
            if( null == payload){
                continue;
            }
            ImNode worker = JsonUtil.jsonBytes2Object(payload,ImNode.class);
            workers.add(worker);
        };
        return workers;
    }

    public void removeWorkers(){
        try {
            client.delete().deletingChildrenIfNeeded().forPath(managerPath);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
