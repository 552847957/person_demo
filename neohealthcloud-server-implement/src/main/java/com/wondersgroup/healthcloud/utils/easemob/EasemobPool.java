package com.wondersgroup.healthcloud.utils.easemob;

import com.wondersgroup.healthcloud.common.utils.IdGen;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

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
@Component
public class EasemobPool {
  private static final Logger logger = LoggerFactory.getLogger(EasemobPool.class);
  private static final ExecutorService executor = new ThreadPoolExecutor(10, 10, 7L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
  static final String poolKey = "easemob:account:pool";
  private static final int poolSize = 100;

  private JedisPool pool;

  private EasemobAccountUtil util;

  private int checkSize() {
    try (Jedis jedis = pool.getResource()) {
      int count = jedis.llen(poolKey).intValue();
      return count > poolSize ? 0 : (poolSize - count);
    }
  }

  public void fullfillPool() {
    int numberToFill = checkSize();
    for (int i = 0; i < numberToFill; i++) {
      Runnable task = new EasemobAsyncCreateTask(this, true);
      executor.execute(task);
    }
  }

  public EasemobAccount fetchOne() {
    try (Jedis jedis = pool.getResource()) {
      String value = jedis.lpop(poolKey);
      if (value != null) {
        return fromValue(value);
      } else {
        CountDownLatch cdl = new CountDownLatch(1);
        EasemobAsyncCreateTask task = new EasemobAsyncCreateTask(this, false, cdl);
        executor.execute(task);
        if (cdl.await(3L, TimeUnit.SECONDS)) {
          return task.getResult();
        } else {
          return null;
        }
      }
    } catch (InterruptedException ex) {
      return null;
    } finally {
      Runnable task = new EasemobAsyncCreateTask(this, true);
      executor.execute(task);
    }
  }

  EasemobAccount createOne(Boolean toPool) {
    String id = IdGen.uuid();
    String pwd = IdGen.uuid();
    Boolean result = util.register(id, pwd, id);
    if (result) {
      if (toPool) {
        try (Jedis jedis = pool.getResource()) {
          jedis.rpush(poolKey, EasemobPool.generateValue(id, pwd));
        }
        logger.info(String.format("easemob account[%s] password[%s] createUser success", id, pwd));
        return null;
      } else {
        return new EasemobAccount(id, pwd);
      }
    } else {
      return null;
    }
  }

  static String generateValue(String id, String pwd) {
    return id + ":" + pwd;
  }

  static EasemobAccount fromValue(String value) {
    return new EasemobAccount(StringUtils.substring(value, 0, 32),
        StringUtils.substring(value, 33));//split by ":", two strings' length are both 32
  }

  @Autowired
  public void setPool(JedisPool pool) {
    this.pool = pool;
  }

  @Autowired
  public void setUtil(EasemobAccountUtil util) {
    this.util = util;
  }
}
