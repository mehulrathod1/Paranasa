package com.ni.parnasa.fileupload;



import com.ni.parnasa.pojos.UploadImagePojo;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by krish on 04/10/16.
 */
public interface FileUploadService {

    @Multipart
    @POST("Upload_file/profileImage")
    Call<UploadImagePojo> upload(@Part("description") RequestBody description,
                                 @Part MultipartBody.Part file);
}
