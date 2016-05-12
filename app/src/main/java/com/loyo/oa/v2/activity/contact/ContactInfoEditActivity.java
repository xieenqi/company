package com.loyo.oa.v2.activity.contact;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Attachment;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.beans.UserInfo;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.common.http.ServerAPI;
import com.loyo.oa.v2.point.IMobile;
import com.loyo.oa.v2.point.IUser;
import com.loyo.oa.v2.service.InitDataService_;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RegexUtil;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.customview.RoundImageView;
import com.loyo.oa.v2.tool.customview.multi_image_selector.MultiImageSelectorActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * com.loyo.oa.v2.activity
 * 描述 :个人信息编辑页[【编辑个人资料】
 * 作者 : ykb
 * 时间 : 15/8/25.
 */
@EActivity(R.layout.activity_contactinfo_edit)
public class ContactInfoEditActivity extends BaseActivity {

    private final int REQUEST_IMAGE = 100;

    @ViewById
    ViewGroup layout_back;
    @ViewById
    ImageView iv_submit;
    @ViewById
    TextView tv_title;
    @ViewById
    ViewGroup layout_set_avartar;
    @ViewById
    RoundImageView img_title_user;
    @ViewById
    ViewGroup layout_birthday;
    @ViewById
    ViewGroup layout_mobile;
    @ViewById
    TextView tv_mobile;
    @ViewById
    TextView et_qq;
    @ViewById
    EditText et_weixin;
    @ViewById
    RadioButton sex_famale;
    @ViewById
    RadioButton sex_male;
    @ViewById
    TextView tv_birthday;
    @ViewById
    TextView tv_age;
    @ViewById
    TextView tv_departments;
    @ViewById
    TextView tv_positions;
    @Extra
    User user;

    private int mobile_phone = 1;
    private boolean isRun = true;
    private int sex;
    private String uuid = null;
    private String path = null;

    private MHandler mHandler = new MHandler(this);
    private Timer mTimer;
    private TimerTask mTimerTask;
    private TextView tv_get_code;
    private EditText et_code;
    private EditText et_mobile;
    private TextView tv_mobile_error;

    private String birthStr;
    private int age;

    private class MHandler extends Handler {
        private WeakReference<ContactInfoEditActivity> mActivity;

        private MHandler(final ContactInfoEditActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);

            String des = "重新获取(" + msg.what + "秒)";
            TextView tvtime = mActivity.get().tv_get_code;

            if (msg.what == 0) {
                des = "重新获取";
                mActivity.get().recycle();
                if (null != tvtime) {
                    tvtime.setTextColor(ContactInfoEditActivity.this.getResources().getColor(R.color.gray));
                    tvtime.setEnabled(true);
                }
            }

            if (null != tvtime) {
                tvtime.setTextColor(ContactInfoEditActivity.this.getResources().getColor(R.color.title_bg1));
                tvtime.setText(des);
            }

            if (msg.what == 0x01) {
                Utils.setContent(tv_birthday, birthStr);
                Utils.setContent(tv_age, age + "");
            }
        }
    }

    @AfterViews
    void initViews() {
        setTouchView(-1);
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText("编辑个人资料");
        iv_submit.setVisibility(View.VISIBLE);
        layout_back.setOnTouchListener(Global.GetTouch());
        iv_submit.setOnTouchListener(Global.GetTouch());
        layout_birthday.setOnTouchListener(Global.GetTouch());
        layout_set_avartar.setOnTouchListener(Global.GetTouch());
        layout_mobile.setOnTouchListener(Global.GetTouch());
        //et_weixin.addTextChangedListener(new WxTextWatcher(et_weixin, "微信号格式不正确"));
        initData();
    }

    @Click({R.id.layout_back, R.id.layout_set_avartar, R.id.layout_birthday, R.id.iv_submit, R.id.layout_mobile, R.id.iv_submit})
    void onClick(final View v) {
        switch (v.getId()) {
            case R.id.layout_back:
                if (!isDataChange()) {
                    showLeaveDialog();
                } else {
                    app.finishActivity(ContactInfoEditActivity.this, MainApp.ENTER_TYPE_TOP, RESULT_CANCELED, null);
                }
                break;
            /*设置头像*/
            case R.id.layout_set_avartar:
                LogUtil.dee("点击设置头像");
                Intent intent = new Intent(this, MultiImageSelectorActivity.class);
                // 是否显示拍摄图片
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
                // 最大可选择图片数量
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);
                // 选择模式
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
                intent.putExtra(MultiImageSelectorActivity.EXTRA_CROP_CIRCLE, true);
                // 默认选择
                //                if (mSelectPath != null && mSelectPath.size() > 0) {
                //                    intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mSelectPath);
                //                }
                startActivityForResult(intent, REQUEST_IMAGE);
                break;
            case R.id.layout_birthday:
                pickDate();
                break;
            case R.id.iv_submit:
                //关闭键盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(et_weixin.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                updateProfile();
                break;
            case R.id.layout_mobile:
                showUpdateMobileDialog();
                break;
            default:
                break;
        }
    }

    @CheckedChange({R.id.sex_famale, R.id.sex_male})
    void onCheckChanged(final CompoundButton compoundButton, final boolean checked) {
        switch (compoundButton.getId()) {
            case R.id.sex_famale:
                if (checked) {
                    sex = 1;
                }
                break;
            case R.id.sex_male:
                if (checked) {
                    sex = 2;
                }
                break;
            default:

                break;
        }
    }

    /**
     * 判断数据是否被编辑过
     *
     * @return
     */
    private boolean isDataChange() {

        String tel = TextUtils.isEmpty(tv_mobile.getText().toString()) ? null : tv_mobile.getText().toString();
        String birthDay = TextUtils.isEmpty(tv_birthday.getText().toString()) ? null : tv_birthday.getText().toString();
        String weixinId = TextUtils.isEmpty(et_weixin.getText().toString()) ? null : et_weixin.getText().toString();

        if (tel != null) {
            if (!tel.equals(user.mobile)) {
                return false;
            }
        }

        if (birthDay != null) {
            if (!birthDay.equals(user.birthDay)) {
                return false;
            }
        }

        if (weixinId != null) {
            if (!weixinId.equals(user.weixinId)) {
                return false;
            }
        }
        if (sex + "" != null) {
            if (sex != user.gender) {
                return false;
            }
        }

        return true;
    }


    /**
     * 显示对话框
     */
    private void showLeaveDialog() {

        showGeneralDialog(false, true, getString(R.string.app_userinfoedt_message));
        //确认
        generalPopView.setSureOnclick(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                generalPopView.dismiss();
                updateProfile();
            }
        });
        //取消
        generalPopView.setCancelOnclick(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                generalPopView.dismiss();
                app.finishActivity(ContactInfoEditActivity.this, MainApp.ENTER_TYPE_TOP, RESULT_CANCELED, null);
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        if (null == user) {
            return;
        }

/*        if (!TextUtils.isEmpty(user.avatar)) {
            ImageLoader.getInstance().displayImage(user.getAvatar(), img_title_user);
        }*/

        int defaultAvatao;

        if (null == MainApp.user.avatar || MainApp.user.avatar.isEmpty() || !MainApp.user.avatar.contains("http")) {
            if (MainApp.user.gender == 2) {
                defaultAvatao = R.drawable.icon_contact_avatar;
            } else {
                defaultAvatao = R.drawable.img_default_user;
            }
            img_title_user.setImageResource(defaultAvatao);
        } else {
            ImageLoader.getInstance().displayImage(user.getAvatar(), img_title_user);
        }


        path = user.getAvatar();
        Utils.setContent(tv_mobile, user.mobile);
        Utils.setContent(et_weixin, user.weixinId);
        if (user.gender == 2) {
            sex_male.setChecked(true);
        } else if (user.gender == 1) {
            sex_famale.setChecked(true);
        }

        if (!TextUtils.isEmpty(user.birthDay)) {
            int age = Utils.getAge(user.birthDay.substring(0, 4));
            if (age >= 150) {
                return;
            }
            Utils.setContent(tv_birthday, user.birthDay);
            Utils.setContent(tv_age, Utils.getAge(user.birthDay.substring(0, 4)) + "");
        }

        /*获取职位与部门名字*/
        if (null != user.depts && !user.depts.isEmpty()) {
            StringBuilder departments = new StringBuilder();
            StringBuilder posiName = new StringBuilder();
            for (int i = 0; i < user.depts.size(); i++) {
                UserInfo info = user.depts.get(i);
                if (null != info.getShortDept() && !TextUtils.isEmpty(info.getShortDept().getName())) {
                    if (!TextUtils.isEmpty(departments)) {
                        departments.append("|");
                    }
                    departments.append(info.getShortDept().getName());
                }
                if (null != info.getTitle() && !TextUtils.isEmpty(info.getTitle())) {
                    if (!TextUtils.isEmpty(posiName)) {
                        posiName.append("|");
                    }
                    posiName.append(info.getTitle());
                }
            }
            tv_departments.setText(departments);
            tv_positions.setText(posiName);
        }
    }


    /**
     * 编辑个人信息
     */
    private void updateProfile() {

        if (!et_weixin.getText().toString().isEmpty()) {
            if (!RegexUtil.regexk(et_weixin.getText().toString(), RegexUtil.StringType.WX)) {
                Toast("微信号码不正确");
                return;
            }
        }


        showLoading("");
        String tel = tv_mobile.getText().toString();
        String birthDay = tv_birthday.getText().toString();
        String weixinId = et_weixin.getText().toString();
        HashMap<String, Object> map = new HashMap<>();

        map.put("mobile", tel);
        map.put("gender", sex);
        map.put("birthDay", birthDay);
        map.put("weixinId", weixinId);
        map.put("avatar", path);

        RestAdapterFactory.getInstance().build(Config_project.SERVER_URL_LOGIN()).create(IUser.class).updateProfile(user.getId(), map, new RCallback<User>() {
            @Override
            public void success(final User user, final Response response) {
                HttpErrorCheck.checkResponse("修改个人信息", response);
                Toast("修改个人信息成功");
                Intent mIntent = new Intent();
                InitDataService_.intent(ContactInfoEditActivity.this).start(); //更新组织架构
                mIntent.putExtra(ExtraAndResult.STR_SUPER_ID, ExtraAndResult.TYPE_SHOW_DEPT_USER);
                app.finishActivity(ContactInfoEditActivity.this, MainApp.ENTER_TYPE_ZOOM_IN, RESULT_OK, mIntent);
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
            }
        });
    }

    /**
     * 开始倒计时
     */
    private void countDown() {
        mTimerTask = new TimerTask() {
            private int seconds = 60;

            @Override
            public void run() {
                if (!isRun) {
                    return;
                }
                seconds--;
                mHandler.sendEmptyMessage(seconds);
            }
        };
        isRun = true;
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(mTimerTask, 0, 1000);
    }

    /**
     * 回收定时器
     */
    private void recycle() {
        isRun = false;
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
    }

    /**
     * 显示修改手机号的对话框
     */
    private void showUpdateMobileDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_update_mobile, null, false);
        dialogView.getBackground().setAlpha(150);
        final PopupWindow dialog = new PopupWindow(dialogView, -1, -1, true);
        dialog.setAnimationStyle(R.style.PopupAnimation);
        dialog.setBackgroundDrawable(new BitmapDrawable(getResources()));// 响应键盘三个主键的必须步骤
        dialog.showAtLocation(findViewById(R.id.tv_title), Gravity.BOTTOM, 0, 0);

        et_mobile = (EditText) dialogView.findViewById(R.id.et_mobile);
        et_code = (EditText) dialogView.findViewById(R.id.et_code);

        TextView confirm = (TextView) dialogView.findViewById(R.id.btn_confirm);
        TextView cancel = (TextView) dialogView.findViewById(R.id.btn_cancel);
        tv_get_code = (TextView) dialogView.findViewById(R.id.btn_get_code);
        tv_mobile_error = (TextView) dialogView.findViewById(R.id.tv_mobile_error);

        confirm.setOnTouchListener(Global.GetTouch());
        cancel.setOnTouchListener(Global.GetTouch());
        tv_get_code.setOnTouchListener(Global.GetTouch());

        MOnclickListener listener = new MOnclickListener(dialog);
        confirm.setOnClickListener(listener);
        tv_get_code.setOnClickListener(listener);
        cancel.setOnClickListener(listener);

        et_mobile.addTextChangedListener(new MobileTextWatcher(tv_get_code));
    }

    /**
     * 获取验证码
     */
    private void getVerifyCode() {
        recycle();
        String mobile = et_mobile.getText().toString();
        if (TextUtils.isEmpty(mobile)) {
            Toast("请填写手机号");
            return;
        }
        verifyPhone(mobile);
    }

    /**
     * 验证手机号
     *
     * @param tel
     */
    private void verifyPhone(final String tel) {
        //验证手机号
        RestAdapterFactory.getInstance().build(FinalVariables.URL_VERIFY_PHONE).create(IMobile.class).verifyPhone(tel, new RCallback<Object>() {
            @Override
            public void success(final Object o, final Response response) {
                HttpErrorCheck.checkResponse("验证手机号", response);
                tv_get_code.setEnabled(false);
                countDown();
                //请求验证码
                RestAdapterFactory.getInstance().build(FinalVariables.URL_GET_CODE).create(IMobile.class).getVerifyCode(tel, new RCallback<Object>() {
                    @Override
                    public void success(final Object o, final Response response) {
                        HttpErrorCheck.checkResponse("请求手机验证码", response);
                        Toast("发送验证码成功");
                    }

                    @Override
                    public void failure(final RetrofitError error) {
                        super.failure(error);
                        HttpErrorCheck.checkError(error);
                        Toast("发送验证码失败");
                    }
                });
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
                if ("500".equals(error.getMessage().substring(0, 3))) {
                    Toast("该手机号已被录入本系统,请勿重复使用!");
                }
            }
        });
    }

    /**
     * 修改手机号
     *
     * @param dialog
     */
    private void modifyMobile(final PopupWindow dialog) {

        final String mobile = et_mobile.getText().toString();
        String code = et_code.getText().toString();
        if (TextUtils.isEmpty(mobile)) {
            Toast("请填写手机号");
            return;
        }
        if (TextUtils.isEmpty(code)) {
            Toast("请填写验证码");
            return;
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("tel", mobile);
        map.put("code", code);
        app.getRestAdapter(mobile_phone).create(IMobile.class).modifyMobile(map, new RCallback<Object>() {
            @Override
            public void success(final Object o, final Response response) {
                HttpErrorCheck.checkResponse(response);
                dialog.dismiss();
                Toast("修改手机号码成功");
                tv_mobile.setText(mobile);
                response.getUrl();
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
                dialog.dismiss();
                Toast("修改手机号码失败");
            }
        });
    }

    /**
     * 功 能: 生日选择器
     * 说 明: 控件自带按钮错显为英文，找不到原因，只能手动设置按钮监听。
     */

    public void pickDate() {
        Calendar cal = Calendar.getInstance();
        final DatePickerDialog mDialog = new DatePickerDialog(this, null,
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

        //手动设置按钮
        mDialog.setButton(DialogInterface.BUTTON_POSITIVE, "完成", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatePicker datePicker = mDialog.getDatePicker();
                int year = datePicker.getYear();
                int month = datePicker.getMonth();
                int day = datePicker.getDayOfMonth();

                age = Utils.getAge(year + "");
                if (age > 0) {
                    birthStr = year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", day);
                    mHandler.sendEmptyMessage(0x01);
                } else {
                    Toast("出生日期不能是未来时间，请重新设置");
                }
            }
        });

        //取消按钮，如果不需要直接不设置即可
        mDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("BUTTON_NEGATIVE~~");
            }
        });

        mDialog.show();
    }


    /**
     * 电话号码编辑框的文本观察器
     */
    private final class MobileTextWatcher extends ITextWatcher {
        private TextView mButton;

        private MobileTextWatcher(final TextView button) {
            mButton = button;
        }

        @Override
        public void afterTextChanged(final Editable editable) {
            if (TextUtils.isEmpty(editable.toString()) || editable.length() < 11) {
                tv_mobile_error.setText("");
                mButton.setEnabled(false);
            } else if (editable.length() == 11) {
                boolean isMobile = RegexUtil.regexk(et_mobile.getText().toString(), RegexUtil.StringType.MOBILEL);
                if (!isMobile) {
                    tv_mobile_error.setText("手机号码格式不正确");
                } else {
                    mButton.setEnabled(true);
                }
            }
        }
    }

    /**
     * 微信编辑框的文本观察器
     */
