import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.util.net.HTTPMethod;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by hx-pc on 16-12-29.
 */
public class CreateTestCode extends AnAction {

    public CreateTestCode() {
        super("CreateTestCode");
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        Map<String, CreateElement> map = new LinkedHashMap<>();
        PsiFile psiFile = event.getData(PlatformDataKeys.PSI_FILE);
        createTestFile(psiFile);
        if (psiFile != null) {
            for (PsiClass psiClass : ((PsiJavaFileImpl) psiFile).getClasses()) {
                String classApi = getClassApi(psiClass.getText());
                for(PsiMethod psiMethod : psiClass.getMethods()) {
                    Scanner scanner = new Scanner(psiMethod.getText());
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        if(line.contains("@GetMapping")){
                            map.put(psiMethod.getName(), buildCreateElement(classApi, line, HTTPMethod.GET, psiMethod.getParameterList().getParameters()));
                        } else if (line.contains("@PutMapping")) {
                            map.put(psiMethod.getName(), buildCreateElement(classApi, line, HTTPMethod.PUT, psiMethod.getParameterList().getParameters()));
                        } else if (line.contains("@DeleteMapping")) {
                            map.put(psiMethod.getName(), buildCreateElement(classApi, line, HTTPMethod.DELETE, psiMethod.getParameterList().getParameters()));
                        } else if (line.contains("@PostMapping")) {
                            map.put(psiMethod.getName(), buildCreateElement(classApi, line, HTTPMethod.POST, psiMethod.getParameterList().getParameters()));
                        }
                    }
                }
                copyToSystemClipboard(psiClass.getName(), map);
            }
        }
    }

    private void createTestFile(PsiFile psiFile) {
        System.out.println(psiFile.getVirtualFile().getPath());
    }

    private void copyToSystemClipboard(String name, Map<String, CreateElement> map) {
        String funcTestClassStr = "class " + name + "{\n";
        for (Map.Entry<String, CreateElement> entry : map.entrySet()) {
            funcTestClassStr += "\t@Test\n" +
                    "\tpublic void " + entry.getKey() + "() {\n" +
                    "\t\tdef headers = userLogin(accountInfo.dj)\n";
            if (entry.getValue().getHttpMethod() == HTTPMethod.GET) {
                funcTestClassStr += "\t\tdef query = [\n";
            } else {
                funcTestClassStr += "\t\tdef body = [\n";
            }
            for (String param : entry.getValue().getParams()) {
                String[] paramSplitSpace = param.split(" ");
                funcTestClassStr += "\t\t\t\"" + paramSplitSpace[paramSplitSpace.length - 1] + "\" : \"" + paramSplitSpace[paramSplitSpace.length - 2] + "\"\n";
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
            funcTestClassStr += "\t\t\tpath: \""+entry.getValue().getUrl()+"\",\n" +
                    "\t\t\tcontentType: JSON,\n";

            if (entry.getValue().getHttpMethod() == HTTPMethod.GET) {
                funcTestClassStr += "\t\t\tquery : query,\n";
            } else {
                funcTestClassStr += "\t\t\tbody : body,\n";
            }

            funcTestClassStr += "\t\t\theaders: headers)\n" +
                    "\t\tassert res.status == HttpStatus.SC_OK\n";
            funcTestClassStr += "\t}\n\n\n";
        }
        funcTestClassStr += "}";
        StringSelection stringSelection = new StringSelection(funcTestClassStr);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, stringSelection);
    }

    private CreateElement buildCreateElement(String classApi, String line, HTTPMethod httpMethod, PsiParameter...psiParameters) {
        CreateElement createElement = new CreateElement();
        createElement.setHttpMethod(httpMethod);
        createElement.setUrl("/api" + clearApi(classApi) + clearApi(line));
        String[] params = new String[psiParameters.length];
        for (int i = 0; i < psiParameters.length ; i++) {
            params[i] = psiParameters[i].getText();
        }
        createElement.setParams(params);
        return createElement;
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
            if(line.contains("@RequestMapping")){
                return line;
            }
        }
        return "";
    }

    class CreateElement {
        private String url;
        private HTTPMethod httpMethod;
        private String[] params;
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

        public String[] getParams() {
            return params;
        }

        public void setParams(String[] params) {
            this.params = params;
        }

        public String getResponse() {
            return response;
        }

        public void setResponse(String response) {
            this.response = response;
        }
    }

}


/**
 * 复制到剪切板
 StringSelection stringSelection = new StringSelection(psiFields.toString() + "\n a");
 Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, stringSelection);
 */