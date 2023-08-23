package com.one.browser.service;

import com.one.browser.entity.DownloadMsg;

/**
 * @author 18517
 */
public interface DownloadProgressListener {
    /**
     * 返回文件数据
     * @param  downloadMsg 文件返回实体类
     */
    void onDownload(DownloadMsg downloadMsg);
}
