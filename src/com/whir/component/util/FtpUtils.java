package com.whir.component.util;

import java.io.IOException;
import java.util.List;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.*;
import java.io.InputStream;
import java.io.File;

/**
 *
 * 功能描述：ftp客户端操作功能，实现连接ftp服务器、文件上传、下载、目录跳转，创建文件夹、检查文件夹是否存在
 *
 *
 * @author zhoudl
 *
 */
public class FtpUtils {
        private static Log log = LogFactory.getLog(FtpUtils.class);
        FTPClient ftpClient;

        public FtpUtils() {
        }

        /**
         * 连接FTP服务器
         *
         * @param server
         *            FTP服务器IP地址
         * @param user
         *            登录名
         * @param password
         *            密码
         * @param encoding
         *            String 编码方式
         * @throws IOException
         */
        public void connectServer(String server, String user, String password,
                        String encoding) throws IOException {
                ftpClient = new FTPClient();
                ftpClient.setControlEncoding(encoding);
                ftpClient.connect(server);
                ftpClient.login(user, password);
        }

        /**
         *
         * 连接FTP服务器,并指定登录路径
         *
         * @param server
         *            FTP服务器IP地址
         *
         * @param user
         *            登录名
         *
         * @param password
         *            密码
         *@param encoding
         *            String 编码方式
         *
         * @param path
         *            登录路径
         *
         * @throws IOException
         *
         */
        public void connectServer(String server, String user, String password,
                        String encoding, String path) throws IOException {
                ftpClient = new FTPClient();
                ftpClient.setControlEncoding(encoding);
                ftpClient.connect(server);
                ftpClient.login(user, password);
                ftpClient.changeWorkingDirectory(path);
        }

        /**
         * 连接FTP服务器,设置默认编码为UTF-8
         *
         * @param server
         *            FTP服务器IP地址
         * @param user
         *            登录名
         * @param password
         *            密码
         * @throws IOException
         */
        public void connectServer(String server, String user, String password)
                        throws IOException {
                ftpClient = new FTPClient();
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.connect(server);
                ftpClient.login(user, password);
        }

        /**
         * upload 上传文件
         *
         * @throws java.lang.Exception
         * @return -1 文件不存在 -2 文件内容为空 >0 成功上传，返回文件的大小，0 上传失败，如果path路径不存在就创建新的路径
         * @param remote
         *            上传后的新文件名
         * @param locale
         *            上传的文件名
         * @param path
         *            String 上传保存的路径名
         * @throws Exception
         */
        public long upload(String locale, String remote, String path)
                        throws Exception {
                boolean createDir = createDir(path);
                if (createDir) {
                        ftpClient.changeWorkingDirectory(path);
                }
                return upload(locale, remote);
        }

        /**
         * upload 上传文件
         *
         * @throws java.lang.Exception
         * @return -1 文件不存在 -2 文件内容为空 >0 成功上传，返回文件的大小，0 上传失败
         * @param remote
         *            上传后的新文件名
         * @param locale
         *            上传的文件(路径加文件名)
         */
        public long upload(String locale, String remote) throws IOException {
                boolean result = false;
                File file_in = new java.io.File(locale);
                if (!file_in.exists()) {
                        return -1;
                }
                if (file_in.length() == 0) {
                        return -2;
                }
                String type = file_in.getName().substring(
                                file_in.getName().lastIndexOf('.') + 1);
                if(StringUtils.isBlank(remote)){
                        remote = file_in.getName().substring(0, file_in.getName().indexOf('.'));
                }
                remote = remote + "." + type;
                InputStream input = new FileInputStream(locale);
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                result = ftpClient.storeFile(remote, input);
                if (result) {
                        return file_in.length();
                } else {
                        return 0;
                }
        }

        /**
         * upload
         *
         * @throws java.lang.Exception
         * @return long
         * @param locale
         *            String 上传文件的路径文件名
         */
        public long upload(String locale) throws Exception {
                String remote = "";
                if (StringUtils.isNotBlank(locale)) {
                        if (locale.indexOf("/") > -1) {
                                remote = locale.substring(locale.lastIndexOf("/") + 1);
                        } else {
                                remote = locale;
                        }
                }
                boolean result = false;
                File file_in = new java.io.File(locale);
                if (!file_in.exists()) {
                        return -1;
                }
                if (file_in.length() == 0) {
                        return -2;
                }
                InputStream input = new FileInputStream(locale);
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                result = ftpClient.storeFile(remote, input);
                if (result) {
                        return file_in.length();
                } else {
                        return 0;
                }
        }

        /**
         *
         * 取得某个目录下的所有文件列表
         *
         *
         * @return list
         * @throws IOException
         *
         * @throws IOException
         *
         */
        public List getFileList(String path) throws IOException {
                List list = new ArrayList();
                if (StringUtils.isNotBlank(path)) {
                        FTPFile[] ftpFiles = ftpClient.listFiles(path);
                        if (ftpFiles.length > 0) {
                                for (int i = 0; i < ftpFiles.length; i++) {
                                        list.add(ftpFiles[i].getName());
                                }
                        }
                }
                return list;
        }

