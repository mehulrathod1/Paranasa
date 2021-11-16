/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ni.parnasa.utils;

import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import com.ni.parnasa.R;
import com.ni.parnasa.activities.JobDetailActivity;
import com.ni.parnasa.activities.PrivateMessagesActivity;
import com.ni.parnasa.activities.customer.MapForCustomerActivity;
import com.ni.parnasa.activities.professional.MapForProfessionalActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
//import com.service.pickme.R;
//import com.service.pickme.activities.JobDetailActivity;
//import com.service.pickme.activities.PrivateMessagesActivity;
//import com.service.pickme.activities.customer.MapForCustomerActivity;
//import com.service.pickme.activities.professional.MapForProfessionalActivity;

import org.json.JSONObject;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.e("ZZZ", "firebase notification arrived------>");
        MyLogs.e("ZZZ", "FCM DATA :: " + remoteMessage.getData());
//        MyLogs.e(TAG, "Notification Message Body: " + remoteMessage.getData().get("body"));

        Map<String, String> map = remoteMessage.getData();
        try {
            String action = map.get("notify_action");
            JSONObject data = new JSONObject(map.get("notify_data"));

            MyLogs.e("ZZZ", "notify_data : " + data);
            MyLogs.e("ZZZ", "notify_action : " + action);

            /** When new job create from customer side then push notification come to professional with this data
             *  notify_new_job_posted  => {notify_data={"job_id":62}, notify_action=notify_new_job_posted}
             *  notify_job_accepted    => {notify_data={"job_id":"68"}, notify_action=notify_job_accepted}
             *  prof_on_way            => {notify_data={"job_id":"59"}, notify_action=notify_prof_on_the_way, body=professional is on the way}
             *  notify_job_rejected    => {notify_data={"job_id":"68"}, notify_action=notify_job_rejected}
             *  notify_invoice_created => {notify_data={"job_id":"68"}, notify_action=notify_invoice_created}
             *  notify_paying_by_cash  => {notify_data={"job_id":"68"}, notify_action=notify_paying_by_cash}
             *  notify_message         => {notify_data={"image":"","user_id":"8","last_msg_id":"68","content":"test no 5","chat_id":"3","timestamp":"1566293008"}, notify_action=notify_msg}
             *  */

            String titleOfNotification = getString(R.string.app_name);

            if (action.equalsIgnoreCase("notify_new_job_posted")) { // arrived of professional

                String jobId = data.getString("job_id");
                String msgBody = "New job posted for you";

                if (BaseUrl.isProfessionalHomeOpen) {
                    Intent intent = new Intent("newJobPostedNotify");
                    intent.putExtra("jobId", jobId);
                    sendBroadcast(intent);
                } else {
                    Intent intent = new Intent(this, MapForProfessionalActivity.class);
                    intent.putExtra("jobId", jobId);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                    sendNotification(titleOfNotification, msgBody, pendingIntent);
                }
            } else if (action.equalsIgnoreCase("notify_job_accepted")) {    // arrived of customer

                String jobId = data.getString("job_id");
                String msgBody = "Your job has been accepted";

                if (BaseUrl.isCustomerMapOpen) {
                    if (BaseUrl.jobIdTmp.equals(jobId)) {
                        Intent intent = new Intent("refreshCustomerMap");
                        sendBroadcast(intent);
                    } else {
                        Intent intent = new Intent(this, MapForCustomerActivity.class);
                        intent.putExtra("jobId", jobId);
                        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                        sendNotification(titleOfNotification, msgBody, pendingIntent);
                    }
                } else {
                    Intent intent = new Intent(this, MapForCustomerActivity.class);
                    intent.putExtra("jobId", jobId);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                    sendNotification(titleOfNotification, msgBody, pendingIntent);
                }
            } else if (action.equalsIgnoreCase("notify_job_rejected")) {    // arrived of customer

                String jobId = data.getString("job_id");
                String msgBody = "Your job has been rejected";

                if (BaseUrl.isJobDetailOpen) {
                    if (BaseUrl.jobIdTmp.equals(jobId)) {
                        Intent intent = new Intent("refreshJob");
                        sendBroadcast(intent);
                    } else {
                        Intent intent = new Intent(this, JobDetailActivity.class);
                        intent.putExtra("jobId", jobId);
                        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                        sendNotification(titleOfNotification, msgBody, pendingIntent);
                    }
                } else {
                    if (BaseUrl.isCustomerMapOpen) {
                        if (BaseUrl.jobIdTmp.equals(jobId)) {
                            Intent intent = new Intent("refreshCustomerMap");
                            intent.putExtra("isCompleteJobOrReject", true);
                            intent.putExtra("needToStopTime", true);
                            sendBroadcast(intent);
                        } else {
                            Intent intent = new Intent(this, MapForCustomerActivity.class);
                            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                            intent.putExtra("jobId", jobId);
                            sendNotification(titleOfNotification, msgBody, pendingIntent);
                        }
                    } else {
                        Intent intent = new Intent(this, MapForCustomerActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                        intent.putExtra("jobId", jobId);
                        sendNotification(titleOfNotification, msgBody, pendingIntent);
                    }
                }
            } else if (action.equalsIgnoreCase("notify_prof_on_the_way")) {     // arrived of customer

                String jobId = data.getString("job_id");
                String msgBody = "Professional is on the way for your job";

                if (BaseUrl.isJobDetailOpen) {
                    if (BaseUrl.jobIdTmp.equals(jobId)) {
                        Intent intent = new Intent("refreshJob");
                        sendBroadcast(intent);
                    } else {
                        Intent intent = new Intent(this, MapForCustomerActivity.class);
                        intent.putExtra("jobId", jobId);
                        intent.putExtra("isOpenFromFcmService", true);
                        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                        sendNotification(titleOfNotification, msgBody, pendingIntent);
                    }
                } else {
                    if (BaseUrl.isCustomerMapOpen) {
                        if (BaseUrl.jobIdTmp.equals(jobId)) {
                            Intent intent = new Intent("refreshCustomerMap");
                            intent.putExtra("isCompleteJobOrReject", false);
                            intent.putExtra("needToStopTime", true);
                            sendBroadcast(intent);
                        } else {
                            Intent intent = new Intent(this, MapForCustomerActivity.class);
                            intent.putExtra("jobId", jobId);
                            intent.putExtra("isOpenFromFcmService", true);
                            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                            sendNotification(titleOfNotification, msgBody, pendingIntent);
                        }
                    } else {
                        Intent intent = new Intent(this, MapForCustomerActivity.class);
                        intent.putExtra("jobId", jobId);
                        intent.putExtra("isOpenFromFcmService", true);
                        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                        sendNotification(titleOfNotification, msgBody, pendingIntent);
                    }
                }

            } else if (action.equalsIgnoreCase("notify_prof_arrived")) {    // arrived of customer

                String jobId = data.getString("job_id").trim();
                String msgBody = "Professional is arrived job location";

                if (BaseUrl.isJobDetailOpen) {
                    if (BaseUrl.jobIdTmp.equals(jobId)) {
                        Intent intent = new Intent("refreshJob");
                        sendBroadcast(intent);
                    } else {
                        Intent intent = new Intent(this, MapForCustomerActivity.class);
                        intent.putExtra("jobId", jobId);
                        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                        sendNotification(titleOfNotification, msgBody, pendingIntent);
                    }
                } else {
                    if (BaseUrl.isTrackingOpen) {
                        Log.e("ZZZ", "Tracking open with " + BaseUrl.jobIdForTracking + " = " + jobId);
                        if (BaseUrl.jobIdForTracking.equals(jobId)) {
                            Intent intent = new Intent("notifyTrackingScreen");
                            sendBroadcast(intent);
                        }

                        else {
                            Intent intent = new Intent(this, MapForCustomerActivity.class);
                            intent.putExtra("jobId", jobId);
                            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                            sendNotification(titleOfNotification, msgBody, pendingIntent);
                        }

                    } else {
                        Intent intent = new Intent(this, MapForCustomerActivity.class);
                        intent.putExtra("jobId", jobId);
                        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                        sendNotification(titleOfNotification, msgBody, pendingIntent);
                    }
                }

            } else if (action.equalsIgnoreCase("notify_job_started")) {     // arrived of customer

                String jobId = data.getString("job_id");
                String msgBody = "Your job has been started";

                if (BaseUrl.isJobDetailOpen) {
                    if (BaseUrl.jobIdTmp.equals(jobId)) {
                        Intent intent = new Intent("refreshJob");
                        sendBroadcast(intent);
                    } else {
                        Intent intent = new Intent(this, MapForCustomerActivity.class);
                        intent.putExtra("jobId", jobId);
                        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                        sendNotification(titleOfNotification, msgBody, pendingIntent);
                    }
                } else {
                    if (BaseUrl.isCustomerMapOpen) {
                        if (BaseUrl.jobIdTmp.equals(jobId)) {
                            Intent intent = new Intent("refreshCustomerMap");
                            intent.putExtra("isCompleteJobOrReject", false);
                            intent.putExtra("needToStopTime", true);
                            sendBroadcast(intent);
                        } else {
                            Intent intent = new Intent(this, MapForCustomerActivity.class);
                            intent.putExtra("jobId", jobId);
                            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                            sendNotification(titleOfNotification, msgBody, pendingIntent);
                        }
                    } else {
                        Intent intent = new Intent(this, MapForCustomerActivity.class);
                        intent.putExtra("jobId", jobId);
                        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                        sendNotification(titleOfNotification, msgBody, pendingIntent);
                    }
                }
            } else if (action.equalsIgnoreCase("notify_job_ended")) {    // arrived of customer

                String jobId = data.getString("job_id");
                String msgBody = "One of your job has been completed";

                if (BaseUrl.isJobDetailOpen) {
                    if (BaseUrl.jobIdTmp.equals(jobId)) {
                        Intent intent = new Intent("refreshJob");
                        sendBroadcast(intent);
                    } else {
                        Intent intent = new Intent(this, JobDetailActivity.class);
                        intent.putExtra("jobId", jobId);
                        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                        sendNotification(titleOfNotification, msgBody, pendingIntent);
                    }
                } else {
                    if (BaseUrl.isCustomerMapOpen) {
                        if (BaseUrl.jobIdTmp.equals(jobId)) {
                            Intent intent = new Intent("refreshCustomerMap");
                            intent.putExtra("isCompleteJobOrReject", true);
                            intent.putExtra("needToStopTime", true);
                            sendBroadcast(intent);
                        } else {
                            Intent intent = new Intent(this, JobDetailActivity.class);
                            intent.putExtra("jobId", jobId);
                            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                            sendNotification(titleOfNotification, msgBody, pendingIntent);
                        }
                    } else {
                        Intent intent = new Intent(this, JobDetailActivity.class);
                        intent.putExtra("jobId", jobId);
                        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                        sendNotification(titleOfNotification, msgBody, pendingIntent);
                    }
                }

            } else if (action.equalsIgnoreCase("notify_invoice_created")) {     // arrived of customer

                String jobId = data.getString("job_id");
                String msgBody = "Invoice created for your job";

                if (BaseUrl.isJobDetailOpen) {
                    if (BaseUrl.jobIdTmp.equals(jobId)) {
                        Intent intent = new Intent("refreshJob");
                        sendBroadcast(intent);
                    } else {
                        Intent intent = new Intent(this, JobDetailActivity.class);
                        intent.putExtra("jobId", jobId);
                        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                        sendNotification(titleOfNotification, msgBody, pendingIntent);
                    }
                } else {
                    Intent intent = new Intent(this, JobDetailActivity.class);
                    intent.putExtra("jobId", jobId);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                    sendNotification(titleOfNotification, msgBody, pendingIntent);
                }
            } else if (action.equalsIgnoreCase("notify_paying_by_cash")) {      // arrived of professional

                String jobId = data.getString("job_id");
                String msgBody = "Please confirm cash payment";

                if (BaseUrl.isJobDetailOpen) {
                    if (BaseUrl.jobIdTmp.equals(jobId)) {
                        Intent intent = new Intent("refreshJob");
                        sendBroadcast(intent);
                    } else {
                        Intent intent = new Intent(this, JobDetailActivity.class);
                        intent.putExtra("jobId", jobId);
                        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                        sendNotification(titleOfNotification, msgBody, pendingIntent);
                    }
                } else {
                    Intent intent = new Intent(this, JobDetailActivity.class);
                    intent.putExtra("jobId", jobId);
//                intent.putExtra("isRequiredConfirmation", true);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                    sendNotification(titleOfNotification, msgBody, pendingIntent);
                }
            } else if (action.equalsIgnoreCase("notify_cancel_paying_by_paypal")) {

                sendNotification(titleOfNotification, "PayPal payment has been cancel", null);

                /*Intent intent = new Intent(this, JobDetailActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("jobId", data.getString("job_id"));
                intent.putExtra("isRequiredConfirmation", true);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                sendNotification(getString(R.string.app_name), "Please confirm cash payment", pendingIntent);*/

            } else if (action.equalsIgnoreCase("notify_update_payment_mode")) {

                sendNotification(titleOfNotification, "Does not found any payment mode", null);

            } else if (action.equalsIgnoreCase("notify_msg")) {

//              {notify_data={"image":"","user_id":"8","last_msg_id":"68","content":"test no 5","chat_id":"3","timestamp":"1566293008"}, notify_action=notify_msg}

                String user_id = data.getString("user_id");
                String last_msg_id = data.getString("last_msg_id");
                String is_image = data.getString("image");
                String content_msg = data.getString("content");
                String chat_room_id = data.getString("chat_id");
                String timestamp = data.getString("timestamp");

                if (BaseUrl.isChatActivityOpen) {
                    if (BaseUrl.chatRoomId.equals(chat_room_id)) {
                        // Send broadcast if same chat open
                        Intent intent = new Intent("updateChat");
                        intent.putExtra("user_id", user_id);
                        intent.putExtra("last_msg_id", last_msg_id);
                        intent.putExtra("is_image", is_image);
                        intent.putExtra("content_msg", content_msg);
                        intent.putExtra("chat_room_id", chat_room_id);
                        intent.putExtra("timestamp", timestamp);
                        sendBroadcast(intent);
                    } else {
                        Intent intent = new Intent(this, PrivateMessagesActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                        sendNotification(titleOfNotification, "You have new message", pendingIntent);
                    }
                } else {
                    if (is_image.equalsIgnoreCase("")) { // there is no any image attachment
                        Intent intent = new Intent(this, PrivateMessagesActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                        sendNotification(titleOfNotification, "You have new message", pendingIntent);
                    } else {
                        Intent intent = new Intent(this, PrivateMessagesActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                        sendNotification(titleOfNotification, "You have new message with attachment", pendingIntent);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
//            sendNotification("DeveloperTesting", "This is for testing purpose", null);
        }
    }

    private void sendNotification(String title, String messageBody, PendingIntent pendingIntent) {
        NotificationHelper.getInstance(this).notify(title, messageBody, pendingIntent);
    }
}