/*    private class WxTextWatcher extends ITextWatcher {
        private String mDes;
        private EditText mEt;

        private WxTextWatcher(EditText tv, String des) {
            mEt = tv;
            mDes = des;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            boolean isMobile = RegexUtil.regexk(editable.toString(), RegexUtil.StringType.WX);
            if (!isMobile) {
                mEt.setError(mDes);
            }
        }
    }*/

    /**
     * 弹出框控件点击事件
     */
    private class MOnclickListener implements View.OnClickListener {
        private PopupWindow mDialog;

        private MOnclickListener(final PopupWindow dialog) {
            mDialog = dialog;
        }

        @Override
        public void onClick(final View view) {
            switch (view.getId()) {
                //获取验证码
                case R.id.btn_get_code:
                    getVerifyCode();
                    break;
                //确认修改手机号
                case R.id.btn_confirm:
                    modifyMobile(mDialog);
                    recycle();
                    break;
                //取消
                case R.id.btn_cancel:
                    mDialog.dismiss();
                    recycle();
                    break;
                default:

                    break;
            }
        }
    }

    private abstract class ITextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {

        }

        @Override
        public void onTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {

        }
    }

    @Override
    public void onBackPressed() {
        if (!isDataChange()) {
            showLeaveDialog();
        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {

            List<String> mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            LogUtil.dee("mSelectPath:" + (mSelectPath == null));
            StringBuilder sb = new StringBuilder();
            for (String p : mSelectPath) {
                sb.append(p);
            }
            LogUtil.dee("sb.toString:" + sb.toString());

            ImageLoader.getInstance().displayImage("file://" + sb.toString(), img_title_user);
            User.setImageUrl("file://" + sb.toString());
            try {
                Uri uri = Uri.parse("file://" + sb.toString());
                File newFile = Global.scal(this, uri);

                if (newFile != null && newFile.length() > 0) {
                    RequestParams params = new RequestParams();
                    if (uuid == null) {
                        uuid = StringUtil.getUUID();
                    }
                    params.put("uuid", uuid);

                    if (newFile.exists()) {
                        params.put("attachments", newFile, "image/*");
                    }

                    ArrayList<ServerAPI.ParamInfo> lstParamInfo = new ArrayList<ServerAPI.ParamInfo>();
                    ServerAPI.ParamInfo paramInfo = new ServerAPI.ParamInfo("bitmap", newFile);
                    lstParamInfo.add(paramInfo);
                    ServerAPI.request(this, ServerAPI.POST, FinalVariables.attachments, null, params, AsyncHandler_Upload_New_Attachments.class, lstParamInfo);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public class AsyncHandler_Upload_New_Attachments extends BaseActivityAsyncHttpResponseHandler {
        File file;

        public void setBitmap(final File imageFile) {
            file = imageFile;
        }

        @Override
        public void onSuccess(final int arg0, final Header[] arg1, final byte[] arg2) {
            try {
                Attachment attachment = MainApp.gson.fromJson(getStr(arg2), Attachment.class);
                path = attachment.getUrl();

            } catch (Exception e) {
                Global.ProcException(e);
            }
        }
    }
}
