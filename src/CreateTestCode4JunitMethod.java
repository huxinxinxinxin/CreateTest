import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.ui.treeStructure.Tree;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;
import java.util.List;

/**
 * Created by hx-pc on 16-12-29.
 */
public class CreateTestCode4JunitMethod extends AnAction {

    public CreateTestCode4JunitMethod() {
        super("CreateTestCode4JunitMethod");
    }

    private PsiClass psiClass;
    private JFrame frame;

    @Override
    public void actionPerformed(AnActionEvent event) {
        if (Objects.nonNull(frame)) {
            frame.removeAll();
            frame = null;
        }
        JunitKeyListener keyListener = new JunitKeyListener();
        PsiFile psiFile = event.getData(PlatformDataKeys.PSI_FILE);
        createTestFile(psiFile);
        if (psiFile != null) {
            for (PsiClass psiClass : ((PsiJavaFileImpl) psiFile).getClasses()) {
                this.psiClass = psiClass;
                frame = StaticBuildMethod.createMethodTree(psiClass, keyListener);
            }
        }
    }

    private void createTestFile(PsiFile psiFile) {
    }

    private void copyToSystemClipboard(Map<String, CreateElement> createElementMap, PsiClass psiClass) {
        StringSelection stringSelection = new StringSelection(StaticBuildMethod.getMethodString(createElementMap, psiClass));
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, stringSelection);
        System.out.println("copyToSystemClipboard done");
    }

    class JunitKeyListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
        }
        @Override
        public void keyPressed(KeyEvent e) {
            List<String> selectMethod = new ArrayList<>();
            if (e.getKeyCode() == 10 && Objects.nonNull(e.getSource())) {
                for (TreePath treePath : ((Tree)e.getSource()).getSelectionPaths()) {
                    if (treePath.getPath().length > 1) {
                        selectMethod.add(treePath.getPath()[1].toString());
                    }
                }
                if (Objects.nonNull(psiClass)) {
                    Map<String, CreateElement> createElementMap = new LinkedHashMap<>();
                    for(PsiMethod psiMethod : psiClass.getMethods()) {
                        if (selectMethod.contains(psiMethod.getName())) {
                            createElementMap.put(psiMethod.getName(), StaticBuildMethod.buildCreateElement(psiMethod.getReturnTypeElement(), psiMethod.getParameterList().getParameters()));
                        }
                    }
                    copyToSystemClipboard(createElementMap, psiClass);
                }
                frame.hide();
            } else if (e.getKeyCode() >= 37 && e.getKeyCode() <= 40 || e.getKeyCode() == 17
                    || e.getKeyCode() == 18 || e.getKeyCode() == 65) {

            } else {
                frame.hide();
            }
        }
        @Override
        public void keyReleased(KeyEvent e) {
        }
    }
}
/**
 * 复制到剪切板
 StringSelection stringSelection = new StringSelection(psiFields.toString() + "\n a");
 Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, stringSelection);
 */