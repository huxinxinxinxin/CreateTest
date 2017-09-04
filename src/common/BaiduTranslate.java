package common;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * Created by hx-pc on 17-9-4.
 */
public class BaiduTranslate {

    public static void getResult(String selectedText, SpellType type) {
        Map<String, Object> map = new HashMap();
        map.put("key", selectedText);
        try {
            Map result = HttpClientUtil.get("", map, Map.class);
            String ste = ((List<Map>)result.get("trans_result")).get(0).get("dst").toString();
            StringSelection stringSelection = new StringSelection(handleStr(ste, type));
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, stringSelection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String handleStr(String ste, SpellType type) {
        StringBuilder re = new StringBuilder();
        if (type == SpellType.CAMEL_CASE) {
            for(int i = 0 ; i < ste.toCharArray().length; i++) {
                if (ste.toCharArray()[i] != ' ') {
                    re.append(ste.toCharArray()[i]);
                } else {
                    re.append((char)(ste.toCharArray()[i + 1] - 32));
                    i++;
                }
            }
            return re.toString();
        }
        return ste;
    }
}
