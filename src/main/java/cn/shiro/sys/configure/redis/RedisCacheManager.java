package cn.shiro.sys.configure.redis;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @version 1.0
 * @Author: wanhao
 * @Description
 * @Date: Created in 9:49 2019/3/22
 */
public class RedisCacheManager implements CacheManager {

    public RedisTemplate redisTemplate;

    public RedisCacheManager(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public <K, V> Cache<K, V> getCache(String s) throws CacheException {
        return new RedisCache<>(redisTemplate);
    }
}
