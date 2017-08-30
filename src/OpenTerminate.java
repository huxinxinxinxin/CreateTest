import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.ui.treeStructure.Tree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 * Created by hx-pc on 17-6-19.
 */
public class OpenTerminate extends AnAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenTerminate.class);

    private JFrame frame = new JFrame();
    List<ThElement> thElements;
    private static boolean toUpdate = false;
    private static Long fileDate;

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        File afile = new File(PathManager.getPluginsPath() + "/bb");
        if (!afile.exists()) {
            afile.mkdirs();
        }
        InputStream inputStream = this.getClass().getResourceAsStream("/test.sh");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(PathManager.getPluginsPath() + "/bb/test.sh"));
            FileUtil.copy(inputStream, fileOutputStream);
        } catch (IOException e) {
            LOGGER.error("{}", e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                LOGGER.error("{}", e);
            }
        }
        File file = createNewConfigFile(PathManager.getPluginsPath() + "/bb/config.sc");
        LOGGER.info("OpenTerminate:{}", file.getPath());
        if (fileDate == null) {
            fileDate = file.lastModified();
            toUpdate = true;
        } else {
            if (fileDate != file.lastModified()) {
                fileDate = file.lastModified();
                toUpdate = true;
            } else {
                toUpdate = false;
            }
        }
        thElements = getThElement(file);
        buildLocalAlias(thElements);
        frame = StaticBuildMethod.createMethodTree4OpenTerminate(thElements, new OTKeyListener(), new MyMouseListener());
    }

    private void buildLocalAlias(List<ThElement> thElements) {
        if (toUpdate) {
            File newFile = new File(System.getProperty("user.home") + "/.bash_my");
            if (!newFile.exists()) {
                File file = new File(System.getProperty("user.home") + "/.bashrc");
                try {
                    FileOutputStream fos = new FileOutputStream(file, true);
                    fos.write(("source ~/.bash_my").getBytes());
                } catch (IOException e) {
                    LOGGER.error("{}", e);
                }
                try {
                    newFile.createNewFile();
                } catch (IOException e) {
                    LOGGER.error("{}", e);
                }
            }
            try {
                FileOutputStream fos = new FileOutputStream(newFile);
                for (ThElement thElement : thElements) {
                    String alias = "alias " + thElement.getLabel() + "=" + "'expect " + PathManager.getPluginsPath() + "/bb/test.sh" +
                            " " + thElement.getUsername() + " " +
                            "" + thElement.getHost() + " " +
                            "" + thElement.getPassword() + "'\n";
                    fos.write(alias.getBytes());
                }
            } catch (IOException e) {
                LOGGER.error("{}", e);
            }
            try {
                String[] cmd = {"/bin/sh", "-c", "source "+System.getProperty("user.home")+"/.bashrc"};
                Runtime.getRuntime().exec(cmd);
            } catch (IOException e) {
                LOGGER.error("{}", e);
            }
        }
    }

    private File createNewConfigFile(String s) {
        File file = new File(s);
        try {
            if (!file.exists()) {
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                fos.write("testLogin xin.hu 2wsx3edc 192.168.31.116 测试登录本地".getBytes());
                fos.write("testLoginWithoutPasswd xin.hu ? 192.168.31.116 测试登录本地免密码".getBytes());
            }
        } catch (IOException e) {
            LOGGER.error("{}", e);
        }
        return file;
    }

    private void gnomeTerminal(String username, String password, String host) {
        try {
            String [] cmd={"/bin/sh", "-c", "terminator -x expect " + PathManager.getPluginsPath() + "/bb/test.sh" +
                    " "+username+" " +
                    ""+host+" " +
                    ""+password+""};
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            LOGGER.error("{}", e);
        }
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            LOGGER.error("{}", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                LOGGER.error("{}", e);
            }
        }

        return sb.toString();
    }

    public List<ThElement> getThElement(File file) {
        List<ThElement> res = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] re = line.split(" ");
                res.add(new ThElement(re[0], re[1], re[2], re[3], re[4]));
            }
        } catch (FileNotFoundException e) {
            LOGGER.error("{}", e);
        }
        return res;
    }

    class ThElement {
        private String label;
        private String username;
        private String password;
        private String host;
        private String desc;

        public ThElement(String label, String username, String password, String host, String desc) {
            this.label = label;
            this.username = username;
            this.password = password;
            this.host = host;
            this.desc = desc;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }

    class OTKeyListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == 10 && Objects.nonNull(e.getSource())) {
                doSomething(e);
            }
            if (e.getKeyCode() == 27) {
                frame.dispose();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }

    private void doSomething(InputEvent e) {
        String sel = "";
        for (TreePath treePath : ((Tree)e.getSource()).getSelectionPaths()) {
            if (treePath.getPath().length > 1) {
                sel = treePath.getPath()[1].toString();
            }
        }
        for (ThElement thElement : thElements) {
            if ((thElement.getLabel() + "["+thElement.getDesc()+"]").equals(sel)) {
                if (thElement.getPassword().equals("?")) {
                    terminator(thElement.getUsername(), thElement.getHost());
                } else {
                    gnomeTerminal(thElement.getUsername(), thElement.getPassword(), thElement.getHost());
                }
                break;
            }
        }
        if (frame != null) {
            frame.dispose();
        }
    }

    private void terminator(String username, String host) {
        try {
            String [] cmd={"/bin/sh", "-c", "terminator -x ssh "+username+"@"+host};
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            LOGGER.error("{}", e);
        }
    }

    class MyMouseListener implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                doSomething(e);
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
}
