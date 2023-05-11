package team.zlg.file.util;
import java.security.SecureRandom;

public class AesUtil {
        public static String getRandomString(int length) {
            //String str = "zxcvbnmlkjhgfdsaqwertyuiopQWERTYUIOPASDFGHJKLZXCVBNM1234567890";
            String str = "0123456789abcdef";
            SecureRandom random1 = new SecureRandom();
            String res = "";
            for(int i =0;i<length;i++) {
                int number = random1.nextInt(16);
                res += str.charAt(number);
            }
            return res;
        }
}
