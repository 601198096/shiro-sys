package cn.shiro.sys;

import cn.shiro.sys.configure.MyByteSource;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

/**
 * @version 1.0
 * @Author: wanhao
 * @Description
 * @Date: Created in 15:29 2019/3/27
 */
public class TestSale {

    public static void main(String[] args) {
        System.out.println("123".toCharArray());
        System.out.println(ByteSource.Util.bytes("liudehua"));
        SimpleHash simpleHash = new SimpleHash(Md5Hash.ALGORITHM_NAME, "123".toCharArray(), new MyByteSource("liudehua"), 2);
        System.out.println(simpleHash);
    }
}
