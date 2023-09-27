package com.clobotics.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UploadResp {

    @JsonProperty("file_url")
    private String fileUrl;

    private String md5;

    @JsonProperty("file_size")
    private Long fileSize;

    @JsonProperty("watermark_info")
    private WatermarkInfo watermarkInfo;

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public WatermarkInfo getWatermarkInfo() {
        return watermarkInfo;
    }

    public void setWatermarkInfo(WatermarkInfo watermarkInfo) {
        this.watermarkInfo = watermarkInfo;
    }

    public static class WatermarkInfo {

        @JsonProperty("file_url")
        private String fileUrl;

        private String md5;

        @JsonProperty("file_size")
        private Long fileSize;

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }

        public Long getFileSize() {
            return fileSize;
        }

        public void setFileSize(Long fileSize) {
            this.fileSize = fileSize;
        }
    }
}
