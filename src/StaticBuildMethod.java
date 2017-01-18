import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiTypeElement;
import com.intellij.ui.treeStructure.Tree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.KeyListener;
import java.util.Map;

/**
 * Created by hx-pc on 17-1-6.
 */
public class StaticBuildMethod {

    public static JFrame createMethodTree(PsiClass psiClass, KeyListener keyListener) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        GraphicsConfiguration[] gc = gs[0].getConfigurations();
        Rectangle bounds = gc[0].getBounds();
        JFrame frame = new JFrame("createMethodTree");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(psiClass.getName());
        for (PsiMethod psiMethod : psiClass.getMethods()) {
            if (psiMethod.getText().contains("public")) {
                node.add(new DefaultMutableTreeNode(psiMethod.getName()));
            }
        }
        Tree tree = new Tree(node);
        tree.addKeyListener(keyListener);
        Font font = new Font(null, 0, 5);
        frame.setFont(font);
        frame.setSize(500, 500);
        frame.setLocation(new Double(bounds.getWidth()).intValue()/2 - 250, new Double(bounds.getHeight()).intValue()/2 - 250);
        frame.getContentPane().add(tree);
        frame.setVisible(true);
        frame.show();
        return frame;
    }

    public static String getMethodString(Map<String, CreateElement> createElementMap, PsiClass psiClass) {
        String result = "";
        for (Map.Entry<String, CreateElement> elementEntry: createElementMap.entrySet()) {
            result += getLine(1,"",1) +
                    getLine(1, "@Test", 1) +
                    getLine(1, "public void test_"+elementEntry.getKey()+"() {", 1) +
                    getLine(2, "try {", 1);
            if (!elementEntry.getValue().getResponse().equals("void")) {
                result += getLine(3, elementEntry.getValue().getResponse() + " response = " + toLowerFirstCode(psiClass.getName()) + "." + elementEntry.getKey() + "(" + writeParam(elementEntry.getValue().getParamTypes()) + ");", 1) +
                        getLine(3, "assert response != null;", 1) ;
            } else {
                result += getLine(3, toLowerFirstCode(psiClass.getName()) + "." + elementEntry.getKey() + "(" +  writeParam(elementEntry.getValue().getParamTypes()) + ");", 1);

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
        for (int i = 0; i < psiParameters.length ; i++) {
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
        for (int i = 0; i < count ; i ++) {
            result += "\n";
        }
        return result;
    }

    public static String getTab(int count) {
        String result = "";
        for (int i = 0; i < count ; i ++) {
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
                paramStr += "new " + params[i].split("\\.")[ params[i].split("\\.").length - 1] + "()";
            }
            if (i != params.length -1) {
                paramStr += ", ";
            }
        }
        return paramStr;
    }

}
