package uk.gov.ons.ctp.response.lib.common.distributed;

import java.util.concurrent.TimeUnit;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;

/** DistributedAtomicLong implementation. */
public class DistributedAtomicLongRedissonImpl extends DistributedManagerBase
    implements DistributedAtomicLong {

  private Integer timeToLive = 0;
  private RedissonClient redissonClient;

  /**
   * Constructor
   *
   * @param keyRoot each distrubuted AtomicLong created will be stored with this prefix in its key
   * @param redissonClient the client connected to the underlying redis server
   * @param timeToLive the time that each AtomicLong will be allowed to live in seconds before the
   *     underlying redis server purges it. If minus number or zero will not have expiry set.
   */
  public DistributedAtomicLongRedissonImpl(
      String keyRoot, RedissonClient redissonClient, Integer timeToLive) {
    super(keyRoot);
    this.timeToLive = timeToLive;
    this.redissonClient = redissonClient;
  }

  @Override
  public long getValue(String key) {
    RAtomicLong value = setExpiry(key);
    return value.get();
  }

  @Override
  public void setValue(String key, long setValue) {
    RAtomicLong value = setExpiry(key);
    value.set(setValue);
  }

  @Override
  public long incrementAndGet(String key) {
    RAtomicLong value = setExpiry(key);
    return value.getAndIncrement();
  }

  @Override
  public long decrementAndGet(String key) {
    RAtomicLong value = setExpiry(key);
    return (value.get() > 0) ? value.getAndDecrement() : 0;
  }

  @Override
  public boolean delete(String key) {
    return redissonClient.getAtomicLong(createGlobalKey(key)).delete();
  }

  /**
   * Set the expiry if not already set and one required. If timeToLive is less than or equal to zero
   * do not set expiry and allow to remain until explicitly deleted.
   *
   * @param key the name of distributed AtomicLong.
   * @return RAtomicLong
   */
  private RAtomicLong setExpiry(String key) {
    RAtomicLong value = redissonClient.getAtomicLong(createGlobalKey(key));
    if (value.remainTimeToLive() < 0 && timeToLive >= 0) {
      value.expire(timeToLive, TimeUnit.SECONDS);
    }
    return value;
  }
}
