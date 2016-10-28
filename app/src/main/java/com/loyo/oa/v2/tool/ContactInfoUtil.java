package com.loyo.oa.v2.tool;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import com.loyo.oa.v2.activityui.customer.model.MyContactInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * 【获取手机通讯录】联系人信息
 * Created by yyy on 16/10/27.
 */

public class ContactInfoUtil {

    private Context mContext;
    private CharacterParser characterParser;

    public ContactInfoUtil(Context mContext) {
        this.mContext = mContext;
        characterParser = new CharacterParser();
    }

    public List<MyContactInfo> getMyCallContactInfo() {
        MyContactInfo contactInfo;
        List<MyContactInfo> contactInfoList = new ArrayList<>();
        ContentResolver cr = mContext.getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        while (cursor.moveToNext()) {
            //获取联系人名字
            int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
            String contact = cursor.getString(nameFieldColumnIndex);
            //取得电话号码
            String ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId, null, null);

            while (phone.moveToNext()) {//获取联系人所有电话信息  手机、单位电话 ..
                String PhoneNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                contactInfo = new MyContactInfo();
                contactInfo.setName(contact);
                contactInfo.setPhono(PhoneNumber);
                contactInfoList.add(contactInfo);

                String pinyin = characterParser.getSelling(contact);
                String sortString = pinyin.substring(0, 1).toUpperCase();

                if (sortString.matches("[A-Z]")) {
                    contactInfo.setSortLetters(sortString.toUpperCase());
                } else {
                    contactInfo.setSortLetters("#");
                }
            }
        }
        cursor.close();
        return contactInfoList;
    }

}
