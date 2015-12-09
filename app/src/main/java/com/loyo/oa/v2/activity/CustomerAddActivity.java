package com.loyo.oa.v2.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.customer.CustomerRepeat;
import com.loyo.oa.v2.adapter.SignInGridViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Attachment;
import com.loyo.oa.v2.beans.BaseBeans;
import com.loyo.oa.v2.beans.City;
import com.loyo.oa.v2.beans.Contact;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.NewTag;
import com.loyo.oa.v2.beans.TagItem;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.ServerAPI;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.point.IAttachment;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LocationUtil;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.SelectPicPopupWindow;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FocusChange;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

@EActivity(R.layout.activity_customer_add)
public class CustomerAddActivity extends BaseActivity implements View.OnClickListener {

    public static final int REQUEST_CUSTOMER_LABEL = 5;
    public static final int REQUEST_CUSTOMER_NEW_CONTRACT = 6;

    @ViewById
    ViewGroup img_title_left;
    @ViewById
    ViewGroup img_title_right;

    @ViewById
    EditText edt_name;
    @ViewById
    EditText edt_contract;
    @ViewById
    EditText edt_contract_tel;
    @ViewById
    EditText et_address;

    @ViewById
    TextView tv_labels;

    @ViewById
    LinearLayout layout_newContract;
    @ViewById
    LinearLayout layout_address;

    @ViewById
    Button btn_add_new_contract;

    @ViewById
    GridView gridView_photo;

    SignInGridViewAdapter signInGridViewAdapter;
    String uuid = null;
    ArrayList<Attachment> lstData_Attachment = new ArrayList<>();

    ArrayList<Contact> mContacts = new ArrayList<>();
    ArrayList<TagItem> items = new ArrayList<>();
    ArrayList<NewTag> tags;
    String tagItemIds;
    String mGpsAddress;

    boolean isFocused = false;

    @AfterViews
    void initUI() {
        img_title_left.setOnTouchListener(Global.GetTouch());
        img_title_right.setOnTouchListener(Global.GetTouch());

        super.setTitle("新建客户");
        init_gridView_photo();
        getTempCustomer();

        new LocationUtil(this, new LocationUtil.AfterLocation() {
            @Override
            public void OnLocationSucessed(String address, double longitude, double latitude, float radius) {
                if (!isFocused) {
                    et_address.setText(address);
                    mGpsAddress = address;
                }
            }

            @Override
            public void OnLocationFailed() {
                Toast.makeText(CustomerAddActivity.this, "定位失败,请在网络和GPS信号良好时重试", Toast.LENGTH_LONG).show();
            }
        });
    }

    void getTempCustomer() {
        Customer customer = DBManager.Instance().getCustomer();
        if (customer == null) return;

        edt_name.setText(customer.getName());
        ArrayList<Contact> contacts = customer.getContacts();
        if (contacts != null && contacts.size() > 0) {
            for (Contact c : contacts) {
                if (c.isDefault()) {
                    edt_contract.setText(c.getName());
                    edt_contract_tel.setText(c.getTel());
                } else {
                    setContract(c);
                }
            }
        }
    }

    @FocusChange(R.id.et_address)
    void addressFocused(View hello, boolean hasFocus) {
        if (hasFocus) {
            isFocused = true;
        }
    }

    void init_gridView_photo() {
        signInGridViewAdapter = new SignInGridViewAdapter(this, lstData_Attachment, true, true);
        SignInGridViewAdapter.setAdapter(gridView_photo, signInGridViewAdapter);
    }

    @Click({R.id.img_title_left, R.id.img_title_right,R.id.tv_search,
            R.id.layout_customer_label})
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_search:
                serachRepate();
                Intent intent = new Intent(CustomerAddActivity.this, CustomerRepeat.class);
                startActivity(intent);
                break;


