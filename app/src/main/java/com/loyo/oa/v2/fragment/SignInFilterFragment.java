package com.loyo.oa.v2.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ScrollView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.tool.BaseFragment;

/**
 * Created by Administrator on 2014/12/11 0011.
 */
public class SignInFilterFragment extends BaseFragment {
    View view;

    ListView lv_subordinates;

    public static String MSG_SIGNIN_FILTER = "com.loyo.oa.v2.signin.filter";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

       if (view == null) {
            view = inflater.inflate(R.layout.fragment_signin_manager_filter, container, false);

           /* if (Common.getSubUsers().size() > 0) {
                lv_subordinates = (ListView) view.findViewById(R.id.lv_subordinates);

                final List<Map<String, Object>> userList = new ArrayList<>();

                for (User user : Common.getSubUsers()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("name", user.getRealname());
                    item.put("userId", user.getId());
                    userList.add(item);
                }

                SimpleAdapter subAdapter = new SimpleAdapter(getActivity(), userList,
                        R.layout.item_search_subordinates_listview, new String[]{"name"}, new int[]{R.id.tv_username});

                lv_subordinates.setAdapter(subAdapter);
                lv_subordinates.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Map<String, Object> user = userList.get(position);

                        int userId = (int) user.get("userId");
                        if (userId > 0) {
                            Intent intent = new Intent();
                            intent.setAction(MSG_SIGNIN_FILTER);
                            intent.putExtra("userId", userId);
                            view.getContext().sendBroadcast(intent);
                        }
                    }
                });
            }*/
            lv_subordinates.setFocusable(false);
            //重要,这句是保证事件不透传到底层视图
            view.findViewById(R.id.layout_body).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });

            view.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    ((ScrollView) view.findViewById(R.id.sv_main)).scrollTo(0, 0);
                }
            }, 500);

        }
        return view;
    }
}
