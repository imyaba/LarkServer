package com.workhub.larktools.controller;

import com.github.hollykunge.security.common.msg.ListRestResponse;
import com.github.hollykunge.security.common.msg.ObjectRestResponse;
import com.github.hollykunge.security.common.util.UUIDUtils;
import com.workhub.larktools.entity.ToolFile;
import com.workhub.larktools.feign.IFastDFSService;
import com.workhub.larktools.service.ToolUploadService;
import com.workhub.larktools.tool.CommonUtil;
import com.workhub.larktools.vo.ToolFileVo;
import feign.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

/**
 * 工具上传下载
 * @author zhuqz
 * @since 2019-08-13 10:22:34
 */
@RestController
@RequestMapping("/toolUpload")
public class ToolUploadController {
    @Autowired
    private IFastDFSService iFastDFSService;
    @Resource
    private HttpServletRequest httpServletRequest;
    @Resource
    private ToolUploadService toolUploadService;
    //上传
    /**
    * @MethodName: singleFileUpload
     * @Description: 上传文件
     * @Param: [file, fileInf] fileInf：treeId节点；describe描述;
     * @Return: com.github.hollykunge.security.common.msg.ObjectRestResponse
     * @Author: admin
     * @Date: 2019/8/16
    **/
    @PostMapping("/uploadFile")
    public ObjectRestResponse singleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam Map fileInf) throws Exception{
        String userId=CommonUtil.nulToEmptyString(httpServletRequest.getHeader("userId"));
        String userName = URLDecoder.decode(CommonUtil.nulToEmptyString(httpServletRequest.getHeader("userName")),"UTF-8");
        ObjectRestResponse fastDfs = this.iFastDFSService.sensitiveUpload(file);

        if(fastDfs!=null && fastDfs.getResult()!=null){
             Map fileInfo = (Map)fastDfs.getResult();
            if(fileInfo!=null){
                ToolFile toolFile = new ToolFile();
                toolFile.setId(UUIDUtils.generateShortUuid());
                toolFile.setFileId(CommonUtil.nulToEmptyString(fileInfo.get("fileId")));
                toolFile.setFileName(CommonUtil.nulToEmptyString(fileInfo.get("fileName")));
                toolFile.setFileExt(CommonUtil.nulToEmptyString(fileInfo.get("fileExt")));
                toolFile.setCreator(userId);
                toolFile.setCreatorName(userName);
                toolFile.setDescribe(CommonUtil.nulToEmptyString(fileInf.get("describe")));
                toolFile.setTreeId(CommonUtil.nulToEmptyString(fileInf.get("treeId")));
                this.toolUploadService.add(toolFile);
            }
        }

        return fastDfs;
    }
    @GetMapping("/downloadFile")
    //下载
        public void downloadFile(HttpServletRequest request, HttpServletResponse response) {
            String fileId = request.getParameter("fileId");
            try {
                Response feignResponse =iFastDFSService.sensitiveDownload(fileId);
                Response.Body body = feignResponse.body();
                for(Object key : feignResponse.headers().keySet()){
                    List<String> kList = (List)feignResponse.headers().get(key);
                    for(String val : kList){
                        response.setHeader((key==null)?"":key.toString(), val);
                    }
                }
                try(InputStream inputStream = body.asInputStream();
                    OutputStream outputStream = response.getOutputStream()
                ){
                    byte[] b = new byte[inputStream.available()];
                    inputStream.read(b);
                    outputStream.write(b);
                    outputStream.flush();
                }catch (IOException e){
                    throw new IOException("IO流异常", e);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    @PostMapping("/removeFile")
    public ObjectRestResponse removeFile(@RequestParam("fileId") String fileId) throws Exception{
        String userId=CommonUtil.nulToEmptyString(httpServletRequest.getHeader("userId"));
        String userName = URLDecoder.decode(CommonUtil.nulToEmptyString(httpServletRequest.getHeader("userName")),"UTF-8");
        ObjectRestResponse fastDfs = this.iFastDFSService.removeFile(fileId);
        if(fastDfs!=null && fastDfs.isRel()){
            ToolFile toolFile = new ToolFile();
            toolFile.setFileId(fileId);
            toolFile.setUpdator(userId);
            toolFile.setUpdatorName(userName);
            this.toolUploadService.delete(toolFile);
        }
        return fastDfs;
    }
    //更换文件所属节点
    @PostMapping("/updateTreeNode")
    //fileInf:fileId文件id；newTreeId新的树节点
    public ObjectRestResponse updateTreeNode(@RequestParam Map fileInf) throws Exception{
        String userId=CommonUtil.nulToEmptyString(httpServletRequest.getHeader("userId"));
        String userName = URLDecoder.decode(CommonUtil.nulToEmptyString(httpServletRequest.getHeader("userName")),"UTF-8");
        ObjectRestResponse fastDfs = new ObjectRestResponse();
        fastDfs.rel(true);

        ToolFile toolFile = new ToolFile();
        toolFile.setFileId(CommonUtil.nulToEmptyString(fileInf.get("fileId")));
        toolFile.setUpdator(userId);
        toolFile.setUpdatorName(userName);
        toolFile.setTreeId(CommonUtil.nulToEmptyString(fileInf.get("newTreeId")));
        this.toolUploadService.updateTreeNode(toolFile);

        return fastDfs;
    }
    //获取某个树节点下所有的文件
    @GetMapping("/queryNodeFile")
    public ListRestResponse<ToolFileVo> queryNodeFile(@RequestParam String treeId) throws Exception{
       return this.toolUploadService.queryNodeFile(treeId);
    }
    //获取整个树所有的文件
    @GetMapping("/queryAllFile")
    public ListRestResponse<ToolFileVo> queryAllFile() throws Exception{
        String userId=CommonUtil.nulToEmptyString(httpServletRequest.getHeader("userId"));
        return this.toolUploadService.queryAllFile(userId);
    }
}
