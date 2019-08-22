package com.github.hollykunge.security.service;

/**
 * @description: 文件块接口
 * @author: dd
 * @since: 2019-08-01
 */
public interface ChunkService {
    /**
     * 根据文件唯一标识和当前块数判断是否已经上传过这个块
     * @param identifier
     * @param chunkNumber
     * @return
     */
    public boolean checkChunk(String identifier, Integer chunkNumber);
}
