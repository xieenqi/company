package com.loyo.oa.v2.tool;

import java.util.regex.Pattern;

/**
 * com.loyo.oa.v2.tool
 * 描述 :正则表达式验证工具
 * 作者 : ykb
 * 时间 : 15/8/26.
 */
public class RegexUtil
{
    private static final String regexkEmaiL = "\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?";
    private static final String regexkIdCard = "[1-9]\\d{13,16}[a-zA-Z0-9]{1}";
    private static final String regexkMobileL = "(\\+\\d+)?1[3458]\\d{9}$";
    private static final String regexkPhone = "(\\+\\d+)?(\\d{3,4}\\-?)?\\d{7,8}$";
    ;
    private static final String regexkDigit = "\\-?[1-9]\\d+";
    ;
    private static final String regexkDecimals = "\\-?[1-9]\\d+(\\.\\d+)?";
    ;
    private static final String regexkBlankSpace = "\\s+";
    ;
    private static final String regexkChinese = "^[\u4E00-\u9FA5]+$";
    ;
    private static final String regexkURL = "(https?://(w{3}\\.)?)?\\w+\\.\\w+(\\.[a-zA-Z]+)*(:\\d{1,5})?(/\\w*)*(\\??(.+=.*)?(&.+=.*)?)?";
    ;
    private static final String regexkPostcode = "[1-9]\\d{5}";
    private static final String regexkIp = "\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b";
    private static final String regexkWx = "^[a-zA-Z\\d_]{6,}$";

    public enum StringType
    {
        /**
         * 邮箱格式 验证
         */
        EMAIL,

        /**
         * 身份证验证
         */
        IDCARD,

        /**
         * 移动电话 验证
         */
        MOBILEL,

        /**
         * 座机号码验证
         */
        PHONE,

        /**
         * 整数验证
         */
        DIGIT,

        /**
         * 验证整数和浮点数
         */
        DECIMALS,

        /**
         * 空白验证
         */
        BLANKSPACE,

        /**
         * 中文验证
         */
        CHINESE,

        /**
         * 互联网地址验证
         */
        URL,

        /**
         * 邮政编码验证
         */
        POSTCODE,

        /**
         * IP地址验证
         */
        IP,

        /**
         * 端口验证
         */
        PORT,

        /**
         * 微信验证
         **/
        WX,
    }

    /**
     * 正在表达式验证
     *
     * @param string     验证的指
     * @param stringType 枚举类型
     * @return (ture==验证成功，false==验证失败)
     */
    public static boolean regexk(String string, StringType stringType)
    {

        if (null == string || null == stringType) {
            return false;
        }
        String regexk = null;
        switch (stringType) {

            case EMAIL:
                regexk = regexkEmaiL;
                break;
            case IDCARD:
                regexk = regexkIdCard;
                break;
            case MOBILEL:
                regexk = regexkMobileL;
                break;
            case PHONE:
                regexk = regexkPhone;
                break;
            case DIGIT:
                regexk = regexkDigit;
                break;
            case DECIMALS:
                regexk = regexkDecimals;
                break;
            case BLANKSPACE:
                regexk = regexkBlankSpace;
                break;
            case CHINESE:
                regexk = regexkChinese;
                break;
            case URL:
                regexk = regexkURL;
                break;
            case POSTCODE:
                regexk = regexkPostcode;
                break;
            case IP:
                regexk = regexkIp;
                break;
            case WX:
                regexk = regexkWx;
                break;
            case PORT:
                int port = Integer.parseInt(string);
                if (port > 0 && port < 65535) {
                    return true;
                } else {
                    return false;
                }
        }

        return Pattern.matches(regexk, string);
    }
}
