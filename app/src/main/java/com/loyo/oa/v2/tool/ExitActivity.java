package com.loyo.oa.v2.tool;

import android.app.Activity;

import java.util.LinkedList;
import java.util.List;

//将每个启动的Activity加入到List中，当要退出时，调用finish()方法实现
public class ExitActivity  {
    private List<Activity> list = new LinkedList<Activity>();
	private static ExitActivity instance;

	private ExitActivity() {
	}

	public static ExitActivity getInstance() {
		if (instance == null) {
			instance = new ExitActivity();
		}
		return instance;
	}

    public List<Activity> getActiveAC(){
        return  list;
    }

	public void addActivity(Activity activity) {
		list.add(activity);
	}

	public void removeActivity(Activity activity){
		list.remove(activity);
	}
    public void finishAllActivity() {
        for (Activity activity : list) {
            activity.finish();
        }
        list.clear();
    }


	public void exit() {
		for (Activity activity : list) {
			activity.finish();
		}
        list.clear();
		System.exit(0);
	}
    public Activity getTopActivity(){
        if(list.size()>0){
            return list.get(list.size()-1);
        }
     return  null;
    }


}
