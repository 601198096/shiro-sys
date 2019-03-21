package cn.shiro.sys.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @version 1.0
 * @Author: wanhao
 * @Description
 * @Date: Created in 14:26 2019/3/21
 */
@RestController
@RequestMapping
public class UserApi {

    @GetMapping("/add")
    public String add(){
        return "add";
    }

    @GetMapping("/update")
    public String update(){
        return "update";
    }

    @GetMapping("/delete")
    public String delete(){
        return "delete";
    }

    @GetMapping("/get")
    public String get(){
        return "get";
    }
}
