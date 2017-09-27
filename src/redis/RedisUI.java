package redis;

import com.intellij.openapi.util.text.StringUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

/**
 * Created by wdq on 17-1-17.
 */
public class RedisUI {
    private JPanel content;
    private JPanel jpanelTop;
    private JTextField textFieldSearch;
    private JButton buttonSerach;
    private JPanel jpanelLeft;
    private JScrollPane jScrollTree;
    private JTree jtree;
    private JPanel jpanelData;
    private JScrollPane scrollData;
    private JTextArea textAreaData;
    private JTextField textFieldIp;
    private JLabel ip;
    private JLabel port;
    private JTextField textFieldPort;
    private JButton buttonOk;
    private JPasswordField passwordField1;
    private JLabel jpwd;
    private JComboBox comboBox1;

    public static Integer width;
    public static Integer height;

    static {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        GraphicsConfiguration[] gc = gs[0].getConfigurations();
        Rectangle bounds = gc[0].getBounds();
        width = new Double(bounds.getWidth()).intValue();
        height = new Double(bounds.getHeight()).intValue();
    }

    public static void main(String[] args) {
        createUI();
    }

    public static void createUI(){
        JFrame frame = new JFrame("RedisUI");
        frame.setSize(width,height);
        RedisUI redisUI = new RedisUI();
        redisUI.jpanelLeft.setPreferredSize(new Dimension(frame.getWidth()/2,frame.getHeight()));
        redisUI.jScrollTree.setPreferredSize(new Dimension(frame.getWidth()/2,frame.getHeight()-55));
        //redisUI.jtree.setPreferredSize(new Dimension(frame.getWidth()/2,frame.getHeight()));

        //分别设置水平和垂直滚动条自动出现
        redisUI.jScrollTree.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        RedisJTreeUI redisJTreeUI = new RedisJTreeUI(redisUI.jpanelLeft, redisUI.textAreaData, redisUI.jtree);

        //添加事件
        redisUI.buttonOk.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String ip = redisUI.textFieldIp.getText();
                String port = redisUI.textFieldPort.getText();
                char[] password = redisUI.passwordField1.getPassword();
                if(StringUtil.isNotEmpty(ip)&&StringUtil.isNotEmpty(port)) {
                    System.out.println(new String(password));
                    RedisTools.initCommandLine(ip, Integer.parseInt(port), password!=null?new String(password):null);
                    redisUI.jtree.removeAll();
                    redisJTreeUI.init(ip);

                    //设置下拉选
                    Map<String, String> redisDbInfos = RedisTools.getRedisDbInfos();
                    redisDbInfos.forEach((k,v)-> redisUI.comboBox1.addItem(k));

                }
            }
        });

        redisUI.buttonSerach.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String selectedItem = (String)redisUI.comboBox1.getSelectedItem();
                String key = redisUI.textFieldSearch.getText();
                if(StringUtil.isNotEmpty(selectedItem) && StringUtil.isNotEmpty(key)) {
                    java.util.List<String> data = RedisTools.getKeys(Integer.parseInt(selectedItem.replace("db", "")), key);
                    redisJTreeUI.updateTreeData(selectedItem + "(" + data.size() + ")", data);
                }

            }
        });

        frame.setContentPane(redisUI.content);
        frame.setVisible(true);
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.pack();
    }

}
