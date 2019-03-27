package cn.shiro.sys.configure.realm;

import cn.hutool.core.util.StrUtil;
import cn.shiro.sys.configure.MyByteSource;
import cn.shiro.sys.service.LoginService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @version 1.0
 * @Author: wanhao
 * @Description
 * @Date: Created in 14:15 2019/3/21
 */
public class AccountAuthorizingRealm extends AuthorizingRealm {

    @Autowired
    private LoginService loginService;

    /**
     * description: 授权
     * @param:
    * @param principals
     * @return {@link AuthorizationInfo}
     * createdBy:ending
     * created:2019/3/21
     * */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

    /**
     * description: 认证
     *
     * @param token
     * @return {@link AuthenticationInfo}
     * createdBy:ending
     * created:2019/3/21
     * @param:
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String username = token.getPrincipal().toString();
        String pwd = loginService.login(username);
        if(StrUtil.isNotBlank(pwd)){
            return new SimpleAuthenticationInfo(username , pwd , new MyByteSource(username) , getName());
        }
        return null;
    }

    @Override
    protected void clearCachedAuthorizationInfo(PrincipalCollection principals) {
        super.clearCachedAuthorizationInfo(principals);
    }

    @Override
    protected void doClearCache(PrincipalCollection principals) {
        super.doClearCache(principals);
    }

    @Override
    protected void clearCachedAuthenticationInfo(PrincipalCollection principals) {
        super.clearCachedAuthenticationInfo(principals);
    }

    @Override
    protected void clearCache(PrincipalCollection principals) {
        super.clearCache(principals);
    }

    public void clearAllCachedAuthorizationInfo() {
        getAuthorizationCache().clear();
    }

    public void clearAllCachedAuthenticationInfo() {
        getAuthenticationCache().clear();
    }

    public void clearAllCached() {
        this.clearAllCachedAuthorizationInfo();
        this.clearAllCachedAuthenticationInfo();
    }
}
