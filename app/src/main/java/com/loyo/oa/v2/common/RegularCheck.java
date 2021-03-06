package com.loyo.oa.v2.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式 检查
 * Created xnq 15/12/30.
 */
public class RegularCheck {
    protected RegularCheck() {
        throw new UnsupportedOperationException(); // 防止子类调用
    }


    /**
     * 判断为数字
     */
    public static boolean isNumeric(String str) {

        Pattern pattern = Pattern.compile("[0-9]*");

        Matcher isNum = pattern.matcher(str);

        if (!isNum.matches()) {
            return false;
        }

        return true;

    }


    /**
     * 邮箱格式验证
     *
     * @param email
     * @return 通过返回true
     */

    public static boolean checkEmail(String email) {

        boolean flag = false;
        try {
            String check = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";

            Pattern regex = Pattern.compile(check);

            Matcher matcher = regex.matcher(email);

            flag = matcher.matches();

        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * 云之讯,回拨座机号验证
     * 座机号：纯数字、0开头、长度10到12位
     * */
    public static boolean isYunTell(String str){
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^0[0-9]{9,11}$"); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }


    /**
     * 云之讯,回拨手机号验证
     * 手机号：纯数字、1开头、长度11位
     * */
    public static boolean isYunPhone(String str){
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^1[0-9]{10}$"); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }

    /**
     * 手机号验证
     *
     * @param str
     * @return 验证通过返回true
     */
    public static boolean isMobilePhone(String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][3,4,5,8][0-9]{9}$"); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }

    /**
     * 电话号码验证
     *
     * @param str
     * @return 验证通过返回true
     */
    public static boolean isPhone(String str) {
        Pattern p1 = null, p2 = null;
        Matcher m = null;
        boolean b = false;
        p1 = Pattern.compile("^[0][1-9]{2,3}-[0-9]{5,10}$");  // 验证带区号的
        p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$");         // 验证没有区号的
        if (str.length() > 9) {
            m = p1.matcher(str);
            b = m.matches();
        } else {
            m = p2.matcher(str);
            b = m.matches();
        }
        return b;
    }

    /*
	 * 验证号码 手机号 固话均可
	 *
	 */
    public static boolean isPhoneNumberValid(String phoneNumber) {
        boolean isValid = false;

        String expression = "((^(13|15|18)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))";
        CharSequence inputStr = phoneNumber;

        Pattern pattern = Pattern.compile(expression);

        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches() ) {
            isValid = true;
        }

        return isValid;

    }

}
