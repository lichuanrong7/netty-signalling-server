package com.example.common.cocurrent;

public interface CallbackTask<R>{
    R execute() throws Exception;
    void onBack(R r);
    void onException(Throwable e);
}
