import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.net.HTTPMethod;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by hx-pc on 16-12-29.
 */
public class CreateTestCode4GroovyMethod extends AnAction {

    public CreateTestCode4GroovyMethod() {
        super("CreateTestCode4GroovyMethod");
    }

    private static List<String> excludeClass = Arrays.asList("");
    private PsiClass psiClass;
    private JFrame frame;
    private String basePath;
    private String realBasePath;

    @Override
    public void actionPerformed(AnActionEvent event) {
        GroovyKeyListener keyListener = new GroovyKeyListener();
        GroovyMouseListener mouseListener = new GroovyMouseListener();
        PsiFile psiFile = event.getData(PlatformDataKeys.PSI_FILE);
        createTestFile(psiFile);
        if (psiFile != null) {
            realBasePath = psiFile.getProject().getBasePath() + "/scm-common/src/main/java/com/social/credits/common/rest/";
            basePath = psiFile.getProject().getBasePath() + "/scm-common/target/classes/com/social/credits/common/rest/";
            for (PsiClass psiClass : ((PsiJavaFileImpl) psiFile).getClasses()) {
                this.psiClass = psiClass;
                frame = StaticBuildMethod.createMethodTree4Groovy(psiClass, keyListener, mouseListener);
                frame.setAutoRequestFocus(true);
                frame.setFocusable(true);
                frame.setFocusableWindowState(true);
            }
        }


    }

    private void createTestFile(PsiFile psiFile) {
        System.out.println(psiFile.getVirtualFile().getPath());
    }

    private String copyToSystemClipboard(Map<String, CreateElement> map, String message) {
        String funcTestClassStr = "\t/**\n" +
                "\t* " + message + "\n" +
                "\t*/\n";
        for (Map.Entry<String, CreateElement> entry : map.entrySet()) {
            funcTestClassStr += "\t@Test\n";
            if (entry.getKey().split("_").length > 1) {
                funcTestClassStr += "\tpublic void " + entry.getKey() + "() {\n" +
                        "\t\tdef headers = userLogin(accountInfo.dj)\n";
            } else {
                funcTestClassStr += "\tpublic void " + entry.getKey() + "200() {\n" +
                        "\t\tdef headers = userLogin(accountInfo.dj)\n";
            }
            if (entry.getValue().getHttpMethod() == HTTPMethod.GET) {
                funcTestClassStr += "\t\tdef query = [\n";
            } else {
                funcTestClassStr += "\t\tdef body = [\n";
            }
            for (String param : entry.getValue().getParams()) {
                String[] paramSplitSpace = param.split(" ");
                if (paramSplitSpace[paramSplitSpace.length - 2].equals("String")) {
                    funcTestClassStr += "\t\t\t\"" + paramSplitSpace[paramSplitSpace.length - 1] + "\" : \"" + paramSplitSpace[paramSplitSpace.length - 2] + "\"\n";
                } else if (paramSplitSpace[paramSplitSpace.length - 2].startsWith("List<")) {
                    funcTestClassStr += "\t\t\t\"" + paramSplitSpace[paramSplitSpace.length - 1] + "\" : [[" +
                            paramSplitSpace[paramSplitSpace.length - 2].substring(5, paramSplitSpace[paramSplitSpace.length - 2].length() - 1) + "]]\n";
                } else {
                    funcTestClassStr += "\t\t\t\"" + paramSplitSpace[paramSplitSpace.length - 1] + "\" : " + paramSplitSpace[paramSplitSpace.length - 2] + "\n";
                }
            }
            funcTestClassStr += "\t\t]\n";
            if (entry.getValue().getHttpMethod() == HTTPMethod.GET) {
                funcTestClassStr += "\t\tdef res = restClient().get(\n";
            } else if (entry.getValue().getHttpMethod() == HTTPMethod.PUT) {
                funcTestClassStr += "\t\tdef res = restClient().put(\n";
            } else if (entry.getValue().getHttpMethod() == HTTPMethod.DELETE) {
                funcTestClassStr += "\t\tdef res = restClient().delete(\n";
            } else if (entry.getValue().getHttpMethod() == HTTPMethod.POST) {
                funcTestClassStr += "\t\tdef res = restClient().post(\n";
            }
            funcTestClassStr += "\t\t\tpath: \"" + entry.getValue().getUrl() + "\",\n" +
                    "\t\t\tcontentType: JSON,\n";

            if (entry.getValue().getHttpMethod() == HTTPMethod.GET || entry.getValue().getHttpMethod() == HTTPMethod.DELETE) {
                funcTestClassStr += "\t\t\tquery : query,\n";
            } else {
                funcTestClassStr += "\t\t\tbody : body,\n";
            }

            funcTestClassStr += "\t\t\theaders: headers)\n";
            if (entry.getKey().split("_").length > 1) {
                funcTestClassStr += "\t\tassert res.status == " + entry.getKey().split("_")[1] + "\n";
            } else {
                funcTestClassStr += "\t\tassert res.status == 200\n";
            }

            funcTestClassStr += "\t}\n\n\n";
        }
        return funcTestClassStr;
    }

