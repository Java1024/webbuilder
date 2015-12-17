package org.webbuilder.utils.common;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by 浩 on 2015-12-09 0009.
 */
public class UrlUtils {

    public static String urlEncode(String url, String charset) throws UnsupportedEncodingException {
        char[] charArr = url.toCharArray();
        for (int i = 0; i < charArr.length; i++) {
            String str = String.valueOf(charArr[i]);
            if (StringUtils.containsChineseChar(str)) {
                url = url.replace(str, URLEncoder.encode(str, charset));
            }
        }
        return url.replaceAll("[ ]", "%20");
    }

}
