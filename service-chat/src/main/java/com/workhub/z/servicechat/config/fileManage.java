package com.workhub.z.servicechat.config;

import com.workhub.z.servicechat.model.FileInfoDto;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
*@Description: 文件控制类
*@Author: 忠
*@date: 2019/5/14
*/
public class fileManage {

    private static final Long FILE_SIZE = 300 * 1024 * 1024L;

    private static final String PATH = "C:/file-management-center/upload/";
    // TODO: 2019/5/14 文件上传
    /**
     * 文件上传(任意路径)
     * @param file
     * @param path
     * @return
     * @throws Exception
     */
    public static void uploadFile(MultipartFile file, String path) throws Exception {
        if (file == null){
            throw new NullPointerException();
        }
        if (FILE_SIZE<file.getSize()){
            throw new RuntimeException("上传文件超过" + FILE_SIZE / 1024 + "M");
        }
        file.transferTo(new File(path));
    }

    /**
     * 文件下载 远程url->下载到本地path
     * @param url
     * @param saveDir
     * @param fileName
     * @throws IOException
     */
    public static void download(String url, String saveDir, String fileName) throws IOException {
        InputStream ins = new FileInputStream(url+fileName);
        Path target = Paths.get(saveDir, fileName);
        Files.createDirectories(target.getParent());
        Files.copy(ins, target, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * 下载
     * @param filePath 文件路径
     * @param fileName 下载的文件名
     * @throws Exception
     */
    public static HttpServletResponse downloadFile(HttpServletResponse response,
                                                   String filePath, String fileName) throws Exception {
        File file = new File(PATH + "/" + filePath);
        response.setContentType("application/x-download");
        response.setHeader("Pragma", "public");
        response.setHeader("Cache-Control",
                "must-revalidate, post-check=0, pre-check=0");
        OutputStream out = null;
        InputStream in = null;
        fileName = new String(fileName.getBytes("GBK"), "ISO-8859-1");
        response.addHeader("Content-disposition", "attachment;filename=" + fileName);// 设定输出文件头

        try {
            out = response.getOutputStream();
            in = new FileInputStream(file);
            int len = in.available();
            byte[] b = new byte[len];
            in.read(b);
            out.write(b);
            out.flush();

        } catch (Exception e) {
            throw new Exception("下载失败!");
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
        return response;
    }
    public static HttpServletResponse downloadUploadFile(HttpServletResponse response,
                                                   String filePath, String fileName) throws Exception {
        File file = new File(filePath);
        response.setContentType("application/x-download");
        response.setHeader("Pragma", "public");
        response.setHeader("Cache-Control",
                "must-revalidate, post-check=0, pre-check=0");
        OutputStream out = null;
        InputStream in = null;
        fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("%5B", "\\[")
                .replaceAll("%5D", "\\]").replaceAll("\\+", "%20")
                .replaceAll("%28", "\\(").replaceAll("%29", "\\)")
                .replaceAll("%3B", ";").replaceAll("%40", "@")
                .replaceAll("%23", "\\#").replaceAll("%26", "\\&")
                .replaceAll("%7B", "\\{").replaceAll("%7D", "\\}")
                .replaceAll("%21", "\\!").replaceAll("%24", "\\$")
                .replaceAll("%21", "\\!").replaceAll("%25", "\\%")
                .replaceAll("%5E", "\\^").replaceAll("%2B", "\\+");
//        fileName = new String(fileName.getBytes("GBK"), "ISO-8859-1");
        response.addHeader("Content-disposition", "attachment;filename=" + fileName);// 设定输出文件头

        try {
            out = response.getOutputStream();
            in = new FileInputStream(file);
            int len = in.available();
            byte[] b = new byte[len];
            in.read(b);
            out.write(b);
            out.flush();

        } catch (Exception e) {
            throw new Exception("下载失败!");
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
        return response;
    }
    /**
     * 服务默认路径
     * @param file
     * @return
     * @throws Exception
     */
    public static void uploadFile(MultipartFile file) throws Exception {
        if (file == null){
            throw new NullPointerException();
        }
        if (FILE_SIZE<file.getSize()){
            throw new RuntimeException("上传文件超过" + FILE_SIZE / 1024 + "M");
        }
        file.transferTo(new File(PATH));
    }
    //上传文件，包含名称
    public static void uploadFile(MultipartFile file, String path, String filename) throws Exception {
        if (file == null) {
            throw new NullPointerException();
        }
        if (FILE_SIZE < file.getSize()) {
            throw new RuntimeException("上传文件超过" + FILE_SIZE / 1024 + "M");
        }
        createFolder(PATH + "/" + path);
        file.transferTo(new File(PATH + "/" + path + "/" + filename));
    }
    public static void uploadFile(MultipartFile file, String path, String filename,int file_size) throws Exception {
        if (file == null) {
            throw new NullPointerException();
        }
        if (file_size < file.getSize()/1024/1024) {
            throw new RuntimeException("上传文件超过" + file_size  + "M");
        }
        createFolder( path);
        file.transferTo(new File(path + "/" + filename));
    }
    //创建文件夹
    public static void createFolder(String path) {
        File folderPath = new File(path);
        if (!folderPath.exists()) {
            try {
                folderPath.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }

        }
    }

    /**
     * 删除文件(不能有子目录)
     * @param url
     */
/*    public static void delFile(String url){
        File file=new File(url);
        if(file.exists()&&file.isFile())
            file.delete();
    }*/
    public static void delFile(String url) {
        File file = new File(PATH + "/" + url);
        if (file.exists() && file.isFile())
            file.delete();
    }
    /**
     * 删除文件(不能有子目录)
     * @param url
     */
    public static void delUploadFile(String url) {
        File file = new File(url);
        if (file.exists() && file.isFile())
            file.delete();
    }

    /**
     * 删除目录(递归删除子目录以及文件)
     * @param path
     */
    public static void delDir(String path){
        File dir=new File(path);
        if(dir.exists()){
            File[] tmp=dir.listFiles();
            Arrays.stream(tmp).forEach(tmpFiles ->{
                if(tmpFiles.isDirectory()) delDir(path+"/"+tmpFiles.getName());
                else tmpFiles.delete();
            });
            dir.delete();
        }
    }
    // TODO: 2019/5/14 批量文件删除（本地磁盘）
    public static void delFile(Set<String> setUrl){
        setUrl.stream().forEach(url->{
            File file=new File(url);
            if(file.exists()&&file.isFile())
                file.delete();
        });
    }
    // TODO: 2019/5/14 文件属性获取
    /**
     * 获取文件夹，文件
     * @param path
     * @return
     */
    public static List<FileInfoDto> getFiles(String path) {
        List<FileInfoDto> files = new ArrayList<FileInfoDto>();
        File file = new File(path);
        File[] tempList = file.listFiles();
        Arrays.stream(tempList).forEach(tempListFile ->{
            FileInfoDto fileInfoDto = new FileInfoDto();
            if (tempListFile.isFile()) fileInfoDto.setType("FILE");
            else if(tempListFile.isDirectory())fileInfoDto.setType("DIRECTORY");

            fileInfoDto.setPath(tempListFile.toString());
            fileInfoDto.setDate(new Date(tempListFile.lastModified()));
            files.add(fileInfoDto);
        });
        return files;
    }
}
