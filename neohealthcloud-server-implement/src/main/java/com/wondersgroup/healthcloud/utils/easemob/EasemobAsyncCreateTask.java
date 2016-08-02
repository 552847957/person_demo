package com.wondersgroup.healthcloud.utils.easemob;

import java.util.concurrent.CountDownLatch;

/**
 * ░░░░░▄█▌▀▄▓▓▄▄▄▄▀▀▀▄▓▓▓▓▓▌█
 * ░░░▄█▀▀▄▓█▓▓▓▓▓▓▓▓▓▓▓▓▀░▓▌█
 * ░░█▀▄▓▓▓███▓▓▓███▓▓▓▄░░▄▓▐█▌
 * ░█▌▓▓▓▀▀▓▓▓▓███▓▓▓▓▓▓▓▄▀▓▓▐█
 * ▐█▐██▐░▄▓▓▓▓▓▀▄░▀▓▓▓▓▓▓▓▓▓▌█▌
 * █▌███▓▓▓▓▓▓▓▓▐░░▄▓▓███▓▓▓▄▀▐█
 * █▐█▓▀░░▀▓▓▓▓▓▓▓▓▓██████▓▓▓▓▐█
 * ▌▓▄▌▀░▀░▐▀█▄▓▓██████████▓▓▓▌█▌
 * ▌▓▓▓▄▄▀▀▓▓▓▀▓▓▓▓▓▓▓▓█▓█▓█▓▓▌█▌
 * █▐▓▓▓▓▓▓▄▄▄▓▓▓▓▓▓█▓█▓█▓█▓▓▓▐█
 * <p/>
 * Created by zhangzhixiu on 15/12/4.
 */
public class EasemobAsyncCreateTask implements Runnable {

    private CountDownLatch cdl;
    private EasemobAccount result;
    private EasemobPool pool;
    private Boolean toPool;

    public EasemobAsyncCreateTask(EasemobPool pool, Boolean toPool) {
        this.pool = pool;
        this.toPool = toPool;
    }

    public EasemobAsyncCreateTask(EasemobPool pool, Boolean toPool, CountDownLatch cdl) {
        this.pool = pool;
        this.toPool = toPool;
        this.cdl = cdl;
    }

    @Override
    public void run() {
        this.result = pool.createOne(toPool);
        if (cdl != null) {
            cdl.countDown();
        }
    }

    public EasemobAccount getResult() {
        return result;
    }
}
