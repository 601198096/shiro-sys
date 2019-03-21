package cn.shiro.sys.api;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @version 1.0
 * @Author: wanhao
 * @Description
 * @Date: Created in 14:08 2019/3/21
 */
@RequestMapping
@RestController
public class LoginApi {

    @GetMapping(value = "/login")
    public String login(String username , String pwd , boolean remember){
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username , pwd , remember);
        usernamePasswordToken.setRememberMe(true);
        SecurityUtils.getSubject().login(usernamePasswordToken);
        return "success";
    }
}
