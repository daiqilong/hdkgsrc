package com.whir.component.util;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * 临时目录处理器<p>
 * &nbsp;&nbsp;&nbsp;&nbsp;提供一组方法用于管理临时目录的增加、清理等功能。
 * @author chenx
 * @version 1.2 2013-7-29
 */
public class TempDirectoryProcessor {
	private static Logger _log = Logger.getLogger(TempDirectoryProcessor.class);
	private static String _rootPath = System.getProperty("user.dir");
	/**
	 * 创建处理器
	 * @param 指定的根目录,如果所设置的目录不存在将使用System.getProperty("user.dir")指定的目录.
	 * @author chenx
	 * @since 1.2 2013-7-29
	 */
	public static void setRootPath(String rootPath) {
		//将原先的目录删除
		for(DirLocation dl : DirLocation.values()) {
			File directory = new File(_rootPath + File.separator + dl);
			try {
				FileUtils.deleteDirectory(directory);
			} catch (IOException e) {
			}
		}
		if(rootPath != null ) {
			File tmp = new File(rootPath);
			if(!tmp.exists()) {
				if(tmp.mkdir()) {
					_rootPath = rootPath;
				}
			} else {
				_rootPath = rootPath;
			}
		}
		for(DirLocation dl : DirLocation.values()) {
			File directory = new File(_rootPath + File.separator + dl);
			if(!directory.exists()){
				directory.mkdir();
			} else {
				try {
					FileUtils.cleanDirectory(directory);
				} catch (IOException e) {
				}
			}
		}
	}
	/**
	 * 在指定的基准目录下生成一个临时路径
	 * @param 指定的基准目录
	 * @author chenx
	 * @since 1.2 2013-7-29
	 */
	public static String create(DirLocation location) {
		File randomDirectory = new File(_rootPath + File.separator + location + File.separator + UUID.randomUUID().toString().replaceAll("-", ""));
		randomDirectory.mkdir();
		return randomDirectory.getAbsolutePath();
	}
	/**
	 * 删除指定的文件夹
	 * @author chenx
	 * @since 1.2 2013-7-29
	 */
	public static void clean(String filePath) {
		try {
			FileUtils.deleteDirectory(new File(filePath));
		} catch (IOException e) {
		}
	}
	/**
	 * 清空所有文件夹
	 * @author chenx
	 * @since 1.2 2013-7-29
	 */
	public static void cleanAll() {
		for(DirLocation dl : DirLocation.values()) {
			try {
				FileUtils.cleanDirectory(new File(_rootPath + File.separator + dl));
			} catch (IOException e) {
				_log.error("TempDirectoryProcessor.class", e);
			}
		}
	}
	/**
	 * 获取根目录位置
	 * @author chenx
	 * @since 1.2 2013-7-29
	 */
	public static String getRootPath() {
		return _rootPath;
	}
	/**
	 * 预定义临时目录基准位置(可扩展)
	 * @author chenx
	 * @version 1.2 2013-7-29
	 */
	public static enum DirLocation {
		eep,//封装
		disk,//光盘导出
		video,//视频
		flash,//flash转换
		export,//数据导出
		upload,//上传
		viewImg,//看图
		edocReader,//电子文件阅读器
		exportXML,//开放管理导出
		other//其他
	}
}
