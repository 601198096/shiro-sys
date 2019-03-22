package cn.shiro.sys.configure.redis;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @version 1.0
 * @Author: wanhao
 * @Description
 * @Date: Created in 9:50 2019/3/22
 */
public class RedisCache<K , V> implements Cache<K , V> {

    private String shiroPrefix = "shiro_prefix:";

    /**
     * description: 存活时间(默认300)
     * remark:
     * */
    private Long time = 300L;

    /**
     * description: 存活时间单位(默认秒)
     * remark:
     * */
    private TimeUnit timeUnit = TimeUnit.SECONDS;

    private RedisTemplate redisTemplate;

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public String getShiroPrefix() {
        return shiroPrefix;
    }

    public void setShiroPrefix(String shiroPrefix) {
        this.shiroPrefix = shiroPrefix;
    }

    public RedisCache(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public RedisCache(String shiroPrefix, RedisTemplate redisTemplate) {
        this.shiroPrefix = shiroPrefix;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public V get(K k) throws CacheException {
        if(ObjectUtil.isNotNull(k)){
            return (V) redisTemplate.opsForValue().get(this.shiroPrefix + k);
        }
        return null;
    }

    @Override
    public V put(K k, V v) throws CacheException {
        if(ObjectUtil.isNotNull(k) && ObjectUtil.isNotNull(v)){
            redisTemplate.opsForValue().set(this.shiroPrefix + k , v , time , timeUnit);
        }
        return v;
    }

    @Override
    public V remove(K k) throws CacheException {
        if(ObjectUtil.isNotNull(k)){
            V v = (V)redisTemplate.opsForValue().get(this.shiroPrefix + k);
            redisTemplate.delete(this.shiroPrefix + k);
            return v;
        }
        return null;
    }

    @Override
    public void clear() throws CacheException {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    @Override
    public int size() {
        return redisTemplate.getConnectionFactory().getConnection().dbSize().intValue();
    }

    @Override
    public Set<K> keys() {
        return redisTemplate.keys(this.shiroPrefix + "*");
    }

    @Override
    public Collection<V> values() {
        List<V> arrayList = CollUtil.newArrayList();
        Set<K> keys = this.keys();
        keys.forEach(k -> arrayList.add(this.get(k)));
        return arrayList;
    }
}
