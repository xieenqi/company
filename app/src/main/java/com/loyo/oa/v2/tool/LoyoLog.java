package com.loyo.oa.v2.tool;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * com.loyo.oa.v2.tool
 * 描述 :日志工具
 * 作者 : ykb
 * 时间 : 15/8/14.
 */
public class LoyoLog
{
	private static final long ONE_DAY_MILLS=24*60*60*1000;
	private static final String FILE_FORMAT_SIMPLE="yyyy-MM-dd";
	private static final String FILE_FORMAT_DETAIL="yyyy-MM-dd HH:mm:ss";
	private static Boolean MYLOG_SWITCH=Config_project.is_developer_mode; // 日志文件总开关
	private static Boolean MYLOG_WRITE_TO_FILE=true;// 日志写入文件开关
	private static char MYLOG_TYPE='v';// 输入日志类型，w代表只输出告警信息等，v代表输出所有信息
	private static String MYLOG_PATH_SDCARD_DIR=Environment.getExternalStorageDirectory()+"/360LoyoLog/";// 日志文件在sdcard中的路径
	private static int SDCARD_LOG_FILE_SAVE_DAYS = 2;// sd卡中日志文件的最多保存天数
	private static String MYLOGFILEName = "loyolog.txt";// 本类输出的日志文件名称
	private static SimpleDateFormat myLogSdf = new SimpleDateFormat(FILE_FORMAT_DETAIL);// 日志的输出格式
	private static SimpleDateFormat logfile = new SimpleDateFormat(FILE_FORMAT_SIMPLE);// 日志文件格式
	private static Handler mHandler=null;

    public static void setLogHandler(Handler handler)
    {
    	mHandler=handler;
    }
	public static void w(String tag, Object msg) { // 警告信息
		log(tag, msg.toString(), 'w');
	}

	
	public static void e(String tag, Object msg) { // 错误信息
		log(tag, msg.toString(), 'e');
	}

	public static void d(String tag, Object msg) {// 调试信息
		log(tag, msg.toString(), 'd');
	}

	public static void i(String tag, Object msg) {//
		log(tag, msg.toString(), 'i');
	}

	public static void v(String tag, Object msg) {
		log(tag, msg.toString(), 'v');
	}

	public static void w(String tag, String text) {
		log(tag, text, 'w');
	}

	public static void e(String tag, String text) {
		log(tag, text, 'e');
	}

	public static void d(String tag, String text) {
		log(tag, text, 'd');
	}

	public static void i(String tag, String text) {
		log(tag, text, 'i');
	}

	public static void v(String tag, String text) {
		log(tag, text, 'v');
	}

	/**
	 * 根据tag, msg和等级，输出日志
	 * 
	 * @param tag
	 * @param msg
	 * @param level
	 * @return void
	 * @since v 1.0
	 */
	private static void log(String tag, String msg, char level) {
		
		if (MYLOG_SWITCH) {
			msg=getFunctionName()+msg;
			if ('e' == level && ('e' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) { // 输出错误信息
				Log.e(tag, msg);
			} else if ('w' == level && ('w' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) {
				Log.w(tag, msg);
			} else if ('d' == level && ('d' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) {
				Log.d(tag, msg);
			} else if ('i' == level && ('d' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) {
				Log.i(tag, msg);
			} else {
				Log.v(tag, msg);
			}
		}
		if (MYLOG_WRITE_TO_FILE)
			writeLogtoFile(String.valueOf(level), tag, msg);
	}

	/**
	 * 打开日志文件并写入日志
	 * 
	 * @return
	 * **/
	private static void writeLogtoFile(String mylogtype, String tag, String text)
	{// 新建或打开日志文件
		Date date=new Date(System.currentTimeMillis());
		String needDelFiel = logfile.format(date);
		File dir=new File(MYLOG_PATH_SDCARD_DIR);
		try {
			if(!dir.exists())
				dir.mkdirs();
		}catch (Exception e){
			e.printStackTrace();
		}
		File logFile=new File(dir,needDelFiel+MYLOGFILEName);
		String content="TIME : "+myLogSdf.format(date) +" LOGTYPE : "+mylogtype+" TAG : "+tag+" CONTENT : "+text;
		try{
			FileWriter fileWriter=new FileWriter(logFile,true);
			fileWriter.append("\n");
			fileWriter.append(content);
			fileWriter.append("\n");
			fileWriter.flush();
			fileWriter.close();
		}catch (Exception e){
			e.printStackTrace();
		}
		delFile();
	}
	/**
	 * Get The Current Function Name
	 *
	 * @return
	 */
	private static String getFunctionName() {
		StackTraceElement[] sts = Thread.currentThread().getStackTrace();
		if (sts == null) {
			return null;
		}
		for (StackTraceElement st : sts) {
			if (st.isNativeMethod()) {
				continue;
			}
			if (st.getClassName().equals(Thread.class.getName())) {
				continue;
			}
			if (st.getClassName().equals(LoyoLog.class.getName())) {
				continue;
			}
			return  "[ " + Thread.currentThread().getName() + ": "
					+ st.getFileName() + ":" + st.getLineNumber() + " "
					+ st.getMethodName() + " ]";
		}
		return null;
	}
	/**
	 * 删除指定的日志文件
	 * @param timeCurrent 截止时间
	 * */
	public static void delFile(long timeCurrent) {// 删除日志文件
//		File file=new File(MYLOG_PATH_SDCARD_DIR);
//		String fileNames[]=file!=null?file.list():null;
//		if(null!=fileNames&&fileNames.length>0){
//			for(int i=0;i<fileNames.length;i++){
//				String  fileName=fileNames[i];
//				String fileStufxx=fileName.substring(FILE_FORMAT_SIMPLE.length());
//				long overTime=DateTool.getDateToTimestamp(fileStufxx,logfile);
//				if(overTime<=timeCurrent) {
//					File child = new File(MYLOG_PATH_SDCARD_DIR, fileName);
//					if(null!=child&&child.exists())
//						child.delete();
//				}
//			}
//		}
	}

	/**
	 * 删除指定的日志文件
	 * */
	public static void delFile() {
		// 删除日志文件
		String needDelFiel = logfile.format(getDateBefore());
		File file = new File(MYLOG_PATH_SDCARD_DIR, needDelFiel + MYLOGFILEName);
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * 得到现在时间前的几天日期，用来得到需要删除的日志文件名
	 * */
	private static Date getDateBefore() {
		Date nowtime = new Date();
		Calendar now = Calendar.getInstance();
		now.setTime(nowtime);
		now.set(Calendar.DATE, now.get(Calendar.DATE)
				- SDCARD_LOG_FILE_SAVE_DAYS);
		return now.getTime();
	}

	/**
	 * 得到现在时间前的几天日期，用来得到需要删除的日志文件名
	 * @param date
	 * */
	private static Date getDateBefore(Date date) {
		Calendar now = Calendar.getInstance();
		now.setTime(date);
		now.set(Calendar.DATE, now.get(Calendar.DATE)
				- SDCARD_LOG_FILE_SAVE_DAYS);
		return now.getTime();
	}
}
