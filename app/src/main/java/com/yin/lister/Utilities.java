package com.yin.lister;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.yin.lister.obj.List;
import com.yin.lister.obj.ListItem;

import org.json.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Yin Family on 8/22/2018.
 */

public class Utilities {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String unescapeJSONString(String str) {
        str = str.replace("_P%ë5nN*", ".")
                .replace("_D%5nNë*", "$")
                .replace("_H%ë5Nn*", "#")
                .replace("_Oë5n%N*", "[")
                .replace("_5nN*C%ë", "]")
                .replace("*_S%ë5nN", "/");
        return str;
    }

    public static String escapeJSONString(String str) {
        str = str.replace(".", "_P%ë5nN*")
                .replace("$", "_D%5nNë*")
                .replace("#", "_H%ë5Nn*")
                .replace("[", "_Oë5n%N*")
                .replace("]", "_5nN*C%ë")
                .replace("/", "*_S%ë5nN");
        return str;
    }

    /**
     * Generates List object to add to list
     * @param str Value to add to firebase list
     */
    public static List getListObject(String str) {
        Log.d("LISTER:", "Adding [" + str + "] to list");

        List item = new List();
        item.setListName(Utilities.escapeJSONString(str));
        item.setAddedDate(dateFormat.format(new Date()));
        item.setAddedBy(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        return item;
    }

    /**
     * Generates ListItem object to add to list
     * @param str Value to add to firebase list
     */
    public static ListItem getListItem(String str) {
        Log.d("LISTER:", "Adding [" + str + "] to list");

        ListItem item = new ListItem();
        item.setItemName(Utilities.escapeJSONString(str));
        item.setAddedDate(dateFormat.format(new Date()));
        item.setAddedBy(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        return item;
    }
}
