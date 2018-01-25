package redis;

import org.apache.commons.lang.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisDataException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by hx-pc on 17-1-14.
 */
public class RedisTools {

    //  redis-cli -h 192.168.31.121 -p 6379
    private static String commandline;
    private static Jedis jedis;

    //初始化命令行
    public static void initCommandLine(String ip, int port, String password) {
        jedis = new Jedis(ip, port);
        if (!StringUtils.isEmpty(password)) {
            jedis.auth(password);
        }
    }

    public static Map<String, String> getRedisDbInfos() {
        int dbCount = 0;
        Map<String, String> dbInfos = new LinkedHashMap<>();
        while (true) {
            try {
                if (jedis.select(dbCount).equals("OK")) {
                    dbInfos.put("db" + dbCount, jedis.dbSize().toString());
                    dbCount++;
                }
            } catch (JedisDataException exception) {
                break;
            }
        }
        return dbInfos;
    }

    public static List<String> getKeys(int dbCount, String key) {
        jedis.select(dbCount);
        return new ArrayList<>(jedis.keys("*" + key + "*"));
    }

    public static void deleteKeyLike(int dbCount, String key) {
        jedis.select(dbCount);
        for (String str : getKeys(dbCount, key)) {
            jedis.del(key);
        }
    }

    public static String getValue(int dbCount, String key) {
        jedis.select(dbCount);
        return jedis.get(key);
    }

    public static void deleteKey(int dbCount, String... keys) {
        jedis.select(dbCount);
        for (String str : keys) {
            jedis.del(str);
        }
    }
}