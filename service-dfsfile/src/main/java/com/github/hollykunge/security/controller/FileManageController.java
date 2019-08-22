package com.github.hollykunge.security.controller;

import com.github.hollykunge.security.common.msg.ObjectRestResponse;
import com.github.hollykunge.security.common.msg.TableResultResponse;
import com.github.hollykunge.security.common.util.UUIDUtils;
import com.github.hollykunge.security.entity.FileManageInf;
import com.github.hollykunge.security.service.FileMangeService;
import com.github.hollykunge.security.util.CommonUtil;
import com.github.hollykunge.security.util.EncryptionAndDeciphering;
import com.github.hollykunge.security.util.FastDFSClientWrapper;
import com.github.hollykunge.security.util.FileTypeEnum;
import com.github.hollykunge.security.vo.FileMonitoringVO;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * file文件统一接口
 * @author zhuqz
 * @since 2019-07-17
 */
@RestController
@RequestMapping("fileManage")
public class FileManageController {
    private static Logger log = LoggerFactory.getLogger(FileManageController.class);
    @Autowired
    private FastDFSClientWrapper dfsClient;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private FileMangeService fileMangeService;

    //上传
    @PostMapping("/upload")
    @ResponseBody
    public ObjectRestResponse<FileManageInf> upload(@RequestParam("file") MultipartFile file) throws Exception {
        String imgUrl = dfsClient.uploadFile(file);
        String userId = (request.getHeader("userId")==null)?"":request.getHeader("userId");
        String userName = URLDecoder.decode((request.getHeader("userName")==null)?"":request.getHeader("userName"),"UTF-8");
        String fileId = UUIDUtils.generateShortUuid();

        FileManageInf fileManageInf = new FileManageInf();
        fileManageInf.setFileId(fileId);
        fileManageInf.setCreator(userId);
        fileManageInf.setCreatorName(userName);
        fileManageInf.setFileEncrypeFlg("0");
        fileManageInf.setPath(imgUrl);

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
        fileManageInf.setFileExt(file_ext);
        fileManageInf.setFileName(fileName);

        String file_type = "";
        FileTypeEnum fileTypeEnum = FileTypeEnum.getEnumByValue(file_ext);
        file_type = fileTypeEnum.getType();
        fileManageInf.setFileType(file_type.toLowerCase());
        fileManageInf.setSizes(Double.valueOf(file.getSize()));
        fileManageInf.setSendFlg("0");
        try{
            this.fileMangeService.insert(fileManageInf);
        }catch (Exception e){
            dfsClient.deleteFile(imgUrl);
            log.error(CommonUtil.getExceptionMessage(e));
            throw e;
        }
        return new ObjectRestResponse<>().data(fileManageInf).rel(true);
    }
    /**
     * 上传加密文件接口
     * @param file file文件
     * @return 文件访问路径
     * @throws Exception
     */
    @PostMapping("/sensitiveUpload")
    @ResponseBody
    public ObjectRestResponse<String> uploadSensitiveFile(@RequestParam("file") MultipartFile file) throws Exception {
        String imgUrl = dfsClient.uploadbase64SensitiveFile(file);
        String userId = (request.getHeader("userId")==null)?"":request.getHeader("userId");
        String userName = URLDecoder.decode((request.getHeader("userName")==null)?"":request.getHeader("userName"),"UTF-8");
        String fileId = UUIDUtils.generateShortUuid();

        FileManageInf fileManageInf = new FileManageInf();
        fileManageInf.setFileId(fileId);
        fileManageInf.setCreator(userId);
        fileManageInf.setCreatorName(userName);
        fileManageInf.setCreateTime(new Date());
        fileManageInf.setFileEncrypeFlg("1");//base64加密
        fileManageInf.setPath(imgUrl);

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
        fileManageInf.setFileExt(file_ext);
        fileManageInf.setFileName(fileName);

        String file_type = "";
        FileTypeEnum fileTypeEnum = FileTypeEnum.getEnumByValue(file_ext);
        file_type = fileTypeEnum.getType();
        fileManageInf.setFileType(file_type.toLowerCase());
        fileManageInf.setSizes(Double.valueOf(file.getSize()));
        fileManageInf.setSendFlg("0");
        try{
            this.fileMangeService.insert(fileManageInf);
        }catch (Exception e){
            dfsClient.deleteFile(imgUrl);
            log.error(CommonUtil.getExceptionMessage(e));
            throw e;
        }
        return new ObjectRestResponse<>().data(fileManageInf).rel(true);
    }
    /**
     * 上传加密文件接口第二种方式（每个字节进行位移+5）
     * @param file file文件
     * @return 文件访问路径
     * @throws Exception
     */
    @PostMapping("/sensitiveUpload2")
    @ResponseBody
    public ObjectRestResponse<String> uploadSensitiveFile2(@RequestParam("file") MultipartFile file) throws Exception {

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

        byte[] bytes = EncryptionAndDeciphering.encryptFile(file);
        String imgUrl = dfsClient.uploadFile(bytes,file_ext);
        String userId = (request.getHeader("userId")==null)?"":request.getHeader("userId");
        String userName = URLDecoder.decode((request.getHeader("userName")==null)?"":request.getHeader("userName"),"UTF-8");
        String fileId = UUIDUtils.generateShortUuid();

        FileManageInf fileManageInf = new FileManageInf();
        fileManageInf.setFileId(fileId);
        fileManageInf.setCreator(userId);
        fileManageInf.setCreatorName(userName);
        fileManageInf.setCreateTime(new Date());
        fileManageInf.setFileEncrypeFlg("2");//位移加密
        fileManageInf.setPath(imgUrl);


        fileManageInf.setFileExt(file_ext);
        fileManageInf.setFileName(fileName);

        String file_type = "";
        FileTypeEnum fileTypeEnum = FileTypeEnum.getEnumByValue(file_ext);
        file_type = fileTypeEnum.getType();
        fileManageInf.setFileType(file_type.toLowerCase());
        fileManageInf.setSizes(Double.valueOf(file.getSize()));
        fileManageInf.setSendFlg("0");
        try{
            this.fileMangeService.insert(fileManageInf);
        }catch (Exception e){
            dfsClient.deleteFile(imgUrl);
            log.error(CommonUtil.getExceptionMessage(e));
            throw e;
        }
        return new ObjectRestResponse<>().data(fileManageInf).rel(true);
    }
    /**
     * 删除文件接口
     */
    @RequestMapping(value = "delete",method = RequestMethod.DELETE)
    @ResponseBody
    //@Transactional(rollbackFor = Exception.class)
    public ObjectRestResponse<Boolean> remove(@RequestParam String fileId) throws Exception {
        FileManageInf fileManageInf = this.fileMangeService.queryById(fileId);
        try {
            dfsClient.deleteFile(fileManageInf.getPath());
        }catch (Exception e){
            log.error(CommonUtil.getExceptionMessage(e));
            return new ObjectRestResponse<>().rel(false);
        }
        this.fileMangeService.deleteById(fileId);
        return new ObjectRestResponse<>().rel(true);
    }