        /**
         *
         * 获得文件和目录列表
         *
         *
         * @return list
         *
         * @throws IOException
         *
         */

        public List getFileList() throws IOException {
                List list = new ArrayList();
                FTPFile[] ftpFiles = ftpClient.listFiles();
                if (ftpFiles.length > 0) {
                        for (int i = 0; i < ftpFiles.length; i++) {
                                list.add(ftpFiles[i].getName());
                        }
                }
                return list;
        }

        /**
         * download 从 ftp下载文件到本地
         *
         * @throws java.lang.Exception
         * @param locale
         *            本地生成的文件名
         * @param remote
         *            服务器上的路径加文件名
         *
         */
        public void download(String remote, String locale) throws Exception {
                FileOutputStream fos = null;
                fos = new FileOutputStream(locale);
                ftpClient.setBufferSize(1024);
                // 设置文件类型（二进制）
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.retrieveFile(remote, fos);
        }

        /**
         * download 从 ftp下载文件到本地
         *
         * @throws java.lang.Exception
         * @param locale
         *            本地生成的文件名
         * @param remote
         *            服务器上的路径加文件名
         * @param localePath
         *            本地保存路径
         *
         */
        public void download(String remote, String locale, String localePath) throws Exception {
                FileOutputStream fos = null;
                if (!localePath.endsWith("/")) {
                        localePath += "/";
                }
                File remoteFile = new File(remote);
                File file = new File(localePath);
                if(!file.exists()){
                        file.mkdirs();
                }
                String type = remoteFile.getName().substring(
                                remoteFile.getName().lastIndexOf('.') + 1);
                locale = locale + "." + type;
                locale = localePath + locale;
                download(remote, locale);
        }
        /**
         *
         * 转到指定目录
         *
         *
         *
         * @param path
         *
         * @throws IOException
         *
         */
        public boolean cdPath(String path) throws IOException {
                if (StringUtils.isNotBlank(path)) {
                        return ftpClient.changeWorkingDirectory(path);
                }else{
                        return false;
                }
        }

        /**
         * 删除文件
         *
         * @param pathname
         *            文件路径名
         * @return 删除文件是否成功：true, 成功; false, 失败
         * @throws IOException
         */
        public boolean deleteFile(String pathname) throws IOException {
                if (StringUtils.isNotBlank(pathname)) {
                        return ftpClient.deleteFile(pathname);
                } else {
                        return false;
                }
        }

        /**
         * closeServer 断开与ftp服务器的链接
         *
         * @throws java.io.IOException
         */
        public void closeFTPClient() throws IOException {
                try {
                        if (ftpClient != null) {
                                ftpClient.logout();
                                ftpClient.disconnect();
                        }
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }

        /**
         * 递归创建远程服务器目录
         *
         * @param dir
         *            String 远程服务器文件绝对路径
         * @param ftpClient
         * @throws Exception
         */
        private boolean createDir(String directory) throws Exception {
                if (StringUtils.isNotBlank(directory)) {
                        if (!directory.endsWith("/")) {
                                directory += "/";
                        }
                        if (!directory.equalsIgnoreCase("/")
                                        && !ftpClient.changeWorkingDirectory(directory)) {
                                // 如果远程目录不存在，则递归创建远程服务器目录
                                int start = 0;
                                int end = 0;
                                if (directory.startsWith("/")) {
                                        start = 1;
                                } else {
                                        start = 0;
                                }
                                end = directory.indexOf("/", start);
                                while (true) {
                                        String subDirectory = directory.substring(start, end);
                                        if (!ftpClient.changeWorkingDirectory(subDirectory)) {
                                                if (ftpClient.makeDirectory(subDirectory)) {
                                                        ftpClient.changeWorkingDirectory(subDirectory);
                                                } else {
                                                        log.debug("创建目录失败");
                                                        return false;
                                                }
                                        }
                                        start = end + 1;
                                        end = directory.indexOf("/", start);
                                        // 检查所有目录是否创建完毕
                                        if (end <= start) {
                                                break;
                                        }
                                }
                        }
                        log.debug("创建目录成功");
                        return true;
                } else {
                        log.debug("创建目录失败");
                        return false;
                }
        }

        /**
         * 检查文件是否存在
         *
         * @param file
         *            路径加文件名
         * @return boolean
         */
        private boolean isFileExist(String file) {
                // 检查远程文件是否存在
                FTPFile[] files;
                try {
                        files = ftpClient.listFiles(file);
                        if (files.length != 1) {
                                return false;
                        } else {
                                return true;
                        }
                } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                }
        }

        /**
         * 检查目录是否存在
         *
         * @param dir
         *            路径名
         * @return boolean
         */
        private boolean isDirExist(String dir) {
                boolean result = false;
                try {
                        result = ftpClient.changeWorkingDirectory(dir);
                } catch (IOException e) {
                        e.printStackTrace();
                }
                return result;
        }
}
