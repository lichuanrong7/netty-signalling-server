package com.example.common.cocurrent;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class FutureTaskScheduler extends Thread{

    private ConcurrentLinkedQueue<ExecuteTask> executeTaskQueue = new ConcurrentLinkedQueue<ExecuteTask>();
    private long sleepTime = 200;
    private ExecutorService pool = Executors.newFixedThreadPool(10);
    private static FutureTaskScheduler inst = new FutureTaskScheduler();
    private FutureTaskScheduler(){
        this.start();
    }
    public static void add(ExecuteTask task){
        inst.executeTaskQueue.add(task);
    }

    @Override
    public void run(){
        while (true) {
            handleTask();// 处理任务
            threadSleep(sleepTime);
        }
    }
    private void threadSleep(long time) {
        try {
            sleep(time);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 处理任务队列，检查其中是否有任务
     */
    private void handleTask() {
        try {
            ExecuteTask executeTask;
            while (executeTaskQueue.peek() != null) {
                executeTask = executeTaskQueue.poll();
                handleTask(executeTask);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 执行任务操作
     *
     * @param executeTask
     */
    private void handleTask(ExecuteTask executeTask) {
        pool.execute(new ExecuteRunnable(executeTask));
    }

    class ExecuteRunnable implements Runnable {
        ExecuteTask executeTask;

        ExecuteRunnable(ExecuteTask executeTask) {
            this.executeTask = executeTask;
        }

        public void run() {
            executeTask.execute();
        }
    }
}
