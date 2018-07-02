package com.fibbery.commons.utils;

/**
 * @author fibbery
 * @date 18/2/7
 */
public class ShortLinkUtls {

    public static String[] chars;

    static {
        chars = new String[62];
        //26小写字母
        for(int i = 0; i < 26; i ++) {
            char init = 'a';
            chars[i] = String.valueOf(((char) (init + i)));
        }
        //26大写字母
        for(int i = 0; i < 26; i++) {
            char init = 'A';
            chars[26 + i] = String.valueOf((char) (init + i));
        }
        //10
        for(int i = 0; i < 10; i++) {
            chars[52 + i] = String.valueOf(i);
        }
    }

    /**
     * 获取原链接的短址，分为四部分防止碰撞
     * 获取目标链接的32位md5值
     * 分为四组，每组和 0x3FFFFFFF  ( 30 个1) 做与操作，然后分为6组，每组和 61 (@see chars )做与操作
     *
     * @param link
     * @return
     */
    public static String[] getShortLink(String link) {
        /*盐值混合*/
        String text = Md5Utils.md5(link + "fibbery");
        int group = text.length() / 8;
        String result[] = new String[group];
        for(int i = 0; i < group; i++) {
            String subText = text.substring(i, i + 8);
            long temp = Long.valueOf(subText, 16) & 0x3FFFFFFF;
            StringBuilder subResult = new StringBuilder();
            for(int j = 0; j < 6; j++) {
                long index = temp & 61;
                subResult.append(chars[(int) index]);
                temp = temp >> 5;
            }
            result[i] = subResult.toString();
        }
        return result;
    }


    public static void main(String[] args) {
        String[] shortLink = getShortLink("https://www.zhenai.com/login/userLogin");
        for (String link : shortLink) {
            System.out.println(link);
        }
    }
}
