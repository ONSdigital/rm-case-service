package uk.gov.ons.ctp.response.lib.common.distributed;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;

/** DistributedInstanceManager implementation */
public class DistributedInstanceManagerRedissonImpl extends DistributedManagerBase
    implements DistributedInstanceManager {

  private RedissonClient redissonClient;

  /**
   * create the impl
   *
   * @param keyRoot instance count will be stored with this prefix in its key
   * @param redissonClient the client connected to the underlying redis server
   */
  public DistributedInstanceManagerRedissonImpl(String keyRoot, RedissonClient redissonClient) {
    super(keyRoot);
    this.redissonClient = redissonClient;
  }

  @Override
  public long getInstanceCount(String key) {
    return redissonClient.getAtomicLong(createGlobalKey(key)).get();
  }

  @Override
  public long incrementInstanceCount(String key) {
    RAtomicLong instanceCount = redissonClient.getAtomicLong(createGlobalKey(key));
    return instanceCount.getAndIncrement();
  }

  @Override
  public long decrementInstanceCount(String key) {
    RAtomicLong instanceCount = redissonClient.getAtomicLong(createGlobalKey(key));
    return (instanceCount.get() > 0) ? instanceCount.getAndDecrement() : 0;
  }
}
