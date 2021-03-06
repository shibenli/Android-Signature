package application;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import com.android.apksigner.ApkSignerTool;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;

public class MainController {
    @FXML
    Label tvTitle;

    @FXML
    TextField tfApkPath;

    @FXML
    TextField tfKeyStorePath;

    @FXML
    PasswordField pfKeyStorePwd;

    @FXML
    PasswordField pfAliasPwd;

    @FXML
    TextField tfAlias;

    @FXML
    TextArea taResult;

    @FXML
    CheckBox cbV2Signing;


    /**
     * 选择apk文件
     *
     * @param event
     */
    @FXML
    private void onChooseApk(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter apkFilter = new FileChooser.ExtensionFilter("安卓应用程序 (*.apk)", "*.apk");
        fileChooser.getExtensionFilters().add(apkFilter);
        File file = fileChooser.showOpenDialog(Main.getPrimaryStage());
        if (file != null) {
            System.out.println(file);
            tfApkPath.setText(file.getAbsolutePath());
        }
    }

    /**
     * 选择秘钥签名文件
     *
     * @param event
     */
    @FXML
    private void onChooseKeyStore(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter jksFilter = new FileChooser.ExtensionFilter("安卓签名文件 (*.jks)", "*.jks");
        FileChooser.ExtensionFilter keystoreFilter = new FileChooser.ExtensionFilter("安卓签名文件 (*.keystore)",
                "*.keystore");
        fileChooser.getExtensionFilters().add(jksFilter);
        fileChooser.getExtensionFilters().add(keystoreFilter);
        File file = fileChooser.showOpenDialog(Main.getPrimaryStage());
        if (file != null) {
            System.out.println(file);
            tfKeyStorePath.setText(file.getAbsolutePath());
        }
    }

    /**
     * 输入签名秘钥密码
     *
     * @param event
     */
    @FXML
    private void onKeyStorePwdEnd(KeyEvent event) {
        String msg = String.format("onKeyStorePwdEnd %s\n", pfKeyStorePwd.getText());
        System.out.printf(msg);
    }

    /**
     * 开始签名
     *
     * @param event
     */
    @FXML
    private void onGotoSignature(ActionEvent event) {

        taResult.clear();
        try {
        	ArrayList<String> params = new ArrayList<>();
        	params.add("--ks=" + tfKeyStorePath.getText());
        	params.add("--ks-key-alias=" + tfAlias.getText());
        	params.add("--key-pass=pass:" + pfAliasPwd.getText());
        	params.add("--ks-pass=pass:" + pfKeyStorePwd.getText());
        	params.add("--v2-signing-enabled=" + cbV2Signing.isSelected());
        	params.add("--in=" + tfApkPath.getText());
        	params.add("--out=asset\\sign_" + System.currentTimeMillis() + ".apk");
            String[] cmd = params.toArray(new String[0]);
            System.setErr(new PrintStream(new MyOutputStream(taResult)));
            taResult.appendText("开始签名...\n");

            ApkSignerTool.sign(cmd);

            taResult.appendText("签名成功\n");

        } catch (Exception e) {
            taResult.appendText(e.getMessage());
        } finally {
            System.setErr(System.err);
        }

    }

}

class MyOutputStream extends OutputStream {

    TextArea txtLog;

    public MyOutputStream(TextArea txtLog) {
        this.txtLog = txtLog;
    }

    public void write(int arg0) throws IOException {
        // 写入指定的字节，忽略
    }

    public void write(byte data[]) throws IOException {
        // 追加一行字符串
        txtLog.appendText(new String(data));
    }

    public void write(byte data[], int off, int len) throws IOException {
        TextArea txtLog = this.txtLog;
        // 追加一行字符串中指定的部分，这个最重要
        txtLog.appendText(new String(data, off, len));
        // 移动TextArea的光标到最后，实现自动滚动
        txtLog.positionCaret(txtLog.getText().length());
    }
}