    private CreateElement buildCreateElement(String classApi, String line, HTTPMethod httpMethod, PsiParameter... psiParameters) {
        CreateElement createElement = new CreateElement();
        createElement.setHttpMethod(httpMethod);
        createElement.setUrl("/api" + clearApi(classApi) + clearApi(line));
        List<String> params = new ArrayList<>();
        for (int i = 0; i < psiParameters.length; i++) {
            if (psiParameters[i].getType().getCanonicalText().startsWith("java.")) {
                params.add(psiParameters[i].getText());
            } else if (psiParameters[i].getType().getCanonicalText().startsWith("org.springframework.data.domain.Pageable")) {
                params.add("Long" + " index");
                params.add("Long" + " size");
            } else {
                String[] paramSplitSpace = psiParameters[i].getText().split(" ");
                String requestClassName = paramSplitSpace[paramSplitSpace.length - 2];
                doHandlerFile(params, realBasePath, requestClassName);
//                try {
//                    copyAllType(basePath);
//                    String[] paramSplitSpace = psiParameters[i].getText().split(" ");
//                    String requestClassName = paramSplitSpace[paramSplitSpace.length - 2];
//                    Class c = getRequestClass(basePath, requestClassName);
//                    Field[] fields = c.getDeclaredFields();
//                    buildParams(params, fields);
//                } catch (Exception e) {
//                    params.add(psiParameters[i].getText());
//                }
            }
        }
        createElement.setParams(params);
        return createElement;
    }

