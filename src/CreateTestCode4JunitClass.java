import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.source.PsiJavaFileImpl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by hx-pc on 16-12-29.
 */
public class CreateTestCode4JunitClass extends AnAction {

    public CreateTestCode4JunitClass() {
        super("CreateTestCode4JunitClass");
    }

    private PsiClass psiClass;

    @Override
    public void actionPerformed(AnActionEvent event) {
        PsiFile psiFile = event.getData(PlatformDataKeys.PSI_FILE);
        FileWriter fileWriter = createTestFile((PsiJavaFile)psiFile);
        if (psiFile != null) {
            for (PsiClass psiClass : ((PsiJavaFileImpl) psiFile).getClasses()) {
                this.psiClass = psiClass;
                if (Objects.nonNull(psiClass)) {
                    Map<String, CreateElement> createElementMap = new LinkedHashMap<>();
                    for(PsiMethod psiMethod : psiClass.getMethods()) {
                        if (psiMethod.getText().contains("public")) {
                            createElementMap.put(psiMethod.getName(), StaticBuildMethod.buildCreateElement(psiMethod.getReturnTypeElement(), psiMethod.getParameterList().getParameters()));
                        }
                    }
                    try {
                        fileWriter.write(StaticBuildMethod.getMethodString4Junit(createElementMap, psiClass));
                        fileWriter.write(StaticBuildMethod.getLine(0,"}", 0));
                        fileWriter.flush();
                        fileWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private FileWriter createTestFile(PsiJavaFile psiFile) {
        String projectPath = psiFile.getProject().getBasePath();
        String packageName = psiFile.getPackageName();
        String newFilePath = projectPath + "/test/" + packageName.replace(".","/") + "/";
        new File(newFilePath).mkdirs();
        String className = psiFile.getClasses()[0].getName();
        File file = new File(newFilePath  + psiFile.getClasses()[0].getName() + "Test.java");
        FileWriter fileWriter = null;
        try {
            file.deleteOnExit();
            file.createNewFile();
            fileWriter = new FileWriter(file);
            fileWriter.write(StaticBuildMethod.getLine(0, "package " + packageName, 1));
            fileWriter.write(StaticBuildMethod.getLine(0, "@RunWith(SpringRunner.class)", 1));
            fileWriter.write(StaticBuildMethod.getLine(0, "@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = Application.class)", 1));
            fileWriter.write(StaticBuildMethod.getLine(0, "@TestPropertySource(\"classpath:application.properties\")", 1));
            fileWriter.write(StaticBuildMethod.getLine(0, "public class " +className +" {", 1));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileWriter;
    }
}
/**
 * 复制到剪切板
 StringSelection stringSelection = new StringSelection(psiFields.toString() + "\n a");
 Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, stringSelection);
 */