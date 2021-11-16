package com.ni.parnasa.pojos;

import com.google.gson.annotations.SerializedName;

public class UploadImagePojoItem {

    @SerializedName("file_url")
    private String fileUrl;

    @SerializedName("file_original_name")
    private String fileOriginalName;

    @SerializedName("file_name")
    private String fileName;

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileOriginalName(String fileOriginalName) {
        this.fileOriginalName = fileOriginalName;
    }

    public String getFileOriginalName() {
        return fileOriginalName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public String toString() {
        return
                "UploadImagePojoItem{" +
                        "file_url = '" + fileUrl + '\'' +
                        ",file_original_name = '" + fileOriginalName + '\'' +
                        ",file_name = '" + fileName + '\'' +
                        "}";
    }
}