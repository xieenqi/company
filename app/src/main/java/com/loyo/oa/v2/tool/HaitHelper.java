package com.loyo.oa.v2.tool;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.loyo.oa.v2.activityui.commonview.SelectDetUserActivity2;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.activityui.other.bean.User;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;

import java.util.ArrayList;
import java.util.List;


/**
 * 艾特功能EditText辅助类
 */
public class HaitHelper {
    private static final char SCANNER_HAIT_TRIM = '\u2005';

    private final List<SelectUser> mHaitSelectUsers = new ArrayList<>(); // 选择用于艾特的用户列表
    private final Fragment mFragment;

    private EditText et_scanner;
    private String oldScanner;

    public HaitHelper(Fragment fragment, EditText et_scanner) {
        this.et_scanner = et_scanner;
        this.mFragment = fragment;

        init();
    }

    public HaitHelper(EditText et_scanner) {
        this(null, et_scanner);
    }

    private void init() {
        et_scanner.addTextChangedListener(new MyTextWatcher());
        et_scanner.setOnKeyListener(new MyOnKeyListener());
    }

    private class MyTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence s, int i, int i1, int i2) {
            String newString = s.toString();
            boolean addNewEditAiTe = TextUtils.isEmpty(oldScanner) ? true : newString.length() > oldScanner.length();
            oldScanner = newString;
            int selection = et_scanner.getSelectionStart();
            int frontCharIndex = selection - 2;
            boolean isLetterDigit = frontCharIndex < 0 ?
                    false : isLetterDigit(oldScanner.charAt(frontCharIndex) + ""); // 判断'@'之前 的是否是字母和文字
            if (addNewEditAiTe
                    && oldScanner.length() != 0
                    && selection > 0
                    && '@' == oldScanner.charAt(selection - 1)
                    && !isLetterDigit) {
                toSelectUserByHait(); //跳转选择要艾特用户的界面
            }
        }

        /**
         * 当用户输入'@'是跳转选择艾特用户的界面
         */
        private void toSelectUserByHait() {
            Bundle bundle = new Bundle();
            bundle.putInt(ExtraAndResult.STR_SELECT_TYPE, ExtraAndResult.TYPE_SELECT_SINGLE);
            if (null == mFragment) {
                MainApp.getMainApp().startActivityForResult((Activity) et_scanner.getContext(),
                        SelectDetUserActivity2.class,
                        MainApp.ENTER_TYPE_RIGHT,
                        ExtraAndResult.REQUEST_CODE,
                        bundle);
            } else {
                mFragment.startActivityForResult(new Intent(mFragment.getActivity(),
                                SelectDetUserActivity2.class).putExtras(bundle),
                        ExtraAndResult.REQUEST_CODE);
            }
        }

        /**
         * 用于判断@前面的字符是否是数字、字母..., 如为数字、字母...不进入选择界面
         *
         * @param str
         * @return
         */
        public boolean isLetterDigit(String str) {
            String regex = "^[a-z0-9A-Z_\\-]+$";
            return str.matches(regex);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    /**
     * 监听EditText键盘按键，主要用于监听删除时间
     */
    public class MyOnKeyListener implements View.OnKeyListener {
        @Override
        public boolean onKey(View view, int keyCode, KeyEvent event) {
            int selection = et_scanner.getSelectionStart();
            if (keyCode == KeyEvent.KEYCODE_DEL
                    && selection > 0
                    && event.getAction() == KeyEvent.ACTION_DOWN) {
                String str = et_scanner.getText().toString();
                char delChar = str.charAt(selection - 1);
                if (delChar == SCANNER_HAIT_TRIM) {
                    int aiTePrefixIndex = str.lastIndexOf("@", selection - 1);
                    if (aiTePrefixIndex > -1) {
                        int index = et_scanner.getSelectionStart();
                        Editable editable = et_scanner.getText();

                        // TODO: ...
                        String delName = str.substring(aiTePrefixIndex + 1, index - 1);
                        delSelectUser(delName);

                        editable.delete(aiTePrefixIndex, index);
                        return true;
                    }
                }
            }
            return false;
        }
    }

    /**
     * 删除
     *
     * @param delName
     */
    private void delSelectUser(String delName) {
        for (int i = 0; i < mHaitSelectUsers.size(); i++) {
            SelectUser selectUser = mHaitSelectUsers.get(i);
            if (selectUser.matchName(delName)) {
                mHaitSelectUsers.remove(i);
                break;
            }
        }
    }

    /**
     * 获取已选择的用户信息
     *
     * @param message
     * @return
     */
    public List<String> getSelectUser(final String message) {
        if (mHaitSelectUsers.size() == 0) {
            return null;
        }
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < mHaitSelectUsers.size(); i++) {
            SelectUser user = mHaitSelectUsers.get(i);
            if (!message.contains(user.name) || ids.contains(user.id)) {
                continue;
            }
            ids.add(user.id);
        }
        return ids;
    }

    /**
     * 清空已选择的@用户
     */
    public void clear() {
        mHaitSelectUsers.clear();
    }

    /**
     * 隐藏输入法
     */
    public void hitKeyBoard() {
        InputMethodManager imm = (InputMethodManager) et_scanner.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_scanner.getWindowToken(), 0);
    }

    /**
     * 弹出输入法
     */
    public void showKeyBoard() {
        InputMethodManager imm = (InputMethodManager) et_scanner.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.showSoftInput(et_scanner, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY);

        et_scanner.setFocusable(true);
        et_scanner.setFocusableInTouchMode(true);
        et_scanner.requestFocus();
    }

    /**
     * 将Activity返回的数据传人此方法解析
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && null != data) {
            User user = (User) data.getSerializableExtra(User.class.getName());
            if (user != null) {
                String id = user.toShortUser().getId();
                if (TextUtils.isEmpty(id) || id.equals(MainApp.user.id)) {
                    Global.Toast("不能@自己");
                           // ((BaseActivity) (mFragment != null ? mFragment.getActivity() : et_scanner.getContext())).Toast("不能@自己");
                    return;
                }
                String name = user.toShortUser().getName();
                mHaitSelectUsers.add(new SelectUser(name, id));
                String selectName = add$Name(name);
                int index = et_scanner.getSelectionStart();
                Editable editable = et_scanner.getText();
                editable.insert(index, selectName);
            }
            showKeyBoard();
        }
    }

    /**
     * 添加艾特用户
     *
     * @param user
     */
    public void addSelectUser(SelectUser user) {
        showKeyBoard();
        int selection = et_scanner.getSelectionStart();
        et_scanner.getText().insert(selection, add$Name_real(user.name));
        mHaitSelectUsers.add(user);
    }

    /**
     * 组合@的用户名 -- xxx + '\u2005'
     *
     * @param selectName
     * @return
     */
    private String add$Name(String selectName) {
        return selectName + SCANNER_HAIT_TRIM;
    }

    /**
     * 组合@的用户名 -- '@' + xxx + '\u2005'
     *
     * @param name
     * @return
     */
    private String add$Name_real(String name) {
        return "@" + name + SCANNER_HAIT_TRIM;
    }

    public static class SelectUser {
        public String id;
        public String name;

        public SelectUser(String name, String id) {
            this.name = name;
            this.id = id;
        }

        /**
         * 用于比较
         *
         * @param name
         * @return
         */
        public boolean matchName(String name) {

            if (TextUtils.isEmpty(name)) {
                return false;
            }
            return name.equals(this.name);
        }
    }
}
