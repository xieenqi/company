package com.loyo.oa.v2.tool;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.util.EncodingUtils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

/**
 * 对文件 的操作
 */
public class FileTool {

	public static File mkdirFile(String filePath) {

		File file = new File(filePath);
		if (!file.exists())
			file.mkdirs();

		return file;
	}

	/*
	 * 判断SD卡是否存在
	 */
	public static boolean isSDExist() {
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

			return true;
		}

		return false;
	}

	public static boolean isExist(String path) {
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			File f = new File(path);
			if (f.exists()) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 获取SD卡剩余空间，单位 MB
	 */
	public static long getSDAvailaleSpace() {

		File path = Environment.getExternalStorageDirectory(); // 取得sdcard文件路径
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return (availableBlocks * blockSize) / 1024 / 1024;
		// (availableBlocks * blockSize)/1024 KIB 单位
		// (availableBlocks * blockSize)/1024 /1024 MIB单位

	}

	// 递归 // 取得文件夹大小
	public static long getFileSize(File f) {

		long size = 0;
		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getFileSize(flist[i]);
			} else {
				size = size + flist[i].length();
			}
		}
		return size;
	}

	public static long getFileFolderSize(String path) {
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

			File file = new File(path);

			if (file.exists()) {
				return getFileSize(new File(path));
			} else {
				return 0;
			}
		}
		return 0;
	}

	public static long getFileSize(String path) {
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			return new File(path).length();
		}
		return 0;
	}

	/**
	 * 获取文件大小
	 * 
	 * @param length
	 * @return
	 */
	public static String formatFileSize(String path) {
		long length = getFileFolderSize(path);
		return formatFileSize(length);
	}

	/**
	 * 获取文件大小
	 * 
	 * @param length
	 * @return
	 */
	public static String formatFileSize(long length) {
		String result = null;
		int sub_string = 0;
		if (length >= 1073741824) {
			sub_string = String.valueOf((float) length / 1073741824).indexOf(".");
			result = ((float) length / 1073741824 + "000").substring(0, sub_string + 3) + "GB";
		} else if (length >= 1048576) {
			sub_string = String.valueOf((float) length / 1048576).indexOf(".");
			result = ((float) length / 1048576 + "000").substring(0, sub_string + 3) + "MB";
		} else if (length >= 1024) {
			sub_string = String.valueOf((float) length / 1024).indexOf(".");
			result = ((float) length / 1024 + "000").substring(0, sub_string + 3) + "KB";
		} else if (length < 1024)
			result = Long.toString(length) + "B";
		return result;
	}

	public static String GetFileName(String URL) {
		try {
			int start = URL.lastIndexOf("/");

			return (URL.substring(start + 1));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;

	}

	public static String GetFileName(String URL, String type) {
		try {

			int end = URL.indexOf(type);
			if (end != -1) {
				String temp = URL.substring(0, end + 3);
				int start = temp.lastIndexOf("/");

				return (temp.substring(start + 1));
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 递归删除文件和文件夹，注意，别乱用，防止誤殺
	 */
	public static void deleteFile(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}
		if (file.isDirectory()) {
			File[] childFile = file.listFiles();
			if (childFile == null || childFile.length == 0) {
				file.delete();
				return;
			}
			for (File f : childFile) {
				deleteFile(f);
			}
			file.delete();
		}
	}

	/**
	 * 清空文件夹内容，但不删除该文件夹
	 * 
	 * @param file
	 */
	public static void clearFolder(File file) {

		if (!file.isDirectory()) {
			return;
		}
		File[] childFile = file.listFiles();
		for (File f : childFile) {
			deleteFile(f);
		}
	}

	/**
	 * 得到Assets的文本文件的文本
	 */
	public static String getStringAsAssets(String fileName, Context context) {
		String res = "";
		try {
			// InputStream in = getResources().openRawResource(R.raw.ansi);
			// 读取assets文件夹中的txt文件,将它放入输入流中
			InputStream in = context.getAssets().open("subscribe.json");
			// 获得输入流的长度
			int length = in.available();
			// 创建字节输入
			byte[] buffer = new byte[length];
			// 放入字节输入中
			in.read(buffer);
			// 获得编码格式
			// String type = codetype(buffer);
			// 设置编码格式读取TXT
			res = EncodingUtils.getString(buffer, "UTF-8");
			// 关闭输入流
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}

		return res;
	}

	public static byte[] getBytes(String filePath) {
		byte[] buffer = null;
		try {
			File file = new File(filePath);
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = fis.read(b)) != -1) {
				bos.write(b, 0, n);
			}
			fis.close();
			bos.close();
			buffer = bos.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}

	/**
	 * 根据byte数组，生成文件
	 */
	public static void getFile(byte[] bfile, String filePath, String fileName) {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = null;
		try {
			File dir = new File(filePath);
			if (!dir.exists() && dir.isDirectory()) {// 判断文件目录是否存在
				dir.mkdirs();
			}
			file = new File(filePath + "/" + fileName);
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(bfile);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	/**
	 * 删除某个文件夹下的所有文件夹和文件
	 */
	public static String deletefile(String delpath) throws FileNotFoundException, IOException {
		try {

			File file = new File(delpath);
			if (!file.isDirectory()) {
				file.delete();
			} else if (file.isDirectory()) {
				String[] filelist = file.list();
				for (int i = 0; i < filelist.length; i++) {
					File delfile = new File(delpath + "/" + filelist[i]);
					if (!delfile.isDirectory()) {
						System.out.println("path=" + delfile.getPath());
						System.out.println("absolutepath=" + delfile.getAbsolutePath());
						System.out.println("name=" + delfile.getName());
						delfile.delete();
						System.out.println("删除文件成功");
					} else if (delfile.isDirectory()) {
						deletefile(delpath + "/" + filelist[i]);
					}
				}
				file.delete();

			}

		} catch (FileNotFoundException e) {
			System.out.println("deletefile()   Exception:" + e.getMessage());
		}
		return "";
	}
}
