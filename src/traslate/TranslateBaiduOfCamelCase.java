package traslate;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import common.BaiduTranslate;
import common.SpellType;
import org.jetbrains.annotations.Nullable;

/**
 * Created by hx-pc on 17-9-4.
 */
public class TranslateBaiduOfCamelCase extends EditorAction {

    protected TranslateBaiduOfCamelCase() {
        super(new TranslateBaiduOfCamelCase.Handler());
    }

    public static class Handler extends EditorWriteActionHandler {
        public Handler() {
        }

        @Override
        public void executeWriteAction(Editor editor, @Nullable Caret caret, DataContext dataContext) {
            if (editor.getSelectionModel().hasSelection()) {
                String selectedText = editor.getSelectionModel().getSelectedText();
                BaiduTranslate.getResult(selectedText, SpellType.CAMEL_CASE);
            } else {

            }
        }


    }

}