    private void doHandlerFile(List<String> params, String basePath, String requestClassName) {
        List<File> allFile = new ArrayList<>();
        scanFile(allFile, new File(basePath));
        for (File file : allFile) {
            if (file.getName().startsWith(requestClassName)) {
                try {
                    Scanner scanner = new Scanner(file);
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        if (line.contains("private") && line.contains(";")) {
                            String[] strings = line.trim().split(" ");
                            params.add(strings[1] + " " + strings[2].substring(0, strings[2].length() - 1));
                        }
                    }
                } catch (FileNotFoundException e) {
                }
                break;
            }
        }
    }

    private void copyAllType(String basePath) throws IOException, ClassNotFoundException {
        String path = basePath.substring(0, basePath.indexOf("/rest")) + "/type/";
        List<File> allFile = new ArrayList<>();
        scanFile(allFile, new File(path));
        for (File file : allFile) {
            loadClass(path, file);
        }
    }

    private Class loadClass(String path, File file) throws IOException, ClassNotFoundException {
        String requestPath = this.getClass().getResource("/").toString() + file.getAbsolutePath().substring(path.indexOf("com/"));
        File newFile = new File(requestPath.replace("file:", ""));
        if (!newFile.exists()) {
            FileUtil.createDirectory(newFile.getParentFile());
        }
        newFile.createNewFile();
        FileUtil.copy(file, newFile);
        String classPath = file.getAbsolutePath().substring(path.indexOf("com/")).replace("/", ".");
        try {
            return Class.forName(classPath.substring(0, classPath.lastIndexOf(".")));
        } finally {
            frame.dispose();
        }
    }

    private Class getRequestClass(String path, String requestClassName) throws ClassNotFoundException, IOException {
        try {
            File directory = new File(path);
            List<File> allFile = new ArrayList<>();
            scanFile(allFile, directory);
            for (File file : allFile) {
                if (file.getName().startsWith(requestClassName)) {
                    if (!file.getName().substring(0, file.getName().lastIndexOf(".")).contains("Exception")) {
                        return loadClass(path, file);
                    }
                }
            }
        } finally {
            frame.dispose();
        }
        throw new ClassNotFoundException();
    }

    private void scanFile(List<File> res, File directory) {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                scanFile(res, file.getAbsoluteFile());
            }
            if (!file.getName().contains("Exception") && file.isFile()) {
                res.add(file);
            }
        }
    }

    private void buildParams(List<String> params, Field[] fields) {
        for (Field field : fields) {
            Class typeClass = field.getType();
            if (typeClass.getCanonicalName().startsWith("java.")) {
                params.add(typeClass.getSimpleName() + " " + field.getName());
            } else {
                Field[] o = typeClass.getFields();
                buildParams(params, o);
            }
        }
    }

    private String clearApi(String string) {
        if (string.indexOf('/') != -1 && string.indexOf("\")") != -1) {
            return string.substring(string.indexOf('/'), string.indexOf("\")"));
        }
        return "";
    }

    private String getClassApi(String str) {
        Scanner scanner = new Scanner(str);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.contains("@RequestMapping")) {
                return line;
            }
        }
        return "";
    }

    class CreateElement {
        private String url;
        private HTTPMethod httpMethod;
        private List<String> params;
        private String response;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public HTTPMethod getHttpMethod() {
            return httpMethod;
        }

        public void setHttpMethod(HTTPMethod httpMethod) {
            this.httpMethod = httpMethod;
        }

        public List<String> getParams() {
            return params;
        }

        public void setParams(List<String> params) {
            this.params = params;
        }

        public String getResponse() {
            return response;
        }

        public void setResponse(String response) {
            this.response = response;
        }
    }

    class ResponseCode {
        private String methodName;
        private String code;

        public ResponseCode(String methodName, String code) {
            this.methodName = methodName;
            this.code = code;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

    class GroovyKeyListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == 10) {
                buildStart(e);
            }
            if (e.getKeyCode() == 27) {
                frame.dispose();
            }
        }


        @Override
        public void keyReleased(KeyEvent e) {
        }
    }

    class GroovyMouseListener implements MouseListener{
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                buildStart(e);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    private void buildStart(InputEvent e) {
        String re = "";
        List<String> selectMethod = new ArrayList<>();
        for (TreePath treePath : ((Tree) e.getSource()).getSelectionPaths()) {
            if (treePath.getPath().length > 1) {
                selectMethod.add(treePath.getPath()[1].toString());
            }
        }
        List<ResponseCode> responseCodes = selectMethod.stream().map(s -> {
            if (s.split("_").length > 1) {
                return new ResponseCode(s.split("_")[0], s.split("_")[1]);
            } else {
                return new ResponseCode(s, "");
            }
        }).collect(Collectors.toList());
        if (Objects.nonNull(e.getSource())) {
            String classApi = getClassApi(psiClass.getText());
            for (ResponseCode responseCode : responseCodes) {
                Map<String, CreateElement> map = new LinkedHashMap<>();
                for (PsiMethod psiMethod : psiClass.getMethods()) {
                    if (responseCode.getMethodName().equals(psiMethod.getName())) {
                        Scanner scanner = new Scanner(psiMethod.getText());
                        String result = "";
                        String resultLine = "";
                        String message = "";
                        String code = "";
                        while (scanner.hasNextLine()) {
                            String line = scanner.nextLine();
                            if (line.contains("@ApiResponse") && !line.contains("@ApiResponses") && line.contains(responseCode.getCode())) {
                                message = getMessage(line);
                                code = StaticBuildMethod.getCode(line);
                            }
                            if (line.contains("@GetMapping")) {
                                result = "GET";
                                resultLine = line;
                            } else if (line.contains("@PutMapping")) {
                                result = "PUT";
                                resultLine = line;
                            } else if (line.contains("@DeleteMapping")) {
                                result = "DELETE";
                                resultLine = line;
                            } else if (line.contains("@PostMapping")) {
                                result = "POST";
                                resultLine = line;
                            }
                        }
                        map.put(psiMethod.getName() + "_" + code.trim(), buildCreateElement(classApi, resultLine, HTTPMethod.valueOf(result), psiMethod.getParameterList().getParameters()));
                        re += copyToSystemClipboard(map, message);
                        re += "\n";
                    }
                }
            }
            frame.dispose();
        }
        StringSelection stringSelection = new StringSelection(re);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, stringSelection);
    }

    private String getMessage(String line) {
        if (StringUtils.isNotEmpty(line)) {
            return line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
        } else {
            return "";
        }
    }

}


/**
 * 复制到剪切板
 * StringSelection stringSelection = new StringSelection(psiFields.toString() + "\n a");
 * Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, stringSelection);
 */