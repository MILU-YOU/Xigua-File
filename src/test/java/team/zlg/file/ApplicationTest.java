package team.zlg.file;

import org.junit.platform.commons.util.StringUtils;
import team.zlg.file.mapper.UserMapper;
import team.zlg.file.model.User;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import team.zlg.file.util.AES_128;
import team.zlg.file.util.RSA;

import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class ApplicationTest {

/*
    @Autowired
    private UserMapper userMapper;
*/

/*
    @Test
    public void test1() {
        User user = new User();
        user.setUsername("用户名1");
        user.setPassword("密码1");
        user.setTelephone("手机号1");
        userMapper.insertUser(user);
        System.out.println("数据库字段查询结果显示");
        List<User> list = userMapper.selectUser();
        list.forEach(System.out::println);
    }
*/

 /*   @Test
    public void test2() {
        User user = new User();
        user.setUsername("用户名2");
        user.setPassword("密码2");
        user.setTelephone("手机号2");
        userMapper.insert(user);
        List<User> list = userMapper.selectList(null);
        System.out.println("数据库字段查询结果显示");
        list.forEach(System.out::println);
    }*/

    @Test
    public void AESTest() throws NoSuchAlgorithmException {
        String key = AES_128.generateKey();
        String IV = AES_128.generateIV();
        AES_128 aes = new AES_128(key,IV);
        aes.encrypted("E:\\Graduation Project\\加密解密\\测试文件\\西瓜.jpg", "E:\\Graduation Project\\加密解密\\加密文件\\encpic.png");
        aes.decrypted("E:\\Graduation Project\\加密解密\\加密文件\\encpic.png", "E:\\Graduation Project\\加密解密\\解密文件\\decpic.png");
    }

    @Test
    public void RSATest() throws UnsupportedEncodingException, RSA.pqException {
        int generateKeyFlag = 0;// 大质数p、q的产生方式，0：文件读入；1：随机产生
        int pqLength = 1024;// p、q长度（比特数）
        String e = "65537";// 公钥指数
        String originalText = "asdwrafasf";// 原文

        System.out.println("加密前：" + originalText);
        long start = System.currentTimeMillis();
        RSA rsa = new RSA(new BigInteger(e), generateKeyFlag, pqLength);
        BigInteger[] c = rsa.encryption(originalText);// 加密


        String s = Arrays.toString(c);
//        System.out.println("字符串输出：" + s);
        String[] split = s.split("");
        String[] s0 = new String[split.length-2];
        for(int i=0;i<split.length-2;i++){
            s0[i]=split[i+1];
        }
        for(int i=0;i<s0.length;i++){
            System.out.print(s0[i]);
        }
        StringBuffer s1 = new StringBuffer();
        for (String string : s0) {
            s1.append(string);
        }

        System.out.println("");
        String s2 = s1.toString();
        System.out.println("s2为" + s2);
        BigInteger bigInteger = new BigInteger(s2);
        System.out.println("该大数为：" + bigInteger);
        BigInteger[] bigIntegers = new BigInteger[1];
        bigIntegers[0] = bigInteger;
        System.out.println("大数数组第一个元素为：" + bigIntegers[0]);

        System.out.println();
        System.out.print("加密后：");
        for (int i = 0; i < c.length; i++) {
            System.out.println(c[i]);
        }

        System.out.println();
        System.out.println("解密后：" + rsa.decryption(bigIntegers));// 解密
    }
}