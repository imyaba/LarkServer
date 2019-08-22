package com.github.hollykunge.security.biz;

import com.ace.cache.annotation.Cache;
import com.ace.cache.api.impl.CacheRedis;
import com.alibaba.fastjson.JSON;
import com.github.hollykunge.security.common.biz.BaseBiz;
import com.github.hollykunge.security.common.exception.BaseException;
import com.github.hollykunge.security.common.util.EntityUtils;
import com.github.hollykunge.security.common.util.UUIDUtils;
import com.github.hollykunge.security.common.vo.FileInfoVO;
import com.github.hollykunge.security.comtants.FileComtants;
import com.github.hollykunge.security.entity.FileInfoEntity;
import com.github.hollykunge.security.entity.FileServerPathEntity;
import com.github.hollykunge.security.mapper.FileInfoMapper;
import com.github.hollykunge.security.mapper.FileServerPathMapper;
import com.github.hollykunge.security.util.*;
import com.github.hollykunge.security.vo.JwtInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件基础数据业务处理层
 *
 * @author zhhongyu
 * @since 2019-07-29
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class FileInfoBiz extends BaseBiz<FileInfoMapper, FileInfoEntity> {
    @Autowired
    private FastDFSClientWrapper dfsClient;
    @Autowired
    private FileServerPathBiz fileServerPathBiz;
    @Autowired
    private AppendFileUtils appendFileUtils;
    @Autowired
    private CacheRedis cacheRedis;

    @Autowired
    private FileServerPathMapper fileServerPathMapper;


    @Override
    protected String getPageName() {
        return null;
    }

    /**
     * 上传非加密文件业务处理
     *
     * @param file
     * @return
     * @throws Exception
     */
    public FileInfoVO uploadFile(MultipartFile file) throws Exception {
        String md5Key = MD5Util.MD5(file.getBytes());
        String fileServerPathId = "";
        //先从缓存中获取文件
        fileServerPathId = ((FileInfoBiz) AopContext.currentProxy()).uploadFileCache(md5Key, null);
        FileInfoEntity fileInforEntity = new FileInfoEntity();
        FileServerPathEntity fileServerPathEntity = new FileServerPathEntity();
        //如果缓存中没有该文件
        if (StringUtils.isEmpty(fileServerPathId)) {
            String path = dfsClient.uploadFile(file);
            fileServerPathEntity.setPath(path);
            fileServerPathEntity.setFileEncrype(FileComtants.NO_SENSITIVE_TYPE);
            this.fileToEntity(file, fileInforEntity, fileServerPathEntity);
            this.insertEntityAce(fileInforEntity, fileServerPathEntity, md5Key);
        }
        //如果缓存中有该文件，则进行插入数据库表到文件基本信息表，然后直接返回成功上传到服务器
        // 从而实现秒传效果
        if (!StringUtils.isEmpty(fileServerPathId)) {
            this.fileToEntity(file, fileInforEntity, null);
            fileInforEntity.setFilePathId(fileServerPathId);
            mapper.insertSelective(fileInforEntity);
        }
        FileInfoVO fileInforVO = this.transferEntityToVo(fileInforEntity);
        return fileInforVO;
    }

    /**
     * 上传加密文件
     *
     * @param file          文件
     * @param sensitiveType 加密类型（1为base64加密，2为位移加密法，3为文件流加密）
     * @return
     * @throws Exception
     */
    public FileInfoVO uploadSensitiveFile(MultipartFile file, String sensitiveType) throws Exception {
        String md5Key = MD5Util.MD5(file.getBytes());
        String fileServerPathId = "";
        //先从缓存中获取文件
        fileServerPathId = ((FileInfoBiz) AopContext.currentProxy()).uploadFileCache(md5Key, null);
        FileInfoEntity fileInforEntity = new FileInfoEntity();
        FileServerPathEntity fileServerPathEntity = new FileServerPathEntity();
        //如果缓存中没有该文件
        if (StringUtils.isEmpty(fileServerPathId)) {
            String path = "";
            //采用文件流加密
            if (FileComtants.SENSITIVE_BASE64_TYPE.equals(sensitiveType)) {
                path = dfsClient.uploadbase64SensitiveFile(file);
                fileServerPathEntity.setFileEncrype(FileComtants.SENSITIVE_BASE64_TYPE);
            }
            //采用文件流加密
            if (FileComtants.SENSITIVE_CIPHER_TYPE.equals(sensitiveType)) {
                path = dfsClient.uploadCipherSensitiveFile(file);
                fileServerPathEntity.setFileEncrype(FileComtants.SENSITIVE_CIPHER_TYPE);
            }
            //采用位移加密
            if (FileComtants.SENSITIVE_BYTEMOVE_TYPE.equals(sensitiveType)) {
                path = dfsClient.uploadByteMoveSensitiveFile(file);
                fileServerPathEntity.setFileEncrype(FileComtants.SENSITIVE_BYTEMOVE_TYPE);
            }
            fileServerPathEntity.setPath(path);
            this.fileToEntity(file, fileInforEntity, fileServerPathEntity);
            this.insertEntityAce(fileInforEntity, fileServerPathEntity, md5Key);
        }
        //如果缓存中有该文件，则进行插入数据库表到文件基本信息表，然后直接返回成功上传到服务器
        // 从而实现秒传效果
        if (!StringUtils.isEmpty(fileServerPathId)) {
            this.fileToEntity(file, fileInforEntity, null);
            fileInforEntity.setFilePathId(fileServerPathId);
            mapper.insertSelective(fileInforEntity);
        }
        FileInfoVO fileInforVO = this.transferEntityToVo(fileInforEntity);
        return fileInforVO;
    }

    /**
     * 上传加密文件(文件分块上传)
     * @param file 文件
     * @param md5key 整个文件的唯一性标识
     * @param currentNo 当前块
     * @param totalSize 总块数
     * @param fileInfo 文件基本信息
     * @param jwtInfoVO jwttoken解析用户结果实体类
     * @return 最后一块时，返回文件上传基本信息值
     * @throws Exception
     */
    public FileInfoVO uploadAppendSensitiveFile(MultipartFile file,String md5key,
                                                String currentNo,String totalSize,
                                                FileInfoEntity fileInfo,
                                                JwtInfoVO jwtInfoVO) throws Exception {
        if(Integer.parseInt(currentNo)>Integer.parseInt(totalSize)){
            throw new BaseException("当前块数不能大于总块数...");
        }
        //先从redies中获取相同文件,进行秒传实现
        if(!StringUtils.isEmpty(md5key)){
            String fileServerPathId = cacheRedis.get("files:"+md5key);
            //秒传实现如果有id，则进行保存数据操作，直接响应给客户端
            if (!StringUtils.isEmpty(fileServerPathId)) {
                fileInfo.setFilePathId(fileServerPathId);
                mapper.insertSelective(fileInfo);
                FileInfoVO fileInforVO = this.transferEntityToVo(fileInfo);
                return fileInforVO;
            }
        }
        FileInfoVO fileInforVO = null;
        Map<String,Object> fileCard = appendFileUtils.uploadCipherSensitiveFile(file,md5key,currentNo,totalSize);
        String path = (String) fileCard.get("path");
        String isSuccessNo = (String) fileCard.get("isSuccessNo");
        //最后一块时进行数据库保存操作
        if (currentNo.equals(totalSize)) {
            FileServerPathEntity fileServerPathEntity = this.generFileServerPathEntity(jwtInfoVO);
            fileServerPathEntity.setPath(path);
            fileServerPathEntity.setFileEncrype(FileComtants.SENSITIVE_CIPHER_TYPE);
            try {
                fileServerPathMapper.insertSelective(fileServerPathEntity);
                fileInfo.setFilePathId(fileServerPathEntity.getId());
                mapper.insertSelective(fileInfo);
                //缓存整个文件唯一性编码到redies，做秒传功能
                if(!StringUtils.isEmpty(md5key)){
                    cacheRedis.remove(FileComtants.REDIS_KEY_APPEND_FILE+md5key);
                    cacheRedis.remove(FileComtants.REDIS_KEY_PRE+md5key);
                    cacheRedis.set("files:"+md5key,fileServerPathEntity.getId(),525600);
                }
                fileInforVO = this.transferEntityToVo(fileInfo);
                //将最后一次的参数也返回给调用方,下一块为总块数,成功块数为总块数
                fileInforVO.setNextNo(totalSize);
                fileInforVO.setTotalSize(totalSize);
                fileInforVO.setIsSuccessNo(totalSize);
                return fileInforVO;
            } catch (Exception e) {
                //如果数据存入数据库失败，回滚fast服务的文件
                log.error(CommonUtil.getExceptionMessage(e));
                dfsClient.deleteFile(path);
                throw e;
            }
        }
        fileInforVO = new FileInfoVO();
        fileInforVO.setFullPath(path);
        fileInforVO.setIsSuccessNo(isSuccessNo);
        //设置要传的下一块
        fileInforVO.setNextNo(String.valueOf(Integer.parseInt(isSuccessNo)+1));
        fileInforVO.setTotalSize(totalSize);
        return fileInforVO;
    }

    /**
     * 删除文件业务处理
     *
     * @param fileId 文件id
     */
    public void deleteFile(String fileId) {
        if (StringUtils.isEmpty(fileId)) {
            throw new BaseException("fileId is null...");
        }
        FileInfoEntity fileInfoEntity = new FileInfoEntity();
        fileInfoEntity.setId(fileId);
        fileInfoEntity = mapper.selectByPrimaryKey(fileInfoEntity);
        if (fileInfoEntity == null) {
            throw new BaseException("没有改文件...");
        }
        //保留文件历史，只是将数据库中文件信息设置为无效状态
        fileInfoEntity.setStatus(FileComtants.INVALID_FILE);
        mapper.updateByPrimaryKeySelective(fileInfoEntity);
    }

    /**
     * 文件下载业务处理
     *
     * @param fileId
     * @param sensitiveType
     * @return
     */
    public Map<String, Object> downLoadFile(String fileId, String sensitiveType) throws IOException {
        if (StringUtils.isEmpty(fileId)) {
            throw new BaseException("fileId is null ... ");
        }
        FileInfoEntity fileInfoEntity = new FileInfoEntity();
        fileInfoEntity.setId(fileId);
        fileInfoEntity = mapper.selectByPrimaryKey(fileInfoEntity);
        if (fileInfoEntity == null) {
            throw new BaseException("没有该文件...");
        }
        if (StringUtils.isEmpty(fileInfoEntity.getFilePathId())) {
            throw new BaseException("该文件没有对应的服务器路径...");
        }
        FileServerPathEntity fileServerPathEntity = fileServerPathBiz.selectById(fileInfoEntity.getFilePathId());
        if (fileServerPathEntity == null || StringUtils.isEmpty(fileServerPathEntity.getPath())) {
            throw new BaseException("文件没有存在在服务器中...");
        }
        String path = fileServerPathEntity.getPath();
        //文件名称
        String fileName = fileInfoEntity.getFileName();
        //文件后缀
        String fileExt = fileInfoEntity.getFileExt();
        if (StringUtils.isEmpty(fileName)) {
            fileName = FileComtants.DOWNLOAD_FILE_NAME;
        }
        if (StringUtils.isEmpty(fileExt)) {
            fileExt = FileComtants.DOWNLOAD_FILE_EXT;
        }
        fileName = fileName + FileComtants.FILE_REGIX_VALUE + fileExt;
        byte[] fileIO = null;
        //非涉密文件下载
        if (FileComtants.NO_SENSITIVE_TYPE.equals(sensitiveType)) {
            fileIO = dfsClient.download(path);
        }
        if (FileComtants.SENSITIVE_BASE64_TYPE.equals(sensitiveType)) {
            fileIO = dfsClient.downloadBase64SensitiveFile(path);
        }
        if (FileComtants.SENSITIVE_CIPHER_TYPE.equals(sensitiveType)) {
            fileIO = dfsClient.downloadCipherSensitiveFile(path);
        }
        if (FileComtants.SENSITIVE_BYTEMOVE_TYPE.equals(sensitiveType)) {
            fileIO = dfsClient.downloadByteMoveSensitiveFile(path);
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("fileName", fileName);
        result.put("fileByte", fileIO);
        return result;
    }

    /**
     * 获取图片，并响应给客户端
     *
     * @param fileId
     * @param response
     * @param sensitiveType
     * @throws IOException
     */
    public void getImg(String fileId, HttpServletResponse response, String sensitiveType) throws IOException {
        if (StringUtils.isEmpty(fileId)) {
            throw new BaseException("fileId is null ... ");
        }
        OutputStream outputStream = null;
        try {
            //读取路径下面的文件
            FileInfoEntity fileInfoEntity = new FileInfoEntity();
            fileInfoEntity.setId(fileId);
            fileInfoEntity = mapper.selectByPrimaryKey(fileInfoEntity);
            if (fileInfoEntity == null) {
                throw new BaseException("查询不到该文件 ... ");
            }
            if (StringUtils.isEmpty(fileInfoEntity.getFilePathId())) {
                throw new BaseException("该文件没有存储文件路径 ... ");
            }
            FileServerPathEntity fileServerPathEntity = fileServerPathBiz.selectById(fileInfoEntity.getFilePathId());
            if (fileServerPathEntity == null || StringUtils.isEmpty(fileServerPathEntity.getPath())) {
                throw new BaseException("文件没有在文件服务中 ... ");
            }
            String path = fileServerPathEntity.getPath();
            byte[] data = null;
            if (FileComtants.NO_SENSITIVE_TYPE.equals(sensitiveType)) {
                data = dfsClient.download(path);
            }
            if (FileComtants.SENSITIVE_BASE64_TYPE.equals(sensitiveType)) {
                data = dfsClient.downloadBase64SensitiveFile(path);
            }
            if (FileComtants.SENSITIVE_BYTEMOVE_TYPE.equals(sensitiveType)) {
                data = dfsClient.downloadByteMoveSensitiveFile(path);
            }
            if (FileComtants.SENSITIVE_CIPHER_TYPE.equals(sensitiveType)) {
                data = dfsClient.downloadCipherSensitiveFile(path);
            }
            //获取文件后缀名格式
            String ext = ((fileInfoEntity.getFileExt() == null) ? "" : fileInfoEntity.getFileExt());
            //判断图片格式,设置相应的输出文件格式
            if ("jpg".equals(ext) || "JPG".equals(ext)) {
                response.setContentType("image/jpeg");
            }
            if ("png".equals(ext) || "PNG".equals(ext)) {
                response.setContentType("image/png");
            }
            outputStream = new BufferedOutputStream(response.getOutputStream());
            outputStream.write(data);
            outputStream.flush();
        } catch (Exception e) {
            log.error(CommonUtil.getExceptionMessage(e));
        } finally {
            //关流
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    private String insertEntityAce(FileInfoEntity fileInforEntity, FileServerPathEntity fileServerPathEntity,
                                   String md5Key) throws Exception {
        //首先插入文件服务路径表中记录
        fileServerPathBiz.insertSelective(fileServerPathEntity);
        fileInforEntity.setFilePathId(fileServerPathEntity.getId());
        //todo:欠一个redis和数据库事务一致性,这个方法可能不需要
        mapper.insertSelective(fileInforEntity);
        //插入成功将唯一性的md5编码key缓存到缓存中
        String fileServerPathId = null;
        try {
            fileServerPathId = ((FileInfoBiz) AopContext.currentProxy()).uploadFileCache(md5Key, JSON.toJSONString(fileServerPathEntity.getId()));
        } catch (Exception e) {
            log.error(CommonUtil.getExceptionMessage(e));
            dfsClient.deleteFile(fileServerPathEntity.getPath());
            throw e;
        }
        return fileServerPathId;
    }

    /**
     * 将file文件转为数据库实体（path字段需要单独赋值）
     *
     * @param file                 文件
     * @param fileInforEntity      文件基本信息实体类
     * @param fileServerPathEntity 上传文件到服务器路径实体类
     * @return
     * @throws Exception
     */
    private void fileToEntity(MultipartFile file, FileInfoEntity fileInforEntity,
                              FileServerPathEntity fileServerPathEntity) throws Exception {
        if (file == null) {
            throw new BaseException("上传文件不能为空...");
        }
        if (fileInforEntity != null) {
            String fileName = file.getOriginalFilename();
            String suffix = "";
            String fileExt = "";
            String regixValue = FileComtants.FILE_REGIX_VALUE;
            if (fileName.indexOf(regixValue) != -1) {
                suffix = fileName.substring(fileName.lastIndexOf(regixValue));
                fileName = fileName.substring(0, fileName.lastIndexOf(regixValue));
            }
            if (!"".equals(suffix) && !regixValue.equals(suffix)) {
                fileExt = suffix.substring(suffix.indexOf(regixValue) + 1);
            }
            fileInforEntity.setFileExt(fileExt);
            fileInforEntity.setFileName(fileName);
            String fileType = "";
            FileTypeEnum fileTypeEnum = FileTypeEnum.getEnumByValue(fileExt);
            fileType = fileTypeEnum.getType();
            fileInforEntity.setFileType(fileType.toLowerCase());
            fileInforEntity.setFileSize(Double.valueOf(file.getSize()));
            fileInforEntity.setStatus(FileComtants.EFECTIVE_FILE);
            EntityUtils.setCreatAndUpdatInfo(fileInforEntity);
        }
        if (fileServerPathEntity != null) {
            fileServerPathEntity.setStatus(FileComtants.EFECTIVE_FILE);
            EntityUtils.setCreatAndUpdatInfo(fileServerPathEntity);
        }
        return;
    }

    /**
     * 生成FileServerPathEntity私有方法
     * @param jwtInfoVO
     * @return
     */
    private FileServerPathEntity generFileServerPathEntity(JwtInfoVO jwtInfoVO){
        FileServerPathEntity fileServerPathEntity = new FileServerPathEntity();
        if(jwtInfoVO != null){
            BeanUtils.copyProperties(jwtInfoVO,fileServerPathEntity);
        }
        fileServerPathEntity.setId(UUIDUtils.generateShortUuid());
        fileServerPathEntity.setStatus(FileComtants.EFECTIVE_FILE);
        return fileServerPathEntity;
    }

    private FileInfoVO transferEntityToVo(FileInfoEntity fileInfoEntity) {
        FileInfoVO fileInfoVO = new FileInfoVO();
        BeanUtils.copyProperties(fileInfoEntity, fileInfoVO);
        fileInfoVO.setFileId(fileInfoEntity.getId());
        return fileInfoVO;
    }

    /**
     * 文件存放redies中，时间为一年
     *
     * @param base64FileName
     * @param fileId
     * @return
     * @throws Exception
     */
    @Cache(key = "files{1}", result = String.class, expire = 525600)
    public String uploadFileCache(String base64FileName, String fileId) throws Exception {
        return fileId;
    }


    public static void main(String[] args) {
        System.out.println(JSON.toJSONString("654sdf"));
        String s = JSON.parseObject("\"654sdf\"", String.class);
        System.out.println(s);
    }
}
