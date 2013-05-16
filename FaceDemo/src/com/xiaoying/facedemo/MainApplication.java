
package com.xiaoying.facedemo;

import java.io.File;

import android.app.Application;
import android.os.Environment;

public class MainApplication extends Application {
	
	public static String APP_ROOT_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/FaceDemo/";
	
	public static String APP_PIC_PATH = APP_ROOT_DIR + "image/";

	@Override
	public void onCreate() {
		super.onCreate();
		
		File file = new File(APP_ROOT_DIR);
		if(!file.exists()) {
			file.mkdirs();
		}
		
		file = new File(APP_PIC_PATH);
		if(!file.exists()) {
			file.mkdirs();
		}
	}
}
