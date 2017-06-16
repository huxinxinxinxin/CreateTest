import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiTypeElement;
import com.intellij.ui.treeStructure.Tree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by hx-pc on 17-1-6.
 */
public class StaticBuildMethod {

    public static JFrame createMethodTree4Junit(PsiClass psiClass, KeyListener keyListener) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        GraphicsConfiguration[] gc = gs[0].getConfigurations();
        Rectangle bounds = gc[0].getBounds();
        JFrame frame = new JFrame("createJunitMethodTree");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                frame.requestFocus();
                super.windowOpened(e);
            }
        });
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(psiClass.getName());
        for (PsiMethod psiMethod : psiClass.getMethods()) {
            if (psiMethod.getText().contains("public")) {
                node.add(new DefaultMutableTreeNode(psiMethod.getName()));
            }
        }
        Tree tree = new Tree(node);
        tree.addKeyListener(keyListener);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        tree.setSelectionPath(tree.getPathForRow(1));
        Font font = new Font(null, 0, 5);
        frame.setFont(font);
        frame.setSize(500, 500);
        frame.setLocation(new Double(bounds.getWidth()).intValue() / 2 - 250, new Double(bounds.getHeight()).intValue() / 2 - 250);
        frame.getContentPane().add(tree);
        frame.setVisible(true);
        frame.show();
        return frame;
    }

    public static JFrame createMethodTree4Groovy(PsiClass psiClass, KeyListener keyListener, MouseListener mouseListener) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        GraphicsConfiguration[] gc = gs[0].getConfigurations();
        Rectangle bounds = gc[0].getBounds();
        JFrame frame = new JFrame("createGroovyMethodTree");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setFocusable(true);
        frame.setFocusableWindowState(true);
        frame.setAutoRequestFocus(true);
        frame.setAlwaysOnTop(true);
        frame.setFocusTraversalKeysEnabled(true);
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(psiClass.getName());
        for (PsiMethod psiMethod : psiClass.getMethods()) {
            Scanner scanner = new Scanner(psiMethod.getText());
            List<String> stringList = new ArrayList<>();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.contains("@ApiResponse") && !line.contains("@ApiResponses")) {
                    stringList.add(getCode(line));
                }
                if (line.contains("public")) {
                    if (stringList.size() == 0) {
                        node.add(new DefaultMutableTreeNode(psiMethod.getName()));
                    }
                    for (String str : stringList) {
                        node.add(new DefaultMutableTreeNode(psiMethod.getName() + "_" + str));
                    }
                    stringList = new ArrayList<>();
                }
            }
        }
        Tree tree = new Tree(node);
        tree.addKeyListener(keyListener);
        tree.addMouseListener(mouseListener);
        tree.setSelectionRow(1);
        Font font = new Font(null, 0, 5);
        frame.setFont(font);
        frame.setSize(500, 500);
        frame.setLocation(new Double(bounds.getWidth()).intValue() / 2 - 250, new Double(bounds.getHeight()).intValue() / 2 - 250);
        frame.getContentPane().add(tree);
        frame.setVisible(true);
        frame.show();
        return frame;
    }

    public static String getMethodString4Junit(Map<String, CreateElement> createElementMap, PsiClass psiClass) {
        String result = "";
        for (Map.Entry<String, CreateElement> elementEntry : createElementMap.entrySet()) {
            result += getLine(1, "", 1) +
                    getLine(1, "@Test", 1) +
                    getLine(1, "public void test_" + elementEntry.getKey() + "() {", 1) +
                    getLine(2, "try {", 1);
            if (!elementEntry.getValue().getResponse().equals("void")) {
                result += getLine(3, elementEntry.getValue().getResponse() + " response = " + toLowerFirstCode(psiClass.getName()) + "." + elementEntry.getKey() + "(" + writeParam(elementEntry.getValue().getParamTypes()) + ");", 1) +
                        getLine(3, "assert response != null;", 1);
            } else {
                result += getLine(3, toLowerFirstCode(psiClass.getName()) + "." + elementEntry.getKey() + "(" + writeParam(elementEntry.getValue().getParamTypes()) + ");", 1);

            }
            result += getLine(2, "} catch (Exception e) {", 1) +
                    getLine(3, "e.printStackTrace();", 1) +
                    getLine(2, "}", 1) +
                    getLine(1, "}", 1);

        }
        return result;
    }

    public static CreateElement buildCreateElement(PsiTypeElement psiTypeElement, PsiParameter... psiParameters) {
        CreateElement createElement = new CreateElement();
        createElement.setResponse(psiTypeElement.getText());
        String[] paramTypes = new String[psiParameters.length];
        for (int i = 0; i < psiParameters.length; i++) {
            paramTypes[i] = psiParameters[i].getType().getCanonicalText();
        }
        createElement.setParamTypes(paramTypes);
        return createElement;
    }

    public static String getLine(int tabCount, String text, int lnCount) {
        return getTab(tabCount) + text + getLn(lnCount);
    }

    public static String getLn(int count) {
        String result = "";
        for (int i = 0; i < count; i++) {
            result += "\n";
        }
        return result;
    }

    public static String getTab(int count) {
        String result = "";
        for (int i = 0; i < count; i++) {
            result += "\t";
        }
        return result;
    }

    public static String toLowerFirstCode(String str) {
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return new String(chars);
    }

    public static String writeParam(String[] params) {
        String paramStr = "";
        for (int i = 0; i < params.length; i++) {
            if (params[i].equals("java.lang.String")) {
                paramStr += "\"\"";
            } else {
                paramStr += "new " + params[i].split("\\.")[params[i].split("\\.").length - 1] + "()";
            }
            if (i != params.length - 1) {
                paramStr += ", ";
            }
        }
        return paramStr;
    }

    public static String getCode(String ln) {
        return ln.substring(ln.indexOf("=") + 1, ln.indexOf(","));
    }
}
