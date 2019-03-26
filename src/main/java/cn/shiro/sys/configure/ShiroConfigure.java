package cn.shiro.sys.configure;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.shiro.sys.configure.realm.AccountAuthorizingRealm;
import cn.shiro.sys.configure.redis.RedisCacheManager;
import cn.shiro.sys.configure.redis.StringObjectRedisTemplate;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * @version 1.0
 * @Author: wanhao
 * @Description
 * @Date: Created in 14:21 2019/3/21
 */
@Configuration
public class ShiroConfigure {

    /**
     * description: cookie对象;会话Cookie模板 ,默认为: rememberMe,我这设置为myRemember。如果要开启记住功能必须在subject的RememberMe设置为true
     * @param:
    * @param
     * @return {@link SimpleCookie}
     * createdBy:ending
     * created:2019/3/21
     * */
    public Cookie simpleCookie(){
        SimpleCookie simpleCookie = new SimpleCookie("myRemember");
        //1.为true后，只能通过http访问，javascript无法访问
        //2.防止xss读取cookie
        simpleCookie.setHttpOnly(true);
        //cookie存在1800秒,-1表示浏览器关闭时失效此Cookie
        simpleCookie.setMaxAge(1800);
        return simpleCookie;
    }
    /**
     * description: 这个地方有点坑，不是所有的base64编码都可以用，长度过大过小都不行，没搞明白，官网给出的要么0x开头十六进制，要么base64
     * @param:
    * @param
     * @return {@link CookieRememberMeManager}
     * createdBy:ending
     * created:2019/3/21
     * */
    @Bean
    public CookieRememberMeManager cookieRememberMeManager(){
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(simpleCookie());
        cookieRememberMeManager.setCipherKey(Base64.decode("4AvVhmFLUs0KTA3Kprsdag=="));
        return cookieRememberMeManager;
    }

    @Bean
    public AccountAuthorizingRealm myAuthorizingRealm(){
        AccountAuthorizingRealm accountAuthorizingRealm = new AccountAuthorizingRealm();
        //开启认证缓存
        accountAuthorizingRealm.setAuthenticationCachingEnabled(true);
        //名称对应ehcache-shiro.xml的名称
        accountAuthorizingRealm.setAuthenticationCacheName("authenticationCache:");
        //开启权限缓存
        accountAuthorizingRealm.setAuthorizationCachingEnabled(true);
        //名称对应ehcache-shiro.xml的名称
        accountAuthorizingRealm.setAuthorizationCacheName("authorizationCache:");
        return accountAuthorizingRealm;
    }

//    /**
//     * description: 内存缓存
//     * @param:
//    * @param
//     * @return {@link CacheManager}
//     * createdBy:ending
//     * created:2019/3/21
//     * */
//    @Bean
//    public CacheManager cacheManager(){
//        EhCacheManager ehCacheManager = new EhCacheManager();
//        ehCacheManager.setCacheManagerConfigFile("classpath:config/ehcache-shiro.xml");
//        return ehCacheManager;
//    }

    /**
     * description: redis缓存
     * @param:
    * @param redisTemplate
     * @return {@link CacheManager}
     * createdBy:ending
     * created:2019/3/22
     * */
    @Bean
    public CacheManager cacheManager(StringObjectRedisTemplate redisTemplate){
        return new RedisCacheManager(redisTemplate);
    }

    /**
     * description: session管理器
     * @param:
    * @param
     * @return {@link MySessionListener}
     * createdBy:ending
     * created:2019/3/25
     * */
    public MySessionListener mySessionListener(){
        return new MySessionListener();
    }
    public SimpleCookie simpleCookieId(){
        SimpleCookie simpleCookie = new SimpleCookie("sessionId");
        simpleCookie.setHttpOnly(true);
        simpleCookie.setMaxAge(1800);
        return simpleCookie;
    }
    @Bean
    public SessionDAO sessionDAO(){
        EnterpriseCacheSessionDAO enterpriseCacheSessionDAO = new EnterpriseCacheSessionDAO();
        enterpriseCacheSessionDAO.setActiveSessionsCacheName("shiro-activeSessionCache:");
        enterpriseCacheSessionDAO.setSessionIdGenerator(new JavaUuidSessionIdGenerator());
        return enterpriseCacheSessionDAO;
    }
    @Bean
    public SessionManager sessionManager(CacheManager cacheManager , SessionDAO sessionDAO){
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionListeners(CollUtil.newArrayList(this.mySessionListener()));
        sessionManager.setSessionIdCookie(this.simpleCookieId());
        sessionManager.setCacheManager(cacheManager);
        sessionManager.setSessionDAO(sessionDAO);
        //取消url 后面的 JSESSIONID
        sessionManager.setSessionIdUrlRewritingEnabled(false);

        /*
        * 下面目前只有使用内存缓存才有用，下次跟源码，使用内存缓存的时候记得将cacheName的':'去掉
        * */
        //全局会话超时时间（单位毫秒），默认30分钟  暂时设置为10秒钟 用来测试
        sessionManager.setGlobalSessionTimeout(1000 * 30);
        //是否开启删除无效的session对象  默认为true
        sessionManager.setDeleteInvalidSessions(true);
        //是否开启定时调度器进行检测过期session 默认为true
        sessionManager.setSessionValidationSchedulerEnabled(true);
        //设置session失效的扫描时间, 清理用户直接关闭浏览器造成的孤立会话 默认为 1个小时
        //设置该属性 就不需要设置 ExecutorServiceSessionValidationScheduler 底层也是默认自动调用ExecutorServiceSessionValidationScheduler
        //暂时设置为 5秒 用来测试
        sessionManager.setSessionValidationInterval(5000);
        return sessionManager;
    }

    @Bean
    public DefaultWebSecurityManager defaultWebSecurityManager(CookieRememberMeManager cookieRememberMeManager , CacheManager cacheManager , SessionManager sessionManager){
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        //设置cookie管理器
        defaultWebSecurityManager.setRememberMeManager(cookieRememberMeManager);
        //设置缓存管理器
        defaultWebSecurityManager.setCacheManager(cacheManager);
        //session管理器
        defaultWebSecurityManager.setSessionManager(sessionManager);
        defaultWebSecurityManager.setRealm(this.myAuthorizingRealm());
        return defaultWebSecurityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager defaultWebSecurityManager){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(defaultWebSecurityManager);
        //未认证成功后跳转的
        shiroFilterFactoryBean.setUnauthorizedUrl("/403");
        //登录页面
        shiroFilterFactoryBean.setLoginUrl("/login");
        HashMap<String, String> filterChainMap = CollUtil.newHashMap();
        //user表示配置记住我或认证通过可以访问的地址
        //anon:所有url都都可以匿名访问;
        //authc: 需要认证才能进行访问;
        //DefaultFilter枚举类能看到全部
        filterChainMap.put("/delete" , "user");
        filterChainMap.put("/add" , "user");
        filterChainMap.put("/get" , "anon");
        filterChainMap.put("/**" , "authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainMap);
        return shiroFilterFactoryBean;
    }
}
