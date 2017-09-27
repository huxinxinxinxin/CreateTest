package redis;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;


/**
 * Created by wdq on 17-1-14.
 */
public class RedisClient extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        //Messages.showDialog(anActionEvent.getProject(),"hello ","test",new String[]{Options.HEADER_STYLE_KEY},1,Messages.getInformationIcon());
        createUI();
    }

    private void createUI() {
//        MyJFrame jFrame = new MyJFrame();
//        jFrame.createFrame();
        RedisUI.createUI();
    }
}



