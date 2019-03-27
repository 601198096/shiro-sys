package cn.shiro.sys.configure;

import cn.hutool.core.util.ObjectUtil;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @version 1.0
 * @Author: wanhao
 * @Description
 * @Date: Created in 14:39 2019/3/27
 */
public class MyCredentialsMatcher extends SimpleCredentialsMatcher {

    private Cache<String , AtomicInteger> cache;

    public MyCredentialsMatcher(CacheManager cacheManager) {
        this.cache = cacheManager.getCache("loginFailNum:");
    }

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        String username = token.getPrincipal().toString();
        AtomicInteger atomicInteger = cache.get(username);
        if(ObjectUtil.isNull(atomicInteger)){
            atomicInteger = new AtomicInteger(0);
            cache.put(username , atomicInteger);
        }
        int incrementAndGet = atomicInteger.incrementAndGet();
        cache.put(username , atomicInteger);
        //失败次数大于5不准再登录
        if(incrementAndGet > 5){
            /**
             * 此处可以添加数据库操作改变用户的登录状态
             * */
            throw new LockedAccountException();
        }
        boolean match = super.doCredentialsMatch(token, info);
        //只要登录成功就删除统计的失败次数
        if(match){
            cache.remove(username);
        }
        return match;

    }
}
