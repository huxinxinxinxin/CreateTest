package redis;

import com.intellij.openapi.util.text.StringUtil;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by hx-pc on 17-1-14.
 */
public class RedisTools {

//  redis-cli -h 192.168.31.121 -p 6379
    private static String commandline;

    //初始化命令行
    public static void initCommandLine(String ip,int port, String password, String redisHome){
        if (System.getProperty("os.name").equals(OsE.Linux.toString())) {
            commandline = "redis-cli -h "+ip+" -p "+port+(StringUtil.isEmpty(password)?"":" -a "+password);
            return;
        } else if (System.getProperty("os.name").equals(OsE.WINDOWS.toString())) {

        }
        throw new RuntimeException("不支持的操作系统, 目前支持" +
                "linux" +
                "");
    }

    public static Map<String, String> getRedisDbInfos() {
        Map<String, String> dbInfos = new LinkedHashMap<>();
        int dbCount = 0;
        while (true) {
            try {
                Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", commandline+" select "+ dbCount});
                BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String getStr = input.lines().toArray()[0].toString();
                input.close();
                if (getStr.equals("OK")) {
                    dbInfos.put("db" + dbCount, "0");
                    dbCount++;
                } else {
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", commandline+" info"});
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            Object[] lines = input.lines().toArray();
            for (int i = lines.length -1; i >= 0; i--) {
                if (lines[i].toString().contains("Keyspace")) {
                    break;
                } else {
                    String key = lines[i].toString().split(":")[0];
                    String value = lines[i].toString().split(":")[1].split(",")[0].split("=")[1];
                    if (dbInfos.keySet().contains(key)) {
                        dbInfos.put(key, value);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dbInfos;
    }

    public static List<String> getKeys(int dbCount, String key) {
        try {
            List<String> list = new ArrayList<>();
            Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", commandline+" -n "+dbCount+" keys \""+key+"\" | xargs"});
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            Object[] lines = input.lines().toArray();
            Arrays.asList(lines).forEach(arr -> {
                for (String str : arr.toString().split(" ")) {
                    list.add(str);
                }
            });
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null;
    }

    public static void deleteKeyLike(int dbCount, String key) {
        try {
            for(String str : getKeys(dbCount, key)) {
                Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", commandline+" -n "+dbCount+" del " + str});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getValue(int dbCount, String key) {
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", commandline+" -n "+dbCount+" get \""+key+"\""});
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return Arrays.toString(input.lines().toArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void deleteKey(int dbCount, String...keys) {
        try {
            for(String str : keys) {
                Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", commandline+" -n "+dbCount+" del " + str});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}