package redis;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RedisJTreeUI implements TreeSelectionListener {

    //树组件
    protected JTree jTree;

    private JPanel jPanel;
    private JTextArea jTextArea;

    private DataKeys dataKeys;
    private DefaultMutableTreeNode root;

    public RedisJTreeUI(JPanel jPanel, JTextArea jTextArea, JTree jTree) {
        this.jPanel = jPanel;
        this.jTextArea = jTextArea;
        this.jTree = jTree;

        dataKeys = new DataKeys();
        //初始化默认链接
        //RedisTools.initCommandLine("192.168.31.121", 6379, null);
        //init("192.168.31.121");
    }

    private void createTreeData() {
        DefaultMutableTreeNode db;
        Map<String, String> redisDbInfos = RedisTools.getRedisDbInfos();
        for (Map.Entry<String, String> entry : redisDbInfos.entrySet()) {
            db = new DefaultMutableTreeNode(entry.getKey() + "(" + entry.getValue() + ")");
            List<String> keys = RedisTools.getKeys(Integer.parseInt(entry.getKey().replace("db", "")), "*");
            for (String key : keys) {
                db.add(new DefaultMutableTreeNode(key));
            }
            root.add(db);
        }
    }

    public void updateTreeData(String node, List<String> redisDbInfos) {
        root.removeAllChildren();

        DefaultMutableTreeNode db;
        db = new DefaultMutableTreeNode(node);
        for (String key : redisDbInfos) {
            db.add(new DefaultMutableTreeNode(key));
        }
        root.add(db);
        ((DefaultTreeModel)jTree.getModel()).reload();
        jTree.revalidate();

        //展开所有树
        for (int i = 0; i < jTree.getRowCount(); i++) {
            jTree.expandRow(i);
        }
    }


    //初始化
    public void init(String proot) {
        //树节点的相关数据
        root = new DefaultMutableTreeNode(proot);
        //树的数据模型
        DefaultTreeModel model = new DefaultTreeModel(root);
        createTreeData();
        //设置数据模型
        jTree.setModel(model);
        //添加事件
        jTree.addTreeSelectionListener(this);
        jTree.addTreeExpansionListener(new TreeExpansionListener() {
            @Override
            public void treeExpanded(TreeExpansionEvent event) {
                jPanel.revalidate();
            }

            @Override
            public void treeCollapsed(TreeExpansionEvent event) {
                jPanel.revalidate();
            }
        });

        jTree.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                //右键菜单操作
                if (e.getButton() == MouseEvent.BUTTON3) {
                    JPopupMenu textMenu = new JPopupMenu();
                    JMenuItem refresh = new JMenuItem("刷新");
                    JMenuItem delete = new JMenuItem("删除");
                    //事件监听
                    delete.addActionListener(e1 -> {
                        if (dataKeys.getKeys() != null) {
                            List<String> keys = dataKeys.getKeys();
                            System.out.println("delete=" + keys);
                            RedisTools.deleteKey(dataKeys.getDb(), keys.toArray(new String[keys.size()]));
                            //移除删除数据
                            //jTree.remove();
                            model.removeNodeFromParent(dataKeys.getNode());

                        }
                    });
                    refresh.addActionListener(e1 -> {
                        if (dataKeys.getKeys() != null) {
                            //刷新数据
                            root.removeAllChildren();
                            createTreeData();
                            model.reload();
                            jTree.revalidate();

                            //展开所有树
                            for (int i = 0; i < jTree.getRowCount(); i++) {
                                if (jTree.getRowBounds(i).getSize().getWidth() > 79) {
                                    jTree.expandRow(i);
                                }
                            }
                        }
                    });
                    textMenu.add(refresh);
                    textMenu.add(delete);
                    textMenu.show(e.getComponent(), e.getX(), e.getY());

                } else if (e.getClickCount() == 2) {
                    //双击事件
                    if (dataKeys.getKeys() != null) {
                        //System.out.println("show="+dataKeys.getSingleKey());
                        jTextArea.setText(FormatUtil.formatJson(RedisTools.getValue(dataKeys.getDb(), dataKeys.getSingleKey())));
                    }
                }
            }
        });

//        //滚动面板
//        JScrollPane jScrollPane = new JBScrollPane(jTree,
//                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
//                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
//        //添加树到滚动面板
//        jScrollPane.getViewport().add(jTree);
//        //添加到面板容器
//        jPanel.add(jScrollPane);
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {

        dataKeys.clear();
        List<String> keys = new ArrayList<>();
        //多选
        TreePath[] selectionPaths = jTree.getSelectionPaths();

        if (selectionPaths == null) return;

        for (TreePath treePath : selectionPaths) {
            DefaultMutableTreeNode node1 = (DefaultMutableTreeNode) treePath.getLastPathComponent();
            if (node1.getLevel() == 2) {
                //System.out.println(node1.getUserObject().toString());
                keys.add(node1.getUserObject().toString());
            }
        }
        dataKeys.setKeys(keys);

        //获取选择的节点
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree
                .getLastSelectedPathComponent();

        if (node.getLevel() == 0) {
            //一级节点 -- root
        } else if (node.getLevel() == 1) {
            //二级节点
            dataKeys.setNode(node);
        } else if (node.getLevel() == 2) {
            //三级节点
            //  jTextArea.setText(node.getUserObject().toString());
            String parent = node.getParent().toString();
            parent = parent.substring(0, parent.indexOf("("));
            dataKeys.setDb(Integer.parseInt(parent.replace("db", "")));
            dataKeys.setSingleKey(node.getUserObject().toString());
            dataKeys.setNode(node);
        }
    }
}