    /**
     * 文件下载
     */
    @GetMapping("/download")
    public void  download(@RequestParam String fileId, HttpServletResponse response) throws Exception{
        FileManageInf fileManageInf = this.fileMangeService.queryById(fileId);
        String fileName = fileManageInf.getFileName();//下载名称
        String fileExt = fileManageInf.getFileExt();//后缀
        if(!"".equals(fileExt)){
            fileName=fileName+"."+fileExt;
        }
        byte[] data = dfsClient.download(fileManageInf.getPath());
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        ServletOutputStream outputStream = response.getOutputStream();
        IOUtils.write(data, outputStream);
    }

    /**
     * 下载加密文件
     * @param response
     * @throws Exception
     */
    @GetMapping("/sensitiveDownload")
    public void  downloadSensitiveFile(@RequestParam String fileId, HttpServletResponse response) throws Exception{
        FileManageInf fileManageInf = this.fileMangeService.queryById(fileId);
        String fileName = fileManageInf.getFileName();//下载名称
        String fileExt = fileManageInf.getFileExt();//后缀
        if(!"".equals(fileExt)){
            fileName=fileName+"."+fileExt;
        }
        byte[] data = dfsClient.downloadBase64SensitiveFile(fileManageInf.getPath());
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        ServletOutputStream outputStream = response.getOutputStream();
        IOUtils.write(data, outputStream);
    }
    /**
     * 下载加密文件(位移减去5)
     * @param response
     * @throws Exception
     */
    @GetMapping("/sensitiveDownload2")
    public void  downloadSensitiveFile2(@RequestParam String fileId, HttpServletResponse response) throws Exception{
        FileManageInf fileManageInf = this.fileMangeService.queryById(fileId);
        String fileName = fileManageInf.getFileName();//下载名称
        String fileExt = fileManageInf.getFileExt();//后缀
        if(!"".equals(fileExt)){
            fileName=fileName+"."+fileExt;
        }
        byte[] data = dfsClient.download(fileManageInf.getPath());
        data= EncryptionAndDeciphering.decipherFile(data);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        ServletOutputStream outputStream = response.getOutputStream();
        IOUtils.write(data, outputStream);
    }
    //设置文件文已发送状态
    //附件id，附件密级，附件所属工作类型0研讨1工具
    @PostMapping("/fileUpdate")
    @ResponseBody
    public ObjectRestResponse fileUpdate(@RequestParam String fileId,@RequestParam String level,@RequestParam String type) throws Exception {
        int i = this.fileMangeService.fileUpdate(fileId,level,type);
        return new ObjectRestResponse<>().rel(true);
    }
    //图片展示
    @RequestMapping("/getImage")
    public void getFile(HttpServletRequest request , HttpServletResponse response) throws IOException {
        OutputStream outputStream = null;
        try {
            //读取路径下面的文件
            String fileId = request.getParameter("fileId");
            FileManageInf fileManageInf = this.fileMangeService.queryById(fileId);
            //读取路径下面的文件
            if(fileManageInf == null) return;
            byte[] data = dfsClient.download(fileManageInf.getPath());
            //获取文件后缀名格式
            String ext = ((fileManageInf.getFileExt()==null)?"":fileManageInf.getFileExt());
            //判断图片格式,设置相应的输出文件格式
            if(ext.equals("jpg")){
                response.setContentType("image/jpeg");
            }else if(ext.equals("JPG")){
                response.setContentType("image/jpeg");
            }else if(ext.equals("png")){
                response.setContentType("image/png");
            }else if(ext.equals("PNG")){
                response.setContentType("image/png");
            }
            outputStream = new BufferedOutputStream(response.getOutputStream());
            outputStream.write(data);
            outputStream.flush();
        }catch (Exception e){
            log.error(CommonUtil.getExceptionMessage(e));
        }finally {
            //关流
            if(outputStream!=null){
                outputStream.close();
            }
        }

    }
    //加密图片展示
    @RequestMapping("/getSensitiveImage")
    public void getSensitiveImage(HttpServletRequest request , HttpServletResponse response) throws IOException {
        OutputStream outputStream = null;
        try {
            //读取路径下面的文件
            String fileId = request.getParameter("fileId");
            FileManageInf fileManageInf = this.fileMangeService.queryById(fileId);
            //读取路径下面的文件
            if(fileManageInf == null) return;
            byte[] data = dfsClient.downloadBase64SensitiveFile(fileManageInf.getPath());
            //获取文件后缀名格式
            String ext = ((fileManageInf.getFileExt()==null)?"":fileManageInf.getFileExt());
            //判断图片格式,设置相应的输出文件格式
            if(ext.equals("jpg")){
                response.setContentType("image/jpeg");
            }else if(ext.equals("JPG")){
                response.setContentType("image/jpeg");
            }else if(ext.equals("png")){
                response.setContentType("image/png");
            }else if(ext.equals("PNG")){
                response.setContentType("image/png");
            }
            outputStream = new BufferedOutputStream(response.getOutputStream());
            outputStream.write(data);
            outputStream.flush();
        }catch (Exception e){
            log.error(CommonUtil.getExceptionMessage(e));
        }finally {
            //关流
            if(outputStream!=null){
                outputStream.close();
            }
        }

    }
    //加密图片展示（位移加密图片）
    @RequestMapping("/getSensitiveImage2")
    public void getSensitiveImage2(HttpServletRequest request , HttpServletResponse response) throws IOException {
        OutputStream outputStream = null;
        try {
            //读取路径下面的文件
            String fileId = request.getParameter("fileId");
            FileManageInf fileManageInf = this.fileMangeService.queryById(fileId);
            //读取路径下面的文件
            if(fileManageInf == null) return;
            byte[] data = dfsClient.download(fileManageInf.getPath());
            data = EncryptionAndDeciphering.decipherFile(data);
            //获取文件后缀名格式
            String ext = ((fileManageInf.getFileExt()==null)?"":fileManageInf.getFileExt());
            //判断图片格式,设置相应的输出文件格式
            if(ext.equals("jpg")){
                response.setContentType("image/jpeg");
            }else if(ext.equals("JPG")){
                response.setContentType("image/jpeg");
            }else if(ext.equals("png")){
                response.setContentType("image/png");
            }else if(ext.equals("PNG")){
                response.setContentType("image/png");
            }
            outputStream = new BufferedOutputStream(response.getOutputStream());
            outputStream.write(data);
            outputStream.flush();
        }catch (Exception e){
            log.error(CommonUtil.getExceptionMessage(e));
        }finally {
            //关流
            if(outputStream!=null){
                outputStream.close();
            }
        }

    }
    @GetMapping("/getFileSizeByDB")
    //查询附件大小（数据库统计）
    //queryType 查询类型 0 天(默认)，1 月，2 年
    //returnUnit 单位 0 M(默认)，1 G，2 T
    //queryDate 查询日期 天 2019-01-01 ，月 2019-01，年 2019
    public String getFileSizeByDB(@RequestParam("queryType") String queryType,@RequestParam("queryDate") String queryDate,@RequestParam("unit") String returnUnit) {
        String res = "";//-1接口报错
        try {
            res = fileMangeService.getFileSizeByDB((queryType==null||queryType.equals(""))?"0":queryType,queryDate,(returnUnit==null||returnUnit.equals(""))?"0":returnUnit);
        }catch (Exception e){
            e.printStackTrace();
            res = "-1";
        }

        return res;
    }
    /*public String getFileSizeByDB(@RequestParam("queryType") String queryType,@RequestParam("queryDate") String queryDate) {
        String res = "";//-1接口报错
        try {
            res = fileMangeService.getFileSizeByDB((queryType==null||queryType.equals(""))?"0":queryType,queryDate,"0");
        }catch (Exception e){
            e.printStackTrace();
            res = "-1";
        }

        return res;
    }
    public String getFileSizeByDB(@RequestParam("queryDate") String queryDate) {
        String res = "";//-1接口报错
        try {
            res = fileMangeService.getFileSizeByDB("0",queryDate,"0");
        }catch (Exception e){
            e.printStackTrace();
            res = "-1";
        }

        return res;
    }*/
    @GetMapping("/getFileSizeRangeByDB")
    //查询区间附件大小（数据库统计）
    //queryType 查询类型 0 天(默认)，1 月，2 年
    //returnUnit 单位 0 M(默认)，1 G，2 T
    //queryDateBegin 查询日期开始 天 2019-01-01 ，月 2019-01，年 2019
    //queryDateEnd 查询日期结束 天 2019-01-02 ，月 2019-02，年 2020
    public List<Map<String,String>> getFileSizeRangeByDB(@RequestParam("queryType") String queryType, @RequestParam("queryDateBegin") String queryDateBegin, @RequestParam("queryDateEnd") String queryDateEnd, @RequestParam("unit") String returnUnit) {
        List<Map<String,String>> res=null;
        try {
            res = fileMangeService.getFileSizeRangeByDB((queryType==null||queryType.equals(""))?"0":queryType,queryDateBegin,queryDateEnd,(returnUnit==null||returnUnit.equals(""))?"0":returnUnit);
        }catch (Exception e){
            e.printStackTrace();
        }

        return res;
    }
   /* public List<Map<String,String>> getFileSizeRangeByDB(@RequestParam("queryType") String queryType, @RequestParam("queryDateBegin") String queryDateBegin, @RequestParam("queryDateEnd") String queryDateEnd) {
        List<Map<String,String>> res=null;
        try {
            res = fileMangeService.getFileSizeRangeByDB((queryType==null||queryType.equals(""))?"0":queryType,queryDateBegin,queryDateEnd,"0");
        }catch (Exception e){
            e.printStackTrace();
        }

        return res;
    }

    public List<Map<String,String>> getFileSizeRangeByDB(@RequestParam("queryDateBegin") String queryDateBegin, @RequestParam("queryDateEnd") String queryDateEnd) {
        List<Map<String,String>> res=null;
        try {
            res = fileMangeService.getFileSizeRangeByDB("0",queryDateBegin,queryDateEnd,"0");
        }catch (Exception e){
            e.printStackTrace();
        }

        return res;
    }
*/

    /**
     * 上传文件监控
     *参数说明：page 页码 size 每页几条 userName上传用户名称 dateBegin、dateEnd上传时间开始结束 isGroup 是否群主1是0否
     * fileName文件名称 level密级
     * @return
     */
    @PostMapping("/fileMonitoring")
    public TableResultResponse<FileMonitoringVO> fileMonitoring(@RequestParam Map<String,Object> params){
        TableResultResponse<FileMonitoringVO> pageInfo = null;
        try {
            pageInfo = this.fileMangeService.fileMonitoring(params);
        } catch (Exception e) {
            log.error(CommonUtil.getExceptionMessage(e));
        }
        return pageInfo;
    }
}
