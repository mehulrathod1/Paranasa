package com.ni.parnasa.utils;

import android.os.Environment;

import java.io.File;

public class BaseUrl {
    /*old*/

    /**
     * For the Production */


    /**
     * For Ncrypted production server staging
     */
    public static String URL = "https://parnasa.website/api/";

    /**
     * Socket URL
     */
//    public static String SOCKET_URL = "ws://192.168.100.56:4000";
//    public static String SOCKET_URL = "ws://pickmeapp.ncryptedprojects.com:4000";
    public static String SOCKET_URL = "ws://pickmeapp.co:4000";

    public static String authorised_key = "UGlja21lQEFVVEgyMDE4";
    public static String deviceAuthToken = "device_auth_token";
    public static String deviceType = "android";

    /*public static String ImageUpload = URL + "upload_file";
    public static String Upload_file_profileImage = URL + "Upload_file/profileImage";*/

    public static String urlLogin = URL + "User/login";
    public static String signupStepTwo = URL + "user/signup_step2";
    public static String signupStepFoure = URL + "user/signup_step4";
    public static String signupStepFive = URL + "user/signup_step5";
    public static String forgotPassword = URL + "User/forgot_password";
    public static String socialLoginGoogle = URL + "User/socialLogin/google";
    public static String socialLoginFacebook = URL + "User/socialLogin/facebook";
    public static String urlAllServices = URL + "services/servicesKeyword";
    public static String changePassword = URL + "user/changePassword";
    public static String getSubscriptionFeeDetail = URL + "user/getSubscriptionFeeDetail";
    public static String getUserByEmail = URL + "user/getUserByEmail";
    public static String getUserById = URL + "user/get_user";
    public static String favoriteUserList = URL + "User/myFavoriteUsers";
    public static String favoriteUser = URL + "User/favoriteUser";
    public static String unfavoriteUser = URL + "User/unfavoriteUser";

    public static String createChatRoom = URL + "Chats/getChatRoomDetails";
    public static String myChatRoom = URL + "Chats/myChatRooms";
    public static String getChatMessages = URL + "Chats/getChatMessages";
    public static String sendMessage = URL + "Chats/sendMessage";
    public static String getAllCategory = URL + "services/get_all";
    public static String getFilterProfessional = URL + "services/filter_data";
    public static String createJob = URL + "Jobs/createJob";
    public static String editProfile = URL + "user/editProfile";
    public static String customerJobList = URL + "All_jobs/customer_job_list";
    public static String soleJobList = URL + "All_jobs/sole_job_list";
    public static String getJobDetail = URL + "Jobs/getJobs";
    public static String jobStatusAccept = URL + "Jobs/jobStatusUpdate/confirmJob";
    public static String jobStatusReject = URL + "Jobs/jobStatusUpdate/reject";
    public static String jobStatusStart = URL + "Jobs/jobStatusUpdate/startJob";
    public static String jobStatusEnd = URL + "Jobs/jobStatusUpdate/endJob";
    public static String profStatusOccupy = URL + "Jobs/professionalStatusUpdate/occupy";
    public static String profStatusFree = URL + "Jobs/professionalStatusUpdate/free";
    public static String getProfessionalRate = URL + "Job_rates/get_rate";
    public static String profStatusOnTheWay = URL + "Jobs/professionalStatusUpdate/on_the_way";
    public static String getJobReviewAndRating = URL + "Jobs/getjobRatingAndReview";
    public static String addJobReviewAndRating = URL + "Jobs/jobRatingAndReview";
    public static String generateInvoice = URL + "Invoice/generateInvoice";
    public static String getInvoice = URL + "Invoice/getInvoice";
    public static String resendVerificationCode = URL + "User/resendVerificationCode";
    public static String payInvoice = URL + "Invoice/payInvoice";
    public static String cashPaymentConfirmation = URL + "Invoice/cashPaymentConfirmation";
    public static String logoutFromApp = URL + "user/logout";
    public static String subscriptionPayments = URL + "subscriptionPayments/paypal";
    public static String subscriptionPaymentsFromStripe = URL + "user/subcriptionPaymentURL";

    public static String trackingUpdateLatLng = URL + "Tracking/storelatlong";
    public static String serachByServiceOrLocation = URL + "user/serviceMapList";
    public static String updateProfessionalLatLng = URL + "User/updateCurrentLocation";
    public static String jobLocationArrived = URL + "jobs/jobLocationArrived";
    public static String getCurrentLocation = URL + "User/getCurrentLocation";
    public static String getProfessionalDetail = URL + "user/getProfessionalDetails";
    public static String getRegisterFeeDetail = URL + "user/getRegisterFeeDetail";
    public static String registerPayments = URL + "user/registerPayments";
    public static String getUserReviewRating = URL + "user/getReviewRatings";
    public static String getCustomerOngoingJob = URL + "All_jobs/ongoing_job";

    /* Constants declaration */
    public static String defaultMale = "http://pickmeapp.co/dev_v2/admin_assets/images/users/male-icon.jpeg";
    public static String defaultFemale = "http://pickmeapp.co/dev_v2/admin_assets/images/users/female-icon.jpeg";
    public static String dirPath = Environment.getExternalStorageDirectory() + File.separator + "Parnasa";
    public static String isCompletedSignup = "isCompletedSignup";
    public static String chatRoomId = "", jobIdTmp = "", jobIdForTracking = "";
    public static boolean isChatActivityOpen = false;
    public static boolean isJobDetailOpen = false;
    public static boolean isCustomerMapOpen = false;
    public static boolean isTrackingOpen = false;
    public static boolean isProfessionalHomeOpen = false;

    public static String dummyCity = "DummyCity";
    public static String dummyCountry = "DummyCountry";
    public static String dummyLat = "22.57865284122";
    public static String dummyLng = "70.87436665658";

    /*
    public enum ROLE {
        Customer,
        Sole Professional
        Agency
    }
    */

    /*FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
        @Override
        public void onComplete(@NonNull Task< InstanceIdResult > task) {
            fcmToken = task.getResult().getToken();
        }
    });*/
}
