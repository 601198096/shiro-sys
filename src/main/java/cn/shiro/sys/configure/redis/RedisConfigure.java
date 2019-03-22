package cn.shiro.sys.configure.redis;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;

import java.net.UnknownHostException;

/**
 * @version 1.0
 * @Author: wanhao
 * @Description
 * @Date: Created in 17:50 2019/3/22
 */
@Configuration
@ConditionalOnClass(RedisOperations.class)
public class RedisConfigure {

    @Bean
    @ConditionalOnMissingBean
    public StringObjectRedisTemplate stringObjectRedisTemplate(
            RedisConnectionFactory redisConnectionFactory) throws UnknownHostException {
        StringObjectRedisTemplate template = new StringObjectRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }
}
