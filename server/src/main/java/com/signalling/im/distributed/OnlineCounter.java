package com.example.im.distributed;

import com.example.common.constants.ServerConstants;
import com.example.common.zk.ZKClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong;
import org.apache.curator.retry.RetryNTimes;


/**
 * 分布式计数器
 */
@Slf4j
@Data
public class OnlineCounter {

    private static final String PATH = ServerConstants.COUNTER_PATH;

    private CuratorFramework client = null;

    private static OnlineCounter instance = null;

    private OnlineCounter() {

    }

    DistributedAtomicLong distributedAtomicLong = null;

    private long curValue;

    public static OnlineCounter getInst(){
        if(instance == null){
            instance = new OnlineCounter();
            instance.client = ZKClient.getInstance().getClient();
            instance.init();
        }
        return  instance;
    }

    private void init() {
        //分布式计数器，失败时重试10，每次间隔30毫秒
        distributedAtomicLong = new DistributedAtomicLong(client,PATH,new RetryNTimes(10,30));
    }

    /**
     * 增加计数
     * @return
     */
    public boolean increment(){
        boolean result = false;
        AtomicValue<Long> val = null;
        try{
            val = distributedAtomicLong.increment();
            result = val.succeeded();
            log.info("old cnt:"+val.preValue()+",new cnt:"+val.postValue()+",result:"+val.succeeded());
            curValue = val.postValue();
        } catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 减少计数
     * @return
     */
    public boolean decrement(){
        boolean result = false;
        AtomicValue<Long> val = null;
        try{
            val = distributedAtomicLong.decrement();
            result = val.succeeded();
            log.info("old cnt:"+val.preValue()+",new cnt:"+val.postValue()+",result:"+val.succeeded());
            curValue = val.postValue();
        } catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
