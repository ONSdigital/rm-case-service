package uk.gov.ons.ctp.response.lib.common.distributed;

import static java.util.concurrent.TimeUnit.SECONDS;

import org.redisson.api.RCountDownLatch;
import org.redisson.api.RedissonClient;

/** DistributedLatchManager implementation */
public class DistributedLatchManagerRedissonImpl extends DistributedManagerBase
    implements DistributedLatchManager {

  private Integer timeToWait;
  private RedissonClient redissonClient;

  /**
   * create the impl
   *
   * @param keyRoot latch will be stored with this prefix in its key.
   * @param redissonClient the client connected to the underlying redis serve.
   * @param timeToWait time to wait for latch to countdown to zero.
   */
  public DistributedLatchManagerRedissonImpl(
      String keyRoot, RedissonClient redissonClient, Integer timeToWait) {
    super(keyRoot);
    this.redissonClient = redissonClient;
    this.timeToWait = timeToWait;
  }

  @Override
  public boolean setCountDownLatch(String key, long instanceCount) {
    RCountDownLatch latch = redissonClient.getCountDownLatch(createGlobalKey(key));
    return latch.trySetCount(instanceCount);
  }

  @Override
  public void countDown(String key) {
    redissonClient.getCountDownLatch(createGlobalKey(key)).countDown();
  }

  @Override
  public boolean awaitCountDownLatch(String key) throws InterruptedException {
    return redissonClient.getCountDownLatch(createGlobalKey(key)).await(timeToWait, SECONDS);
  }

  @Override
  public boolean deleteCountDownLatch(String key) {
    return redissonClient.getCountDownLatch(createGlobalKey(key)).delete();
  }
}
