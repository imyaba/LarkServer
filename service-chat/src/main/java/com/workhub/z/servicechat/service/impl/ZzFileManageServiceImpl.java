package com.workhub.z.servicechat.service.impl;

import com.workhub.z.servicechat.config.FileTypeEnum;
import com.workhub.z.servicechat.config.RandomId;
import com.workhub.z.servicechat.config.fileManage;
import com.workhub.z.servicechat.service.ZzFileManageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * 附件上传下载等相关功能service
 *
 * @author zhuqz
 * @since 2019-06-04 10:22:34
 */
@Service("zzFileManageService")
public class ZzFileManageServiceImpl implements ZzFileManageService {

    // 上传文件大小和路径配置
    @Value("${file_size_max}")
    private String file_size_max;

    @Value("${file_save_path_group}")
    private String file_root_path;
    //上传附件
    public Map<String, String> singleFileUpload(MultipartFile file) throws Exception {
        Map<String, String> resMap = new HashMap<>();
        resMap.put("res", "1");
        try {
            Calendar cal = Calendar.getInstance();
            String year = cal.get(cal.YEAR) + "";
            String month = (cal.get(cal.MONTH) + 1) + "";
            if (month.length() == 1) {
                month = "0" + month;
            }

            String date = (cal.get(cal.DATE)) + "";
            if (date.length() == 1) {
                date = "0" + date;
            }
            String fileId = RandomId.getUUID();
            String fileName = file.getOriginalFilename();
            String suffix = "";
            String file_ext = "";
            if (fileName.indexOf(".") != -1) {
                suffix = fileName.substring(fileName.lastIndexOf("."));
                fileName = fileName.substring(0,fileName.lastIndexOf("."));
            }
            if(!"".equals(suffix) && !".".equals(suffix)){
                file_ext=suffix.substring(suffix.indexOf(".")+1);
            }
           // String newFileName = fileId + suffix;
            String newFileName = fileId;
            String filepath = file_root_path + year + month + date;

            String file_type = "";
            FileTypeEnum fileTypeEnum = FileTypeEnum.getEnumByValue(file_ext);
            file_type = fileTypeEnum.getType();

            fileManage.uploadFile(file, filepath, newFileName,Integer.valueOf(file_size_max));
            resMap.put("file_id", fileId);
            resMap.put("file_path", filepath + "/" + newFileName);
            resMap.put("file_size", String.valueOf(file.getSize()));
            resMap.put("file_upload_name", fileName);
            resMap.put("file_ext", file_ext.toLowerCase());
            resMap.put("file_type", file_type);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return resMap;
    }

    //删除附件
    public String delUploadFile(String path) throws Exception {
        String res = "1";
        try {
            fileManage.delUploadFile(path);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return res;
    }

    //附件下载
    public HttpServletResponse downloadFile(HttpServletResponse response, String filePath, String fileName) throws Exception {
        try {
            return fileManage.downloadUploadFile(response, filePath, fileName);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }
}
