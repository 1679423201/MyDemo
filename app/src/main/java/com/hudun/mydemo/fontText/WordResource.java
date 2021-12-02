package com.hudun.mydemo.fontText;

/**
 * 文本测试类，用于存一些文本
 * @ClassName WordResource
 * @Description TODO
 * @Author YMD
 * @Date 2021/11/30 16:20
 * @Version 1.0
 */
public class WordResource {
    String message;

    String firstWord = "在使用本产品之前，请您阅读本产品相关的《用户协议》和《隐私政策》，点击“同意”表示您已经完全清楚并接受各条款。\");";
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
