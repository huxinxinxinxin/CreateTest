package redis;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;

/**
 * Created by wdq on 17-1-14.
 */
public class DataKeys {
    private DefaultMutableTreeNode node;
    private Integer db;
    private List<String> keys;
    private String singleKey;

    public void clear() {
        node = null;
        db = null;
        if (keys != null) {
            keys.clear();
        }

        singleKey = null;

    }

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }

    public String getSingleKey() {
        return singleKey;
    }

    public void setSingleKey(String singleKey) {
        this.singleKey = singleKey;
    }

    public Integer getDb() {
        return db;
    }

    public void setDb(Integer db) {
        this.db = db;
    }

    public DefaultMutableTreeNode getNode() {
        return node;
    }

    public void setNode(DefaultMutableTreeNode node) {
        this.node = node;
    }
}
