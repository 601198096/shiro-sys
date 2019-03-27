package cn.shiro.sys.configure;

import org.apache.shiro.codec.Base64;
import org.apache.shiro.codec.CodecSupport;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.util.ByteSource;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;

/**
 * @version 1.0
 * @Author: wanhao
 * @Description
 * @Date: Created in 15:37 2019/3/27
 */
public class MyByteSource implements ByteSource , Serializable {

    private byte[] bytes;
    private String cachedHex;
    private String cachedBase64;

    public MyByteSource() {
    }

    public MyByteSource(byte[] bytes) {
        this.bytes = bytes;
    }


    public MyByteSource(char[] chars) {
        this.bytes = CodecSupport.toBytes(chars);
    }


    public MyByteSource(String string) {
        this.bytes = CodecSupport.toBytes(string);
    }


    public MyByteSource(ByteSource source) {
        this.bytes = source.getBytes();
    }


    public MyByteSource(File file) {
        this.bytes = new MyByteSource.BytesHelper().getBytes(file);
    }


    public MyByteSource(InputStream stream) {
        this.bytes = new MyByteSource.BytesHelper().getBytes(stream);
    }

    public static boolean isCompatible(Object o) {
        return o instanceof byte[] || o instanceof char[] || o instanceof String ||
                o instanceof ByteSource || o instanceof File || o instanceof InputStream;
    }

    @Override
    public byte[] getBytes() {
        return this.bytes;
    }

    @Override
    public String toHex() {
        if ( this.cachedHex == null ) {
            this.cachedHex = Hex.encodeToString(getBytes());
        }
        return this.cachedHex;
    }

    @Override
    public String toBase64() {
        if ( this.cachedBase64 == null ) {
            this.cachedBase64 = Base64.encodeToString(getBytes());
        }
        return this.cachedBase64;
    }

    @Override
    public boolean isEmpty() {
        return this.bytes == null || this.bytes.length == 0;
    }

    @Override
    public String toString() {
        return toBase64();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ByteSource) {
            ByteSource bs = (ByteSource) o;
            return Arrays.equals(getBytes(), bs.getBytes());
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (this.bytes == null || this.bytes.length == 0) {
            return 0;
        }
        return Arrays.hashCode(this.bytes);
    }

    private static final class BytesHelper extends CodecSupport {

        /**
         * 嵌套类也需要提供无参构造器
         */
        private BytesHelper() {
        }

        public byte[] getBytes(File file) {
            return toBytes(file);
        }

        public byte[] getBytes(InputStream stream) {
            return toBytes(stream);
        }
    }
}
