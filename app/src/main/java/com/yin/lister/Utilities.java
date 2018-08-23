package com.yin.lister;

import org.json.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by Yin Family on 8/22/2018.
 */

public class Utilities {

    public static String unescapeJSONString(String str) {
        //try { str = URLDecoder.decode(str, "UTF-8"); } catch (Exception e) { /* ignore failure */}
        str = str.replace("_P%ë5nN*", ".")
                .replace("_D%5nNë*", "$")
                .replace("_H%ë5Nn*", "#")
                .replace("_Oë5n%N*", "[")
                .replace("_5nN*C%ë", "]")
                .replace("*_S%ë5nN", "/");
        return str;
    }

    public static String escapeJSONString(String str) {
        //try { str = URLEncoder.encode(str, "UTF-8"); } catch (Exception e) { /* ignore failure */}
        //str = JSONObject.quote(str);
        str = str.replace(".", "_P%ë5nN*")
                .replace("$", "_D%5nNë*")
                .replace("#", "_H%ë5Nn*")
                .replace("[", "_Oë5n%N*")
                .replace("]", "_5nN*C%ë")
                .replace("/", "*_S%ë5nN");
        return str;
    }
}
