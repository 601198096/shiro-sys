package cn.shiro.sys.configure;

import cn.hutool.core.util.ObjectUtil;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;
import java.util.Deque;
import java.util.LinkedList;

/**
 * @version 1.0
 * @Author: wanhao
 * @Description
 * @Date: Created in 18:28 2019/3/26
 */
public class SSOAccessControlFilter extends AccessControlFilter {

    /**
     * description: 被踢后回退的url
     * remark:
     * */
    private String kickUrl;

    /**
     * description: 最大登录数量
     * remark:
     */
    private Integer maxLoginNum = 1;

    /**
     * description: 缓存
     * remark:
     * */
    private Cache<String , Deque<Serializable>> cache;

    private SessionManager sessionManager;

    private final String ATTR_NAME = "killOut";

    public void setKickUrl(String kickUrl) {
        this.kickUrl = kickUrl;
    }

    public void setMaxLoginNum(Integer maxLoginNum) {
        this.maxLoginNum = maxLoginNum;
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void setCacheManager(CacheManager cacheManager){
        this.cache = cacheManager.getCache("shiro-activeSessionCache:");
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        Subject subject = getSubject(request , response);
        //如果未认证，继续下步认证
        if(!subject.isAuthenticated() && !subject.isRemembered()){
            return true;
        }
        String username = subject.getPrincipal().toString();
        //获取当前sessionId
        Serializable serializable = subject.getSession().getId();

        //缓存中存放的session集合
        Deque<Serializable> serializableDeque = cache.get(username);

        //如果没被包含在deque中，或者没被踢出，将当前session存入集合中
        if(ObjectUtil.isNull(subject.getSession().getAttribute(ATTR_NAME))){
            if(ObjectUtil.isNull(serializableDeque)){
                serializableDeque = new LinkedList<>();
                serializableDeque.push(serializable);
                cache.put(username , serializableDeque);
            }else{
                if(!serializableDeque.contains(serializable)){
                    serializableDeque.push(serializable);
                    cache.put(username , serializableDeque);
                }
            }
        }

        while (serializableDeque.size() > maxLoginNum){
            Serializable removeFirst = serializableDeque.removeLast();
            //获取移除的session对象
            Session session = null;
            try{
                session = sessionManager.getSession(new DefaultSessionKey(removeFirst));
            }catch (Exception e){
                System.out.println("会先在缓存中获取session，不然就会创建session，还会验证session是否已经过期");
            }
            //移除的session对象中设踢出的值
            if(ObjectUtil.isNotNull(session)){
                session.setAttribute(ATTR_NAME , true);
            }
            cache.put(username , serializableDeque);
        }
        //如果当前session存在killOut属性就退出登录
        if(ObjectUtil.isNotNull(subject.getSession().getAttribute(ATTR_NAME))){
            subject.logout();
            WebUtils.issueRedirect(request, response, kickUrl);
            return false;
        }
        return true;
    }
}