            case R.id.img_title_left:
                app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_CANCELED, null);
                break;

            case R.id.img_title_right:
                String customer_name = edt_name.getText().toString().trim();
                String customerAddress = et_address.getText().toString().trim();
                String customerContract = edt_contract.getText().toString().trim();
                String customerContractTel = edt_contract_tel.getText().toString().trim();

                if (customer_name.isEmpty()) {
                    Toast("请输入客户名称");
                    break;
                } else if (customerAddress.isEmpty()) {
                    Toast("请输入的客户地址");
                    break;
                } else if (customerContract.isEmpty()) {
                    Toast("请输入联系人");
                    break;
                } else if (customerContractTel.isEmpty()) {
                    Toast("请输入联系电话");
                    break;
                }
                if (!StringUtil.isEmpty(customerContract) || !StringUtil.isEmpty(customerContractTel)) {
                    Contact defaultContact;
                    defaultContact = new Contact();
                    defaultContact.setName(customerContract);
                    defaultContact.setTel(customerContractTel);
                    defaultContact.setIsDefault(true);

                    if (mContacts.size() > 0 && mContacts.get(0).isDefault()) {
                        mContacts.set(0, defaultContact);
                    } else {
                        mContacts.add(0, defaultContact);
                    }
                }

                JSONObject jsonObject = new JSONObject();
                StringEntity stringEntity = null;
                try {
                    jsonObject.put("name", customer_name);
                    // jsonObject.put("address", customerAddress);

                    if (uuid != null && lstData_Attachment.size() > 0) {
                        jsonObject.put("uuid", uuid);
                    }

                    JSONObject jsonLoc = new JSONObject();
                    jsonLoc.put("addr", customerAddress);
                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(app.longitude);
                    jsonArray.put(app.latitude);
                    jsonLoc.put("loc", jsonArray);
                    jsonObject.put("loc", jsonLoc);
                    jsonObject.put("pname", customerContract);
                    jsonObject.put("ptel", customerContractTel);
                   /* JSONArray jsonArrayContacts = new JSONArray();
                    for (Contact contact : mContacts) {

                        JSONObject jo = new JSONObject();
                        jo.put("id",contact.getId());
                        jo.put("name", contact.getName());
                        jo.put("tel", contact.getTel());
                        jo.put("isDefault", contact.isDefault());

                        jsonArrayContacts.put(jo);
                    }


                  if (!ListUtil.IsEmpty(mContacts)) {
                        jsonObject.put("contacts", jsonArrayContacts);
                    }*/

                   /* if (customerAddress.equals(mGpsAddress)
                            && !StringUtil.isEmpty(customerAddress)
                            && !StringUtil.isEmpty(mGpsAddress)) {

                        jsonObject.put("gpsAddress", mGpsAddress);
                        jsonObject.put("gpsInfo", app.longitude + "," + app.latitude);
                    }
*/
                    if (tags != null && tags.size() > 0) {
                        JSONArray jsonArrayTagItems = new JSONArray();
                        for (NewTag tag : tags) {

                            JSONObject jo = new JSONObject();
                            jo.put("tId", tag.gettId());
                            jo.put("itemId", tag.getItemId());
                            jo.put("itemName", tag.getItemName());
                            jsonArrayTagItems.put(jo);
                        }

                        jsonObject.put("tags", jsonArrayTagItems);
                    }

                    stringEntity = new StringEntity(jsonObject.toString(), "UTF-8");

                } catch (Exception e) {
                    Global.ProcException(e);
                }

                ServerAPI.request(CustomerAddActivity.this, ServerAPI.POST, FinalVariables.customers, stringEntity, ServerAPI.CONTENT_TYPE_JSON, AsyncAddCustomer.class);

                break;
            case R.id.layout_customer_label:
                Bundle bundle2 = new Bundle();
                if (tags != null) {
                    bundle2.putSerializable("tags", tags);
                }
                app.startActivityForResult((Activity) mContext, CustomerLabelActivity_.class, MainApp.ENTER_TYPE_RIGHT, REQUEST_CUSTOMER_LABEL, bundle2);

                break;
            /*case R.id.btn_add_new_contract:
                app.startActivityForResult((Activity) mContext, CustomerContractAddActivity.class, MainApp.ENTER_TYPE_RIGHT, REQUEST_CUSTOMER_NEW_CONTRACT, null);
                break;*/
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUEST_CUSTOMER_LABEL:
                Bundle bundle = data.getExtras();
                tags = (ArrayList<NewTag>) bundle.getSerializable("data");
                StringBuffer sb = null;
                StringBuffer sbItemId = null;
                StringBuffer sbTId = null;
                for (NewTag tag : tags) {
                    if (sb == null) {
                        sb = new StringBuffer();
                        sb.append(String.valueOf(tag.getItemName()));

                        sbItemId = new StringBuffer();
                        sbItemId.append(String.valueOf(tag.getItemId()));

                        sbTId = new StringBuffer();
                        sbTId.append(String.valueOf(tag.gettId()));

                    } else {
                        sb.append("/");
                        sb.append(String.valueOf(tag.getItemName()));

                        sbItemId.append(",");
                        sbItemId.append(String.valueOf(tag.getItemId()));

                        sbTId.append(",");
                        sbTId.append(String.valueOf(tag.gettId()));
                    }
                }

                if (sbItemId != null) {
                    tagItemIds = sbItemId.toString();
                    tv_labels.setText(sb.toString());
                } else
                    tv_labels.setText("");
                break;
            case REQUEST_CUSTOMER_NEW_CONTRACT:

                Contact contact = (Contact) data.getSerializableExtra("data");
                setContract(contact);

                break;
            case SelectPicPopupWindow.GET_IMG:
                try {
                    ArrayList<SelectPicPopupWindow.ImageInfo> pickPhots = (ArrayList<SelectPicPopupWindow.ImageInfo>) data.getSerializableExtra("data");
                    for (SelectPicPopupWindow.ImageInfo item : pickPhots) {
                        Uri uri = Uri.parse(item.path);
                        File newFile = Global.scal(this, uri);

                        if (newFile != null && newFile.length() > 0) {
                            RequestParams params = new RequestParams();
                            if (uuid == null) {
                                uuid = StringUtil.getUUID();
                            }
                            params.put("uuid", uuid);

                            if (newFile.exists()) {
                                params.put("attachments", newFile, "image/jpeg");
                            }

                            ArrayList<ServerAPI.ParamInfo> lstParamInfo = new ArrayList<ServerAPI.ParamInfo>();
                            ServerAPI.ParamInfo paramInfo = new ServerAPI.ParamInfo("bitmap", newFile);
                            lstParamInfo.add(paramInfo);
                            ServerAPI.request(this, ServerAPI.POST, FinalVariables.attachments, null, params, AsyncHandler_Upload_New_Attachments.class, lstParamInfo);
                        }
                    }
                } catch (Exception ex) {
                    Global.ProcException(ex);
                }
                break;
            case FinalVariables.REQUEST_DEAL_ATTACHMENT:
                try {
                    final Attachment delAttachment = (Attachment) data.getSerializableExtra("delAtm");
                    MainApp.getMainApp().getRestAdapter().create(IAttachment.class).remove(String.valueOf(delAttachment.getId()), new RCallback<Attachment>() {
                        @Override
                        public void success(Attachment attachment, Response response) {
                            Toast("删除附件成功!");
                            lstData_Attachment.remove(delAttachment);
                            signInGridViewAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Toast("删除附件失败!");
                            super.failure(error);
                        }
                    });
                } catch (Exception e) {
                    Global.ProcException(e);
                }
                break;

        }
    }

    /**
     * 查重功能
     * */
    void serachRepate(){

        HashMap<String,Object> map = new HashMap<String,Object>();
        map.put("pageIndex",1);
        map.put("pageSize",20);
        map.put("keyWords","0000");

        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).getSerachRepeat(map,new RCallback<City>(){
            @Override
            public void success(City test, Response response) {

                try {
                    LogUtil.dll("success result:"+ Utils.convertStreamToString(response.getBody().in()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                try {
                    LogUtil.dll("fail result:"+ Utils.convertStreamToString(error.getResponse().getBody().in()));
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
    }

    void setContract(Contact c) {
        if (c == null) return;

        mContacts.add(c);

        layout_newContract.setVisibility(View.VISIBLE);

        View view = LayoutInflater.from(this).inflate(R.layout.item_customer_new_contract, null, false);
        TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
        TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);

        tv_name.setText(c.getName());
        tv_phone.setText(c.getTel());

        layout_newContract.addView(view);
    }

    public class AsyncAddCustomer extends BaseActivityAsyncHttpResponseHandler {

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            try {
                Customer retCustomer = MainApp.gson.fromJson(getStr(arg2), Customer.class);
                Toast(getString(R.string.app_add) + getString(R.string.app_succeed));

                isSave = false;
                //fixes bug293 ykb 07-14
//                if(items!=null&&!items.isEmpty())
//                    retCustomer.setType(items);
                Intent intent = new Intent();
                intent.putExtra(Customer.class.getName(), retCustomer);

                app.finishActivity((Activity) mContext, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);

            } catch (Exception e) {
                Global.ProcException(e);
            }
        }
    }

    public class AsyncHandler_Upload_New_Attachments extends BaseActivityAsyncHttpResponseHandler {
        File file;

        public void setBitmap(File imageFile) {
            file = imageFile;
        }

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            try {
                Attachment attachment = MainApp.gson.fromJson(getStr(arg2), Attachment.class);
                attachment.saveFile(file);
                lstData_Attachment.add(attachment);

                init_gridView_photo();
            } catch (Exception e) {
                Global.ProcException(e);
            }
        }
    }

    boolean isSave = true;
    Customer mCustomer;

    @Override
    protected void onDestroy() {
        super.onDestroy();

        DBManager.Instance().deleteCustomer();
        if (isSave) {
            mCustomer = new Customer();
            mCustomer.setName(edt_name.getText().toString().trim());

            ArrayList<Contact> contacts = new ArrayList<>();

            if (mContacts != null && mContacts.size() > 0) {
                contacts.addAll(mContacts);
            }

            Contact defaultContact = new Contact();
            defaultContact.setName(edt_contract.getText().toString());
            defaultContact.setTel(edt_contract_tel.getText().toString());
            defaultContact.setIsDefault(true);
            contacts.add(0, defaultContact);
            mCustomer.setContacts(contacts);

            mCustomer.setOwner(null);
            mCustomer.setMembers(null);
            mCustomer.setTags(null);
            mCustomer.setCreator(null);

            DBManager.Instance().putCustomer(MainApp.gson.toJson(mCustomer));
        }
    }
}
