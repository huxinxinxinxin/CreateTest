package redis;

import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import org.jdesktop.swingx.HorizontalLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by wdq on 17-1-14.
 */
public class MyJFrame extends JFrame {

    private JTextArea jTextArea;
    private JScrollPane scroll;
    private JPanel jPanelLeft;

    private JPanel jPanelTop;

    //定义表格
    private JTable table;
    //定义滚动条面板(用以使表格可以滚动)
    private JScrollPane scrollPane;
    //定义数据模型类的对象(用以保存数据)，
    private DefaultTableModel tableModel;

    public static Double width;
    public static Double height;

    static {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        GraphicsConfiguration[] gc = gs[0].getConfigurations();
        Rectangle bounds = gc[0].getBounds();
        width = bounds.getWidth();
        height = bounds.getHeight();
    }

    private void initUI() {

        //顶部panel
        jPanelTop = new JPanel();
        jPanelTop.setBackground(Color.red);

        //左边panel
        jPanelLeft = new JPanel(new HorizontalLayout());
        jPanelLeft.setSize(width.intValue() / 2, height.intValue());

        //右边数据内容
        jTextArea = new JTextArea("数据内容");
        jTextArea.setSize(width.intValue() / 2, height.intValue());
        jTextArea.setLineWrap(true);
        jTextArea.setWrapStyleWord(true);

        scroll = new JBScrollPane(jTextArea,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        //把定义的JTextArea放到JScrollPane里面去
        //添加树到滚动面板
        scroll.getViewport().add(jTextArea);

//        //分别设置水平和垂直滚动条自动出现
//        scroll.setHorizontalScrollBarPolicy(
//                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//        scroll.setVerticalScrollBarPolicy(
//                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

//        //分别设置水平和垂直滚动条总是出现
     /*   scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        //分别设置水平和垂直滚动条总是隐藏
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_NEVER);*/
    }

    private void createTable() {

        scrollPane = new JBScrollPane();
        // 定义表格列名数组
        String[] columnNames = {"A"};
        // 定义表格数据数组
        String[][] tableValues = {{"A1"}, {"A2"},
                {"A3"}, {"A4"}};

        // 创建指定表格列名和表格数据的表格模型类的对象
        tableModel = new DefaultTableModel(tableValues, columnNames);
        // 创建指定表格模型的表格
        table = new JBTable(tableModel);
        //设置 RowSorter(RowSorter 用于提供对 JTable 的排序和过滤)。
        table.setRowSorter(new TableRowSorter<>(tableModel));
        scrollPane.setViewportView(table);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Object vv = tableModel.getValueAt(table.getSelectedRow(), table.getSelectedColumn());
                jTextArea.setText(vv.toString() + "\n");//jTextArea.getText()+
            }
        });
    }

    public void createFrame() {
        initUI();
        JFrame jFrame = new JFrame("redis");
//        jFrame.setLayout(new GridLayout(1, 2));
        jFrame.setSize(width.intValue(), height.intValue());


        Font font = new Font("仿宋", Font.BOLD, 14);
        jFrame.setFont(font);

//        createTable();
//        jPanelLeft.add(scrollPane);

        //创建树
        new RedisJTree(jPanelLeft, jTextArea);

        //添加组件
        jFrame.add(jPanelTop,BorderLayout.NORTH);
        jFrame.add(jPanelLeft,BorderLayout.WEST);//, BorderLayout.NORTH
        jFrame.add(scroll,BorderLayout.CENTER);

//        jFrame.add(jPanelLeft);//, BorderLayout.WEST
//        jFrame.add(jTextArea);//, BorderLayout.EAST
        //jFrame.pack();
        jFrame.setVisible(true);
        //再加上这一句就可以把Frame放在最中间了
        jFrame.setLocationRelativeTo(null);
        //如果没有这一句，在点击关闭Frame的时候程序其实还是在执行状态中的，加上这一句才算是真正的把资源释放掉了
        jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    public static void main(String[] args) {
        MyJFrame jFrame = new MyJFrame();
        jFrame.createFrame();
    }
}
