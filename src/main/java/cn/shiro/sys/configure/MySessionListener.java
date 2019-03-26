package cn.shiro.sys.configure;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @version 1.0
 * @Author: ending
 * @Description session监听器
 * @Date: Created in 17:55 2019/3/25
 */
public class MySessionListener implements SessionListener {

    private AtomicInteger atomicInteger = new AtomicInteger(0);

    @Override
    public void onStart(Session session) {
        atomicInteger.incrementAndGet();
    }

    @Override
    public void onStop(Session session) {
        atomicInteger.decrementAndGet();
    }

    /**
     * description: 过期Session
     * @param:
    * @param session
     * @return {@link }
     * createdBy:ending
     * created:2019/3/25
     * */
    @Override
    public void onExpiration(Session session) {
        atomicInteger.decrementAndGet();
    }

    public AtomicInteger getAtomicInteger() {
        return atomicInteger;
    }
}
