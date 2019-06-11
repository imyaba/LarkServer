package com.workhub.z.servicechat.service.impl;

import com.workhub.z.servicechat.config.RandomId;
import com.workhub.z.servicechat.config.fileManage;
import com.workhub.z.servicechat.service.ZzFileManageService;
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
            if (fileName.indexOf(".") != -1) {
                suffix = fileName.substring(fileName.lastIndexOf("."));
            }

            String newFileName = fileId + suffix;

            String filepath = year + month + date;

            fileManage.uploadFile(file, filepath, newFileName);

            resMap.put("file_id", fileId);
            resMap.put("file_real_name", newFileName);
            resMap.put("file_path", filepath);
            resMap.put("file_size", String.valueOf(file.getSize()));
            resMap.put("file_upload_name", fileName);
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
            fileManage.delFile(path);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return res;
    }

    //附件下载
    public HttpServletResponse downloadFile(HttpServletResponse response, String filePath, String fileName) throws Exception {
        try {
            return fileManage.downloadFile(response, filePath, fileName);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }
}