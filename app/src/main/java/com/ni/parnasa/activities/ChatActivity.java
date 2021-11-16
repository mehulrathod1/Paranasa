package com.ni.parnasa.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ni.parnasa.AsyncTaskUtils.ParseJSON;
import com.ni.parnasa.R;
import com.ni.parnasa.adapters.ChatAdapter;
import com.ni.parnasa.fileupload.FileUtils;
import com.ni.parnasa.pojos.ChatHistoryMessagesPojoItem;
import com.ni.parnasa.pojos.ChatHistoryPojo;
import com.ni.parnasa.pojos.ChatHistoryPojoItem;
import com.ni.parnasa.pojos.CommonPojo;
import com.ni.parnasa.utils.BaseUrl;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.MyLogs;
import com.ni.parnasa.utils.PrefsUtil;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener, ChatAdapter.OnImageClickedListener {

    private Context mContext;
    private PrefsUtil prefsUtil;

    private ImageView imgBack, imgCall, imgSend, imgAttachment;
    private TextView txtUserName;
    private EditText edtMessage;
    private RecyclerView rvChat;
    private ChatAdapter adapter;
    private LinearLayoutManager layoutManager;

    private String receiverId = "", chatRoomId = "", strUserName, strPhone;
    private boolean isFromPopup;
    private int REQ_STORAGE = 41, SELECT_PICTURE = 65, RETRIVE_COUNT = 20;
    private String oldestMessageId = "", latestMessageId = "";

    private int visibleItemCount, totalItemCount, firstVisibleItemPosition, pastVisibleItems;
    private boolean isLoading = false, isFirstTime = true, isAllowToMsg = true;
    private BroadcastReceiver broadcastReceiver;


    private enum RETRIVE_TYPE {
        initial, historical, latest
    }

    private List<ChatHistoryMessagesPojoItem> messagesPojoItemList;
    private BroadcastReceiver receiver;

    @Override
    protected void onPause() {
        super.onPause();
        BaseUrl.isChatActivityOpen = false;
        if (receiver != null)
            unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseUrl.isChatActivityOpen = false;
        BaseUrl.chatRoomId = "";
    }

    @Override
    protected void onResume() {
        super.onResume();
        BaseUrl.isChatActivityOpen = true;
        registerReceiver(receiver, new IntentFilter("updateChat"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mContext = ChatActivity.this;

        init();

        rvChat.addOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                        if (dy > 0) { // for bottom side

                            visibleItemCount = layoutManager.getChildCount();
                            totalItemCount = layoutManager.getItemCount();
                            firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                            pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

//                            Log.e("TAG TOP", "TOTAL ITEM " + totalItemCount + " | VISIBLE ITEM " + visibleItemCount + " | FIRST POS " + firstVisibleItemPosition + " | PAST POS " + pastVisibleItems);
//                            Log.e("TAG TOP", "isLoading " + isLoading + " | isFirstTime " + isFirstTime);

//                            if (!isLoading) {
                            if (!isLoading && firstVisibleItemPosition == 0) { //(totalItemCount - visibleItemCount) <= (firstVisibleItemPosition + RETRIVE_COUNT)) {
                                // End has been reached
                                isLoading = true;
                                apiCallForGetChatHistory(RETRIVE_TYPE.latest);
                            }
                                /*if ((visibleItemCount + pastVisibleItems) <= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= RETRIVE_COUNT) {
                                    isLoading = true;
                                    apiCallForGetChatHistory(RETRIVE_TYPE.latest);
                                }*/
//                            }

                        } else if (dy < 0) {
                            visibleItemCount = layoutManager.getChildCount();
                            totalItemCount = layoutManager.getItemCount();
                            firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                            pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

//                            Log.e("TAG BOTTOM", "isLoading " + isLoading + " TOTAL ITEM " + totalItemCount + " | VISIBLE ITEM " + visibleItemCount + " | FIRST POS " + firstVisibleItemPosition + " | PAST POS " + pastVisibleItems);

                            if (!isLoading) {
                                if ((visibleItemCount + pastVisibleItems) >= totalItemCount) { // && firstVisibleItemPosition == 0) {//&& totalItemCount >= RETRIVE_COUNT) {
                                    isLoading = true;
                                    apiCallForGetChatHistory(RETRIVE_TYPE.historical);
                                }
                            }
                        }
                    }
                }
        );

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                MyLogs.e("TAG", "ChatActivity onReceive call");
                String user_id = intent.getStringExtra("user_id");
                String last_msg_id = intent.getStringExtra("last_msg_id");
                String is_image = intent.getStringExtra("is_image");
                String content_msg = intent.getStringExtra("content_msg");
                String chat_room_id = intent.getStringExtra("chat_room_id");
                String timestamp = intent.getStringExtra("timestamp");

                List<ChatHistoryMessagesPojoItem> tmpList = new ArrayList<>();

                ChatHistoryMessagesPojoItem tmpPojo = new ChatHistoryMessagesPojoItem();
                tmpPojo.setUserId(user_id);
                if (!is_image.equals("")) {
                    tmpPojo.setImage(is_image);
                    tmpPojo.setContent("");
                } else {
                    tmpPojo.setImage("");
                    tmpPojo.setContent(content_msg);
                }

                tmpPojo.setIsRead("0");
                tmpPojo.setCreatedAt("");
                tmpPojo.setTimestamp(timestamp);
                tmpPojo.setDateHeader("");

                tmpList.add(tmpPojo);

                latestMessageId = last_msg_id;

                if (messagesPojoItemList.size() == 0) {
                    messagesPojoItemList.addAll(implementLogic(RETRIVE_TYPE.initial, tmpList, null));
                    adapter.notifyDataSetChanged();
                } else {
                    messagesPojoItemList.addAll(0, implementLogic(RETRIVE_TYPE.latest, tmpList, messagesPojoItemList.get(0)));
                    adapter.notifyDataSetChanged();
                }
            }
        };
    }

    private void init() {
        prefsUtil = new PrefsUtil(mContext);

        imgBack = findViewById(R.id.imgBack);
        imgCall = findViewById(R.id.imgCall);
        imgSend = findViewById(R.id.imgSend);
        imgAttachment = findViewById(R.id.imgAttachment);
        txtUserName = findViewById(R.id.txtUserName);
        rvChat = findViewById(R.id.rvChat);
        edtMessage = findViewById(R.id.edtMessage);

        try {
            isFromPopup = getIntent().getBooleanExtra("isFromPopup", false);
            receiverId = getIntent().getStringExtra("receiverId");
            chatRoomId = getIntent().getStringExtra("chatRoomId");
            BaseUrl.chatRoomId = chatRoomId;
            strUserName = getIntent().getStringExtra("userName");
            strPhone = getIntent().getStringExtra("phone");

            txtUserName.setText(strUserName);

        } catch (Exception e) {
            e.printStackTrace();
        }

        messagesPojoItemList = new ArrayList<>();

        layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setReverseLayout(true);
        rvChat.setLayoutManager(layoutManager);
        adapter = new ChatAdapter(mContext, messagesPojoItemList, receiverId, this);
        rvChat.setAdapter(adapter);

        imgBack.setOnClickListener(this);
        imgCall.setOnClickListener(this);
        imgSend.setOnClickListener(this);
        txtUserName.setOnClickListener(this);
        imgAttachment.setOnClickListener(this);

        apiCallForGetChatHistory(RETRIVE_TYPE.initial);
    }

    @Override
    public void onClick(View v) {
        if (v == imgBack) {
            onBackPressed();
        } else if (v == imgCall) {
            if (!strPhone.equals(""))
                phoneCall();
            else
                Toast.makeText(mContext, R.string.phone_not_found, Toast.LENGTH_SHORT).show();
        } else if (v == imgSend) {
            String strMsg = edtMessage.getText().toString().trim();
            if (strMsg.equals("")) {
                Toast.makeText(mContext, R.string.msg_validation, Toast.LENGTH_SHORT).show();
            } else {
                apiCallForSendMessage(strMsg, false);
            }
        } else if (v == txtUserName) {
            Intent intent = new Intent(mContext, ProfileActivity.class);
            intent.putExtra("anotherUserId", receiverId);
            startActivity(intent);
        } else if (v == imgAttachment) {
            checkStoragePermission();
        }
    }

    private void phoneCall() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + strPhone));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }

    private void checkStoragePermission() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_STORAGE);
        } else {
            openGallery();
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.pick_from)), SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PICTURE && data != null) {

            Uri selectedImageUri = data.getData();

            String realPath = FileUtils.getPath(mContext, selectedImageUri);

            MyLogs.e("TAG", "Real Path : " + realPath);

            apiCallForSendMessage(realPath, true);

        } else {
            MyLogs.e("TAG", "data having null");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkStoragePermission();
            } else {
                Toast.makeText(mContext, R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void apiCallForGetChatHistory(final RETRIVE_TYPE retriveType) {

        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add(BaseUrl.deviceAuthToken);
        value.add(prefsUtil.getDeviceAuthToken());

        param.add("sender_id");
        value.add(prefsUtil.GetUserID());

        param.add("reciever_id");
        value.add(receiverId);

        param.add("chat_id");
        value.add(chatRoomId);

        if (retriveType.equals(RETRIVE_TYPE.initial)) {
            param.add("retrive_type");
            value.add("initial");

            param.add("offset_id");
            value.add("");

        } else if (retriveType.equals(RETRIVE_TYPE.historical)) {
            param.add("retrive_type");
            value.add("historical");

            param.add("offset_id");
            value.add(oldestMessageId);

        } else if (retriveType.equals(RETRIVE_TYPE.latest)) {
            param.add("retrive_type");
            value.add("latest");

            param.add("offset_id");
            value.add(latestMessageId);
        }

        param.add("retrive_count");
        value.add(String.valueOf(RETRIVE_COUNT));

        new ParseJSON(mContext, BaseUrl.getChatMessages, param, value, ChatHistoryPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {

                    if (retriveType.equals(RETRIVE_TYPE.initial)) {
                        messagesPojoItemList.clear();
                        adapter.setIsStartingFlag(true);
                    } /*else {
                        isLoading = false;
                    }*/

                    ChatHistoryPojoItem pojoItem = ((ChatHistoryPojo) obj).getChatHistoryPojoItem();

                    List<ChatHistoryMessagesPojoItem> responsList = pojoItem.getChatHistoryMessagesPojo();

                    if (responsList.size() > 0) {

                        if (retriveType.equals(RETRIVE_TYPE.initial)) {

                            oldestMessageId = responsList.get(responsList.size() - 1).getMessageId();
                            latestMessageId = responsList.get(0).getMessageId();

                            messagesPojoItemList.addAll(implementLogic(RETRIVE_TYPE.initial, responsList, null));
//                            messagesPojoItemList.addAll(formatDateAndGetList(responsList));
//                            messagesPojoItemList.addAll(responsList);
                            adapter.notifyDataSetChanged();


                        } else if (retriveType.equals(RETRIVE_TYPE.latest)) {

//                            oldestMessageId = messagesPojoItemList.get(messagesPojoItemList.size() - 1).getMessageId();
                            latestMessageId = messagesPojoItemList.get(0).getMessageId();

                            messagesPojoItemList.addAll(0, implementLogic(RETRIVE_TYPE.latest, responsList, messagesPojoItemList.get(0)));
//                            messagesPojoItemList.addAll(0, formateDateAndGetList(responsList));
//                            messagesPojoItemList.addAll(0, responsList);
                            adapter.notifyDataSetChanged();


//                            MyLogs.e("ZZZ latest", "oldMsgId :" + oldestMessageId + " | latestMsgId :" + latestMessageId);

                        } else if (retriveType.equals(RETRIVE_TYPE.historical)) {

                            int tmpSize = messagesPojoItemList.size();

                            oldestMessageId = responsList.get(responsList.size() - 1).getMessageId();
//                            oldestMessageId = messagesPojoItemList.get(tmpSize - 1).getMessageId();
//                            latestMessageId = messagesPojoItemList.get(0).getMessageId();


                            ChatHistoryMessagesPojoItem historyPojo = null;

                            for (int i = (tmpSize - 1); i >= 0; i--) {

                                if (messagesPojoItemList.get(i).getDateHeader().equals("")) {
                                    historyPojo = messagesPojoItemList.get(i);
                                    break;
                                }
                            }

                            messagesPojoItemList.addAll(tmpSize, implementLogic(RETRIVE_TYPE.historical, responsList, historyPojo));
//                            messagesPojoItemList.addAll(tmpSize, implementLogic(RETRIVE_TYPE.historical, responsList, messagesPojoItemList.get(tmpSize - 1)));
//                            messagesPojoItemList.addAll(messagesPojoItemList.size(), formateDateAndGetList(responsList));
//                            messagesPojoItemList.addAll(messagesPojoItemList.size(), responsList);
                            adapter.notifyDataSetChanged();

                            isLoading = false;

//                            MyLogs.e("ZZZ historical", "oldMsgId :" + oldestMessageId + " | latestMsgId :" + latestMessageId);
                        }
                    } else {
                        isLoading = false;

                        Toast.makeText(mContext, R.string.no_msg_found, Toast.LENGTH_SHORT).show();
                    }

                    MyLogs.e("TAG ", "oldMsgId : " + oldestMessageId + " | latestMsgId :" + latestMessageId);

                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private List<ChatHistoryMessagesPojoItem> implementLogic(RETRIVE_TYPE type, List<ChatHistoryMessagesPojoItem> responsList, ChatHistoryMessagesPojoItem commonPojo) {

        List<ChatHistoryMessagesPojoItem> list = new ArrayList<>(); // Fresh list for this function limited

        SimpleDateFormat formater = new SimpleDateFormat("dd-MMM-yyyy");

        if (type.equals(RETRIVE_TYPE.latest)) {

            for (int i = 0; i < responsList.size(); i++) {

                Calendar calPre = Calendar.getInstance();
                Calendar calItem = Calendar.getInstance();

                if (i > 0) {

                    calPre.setTimeInMillis(Long.parseLong(responsList.get(i - 1).getTimestamp()) * 1000);
                    calItem.setTimeInMillis(Long.parseLong(responsList.get(i).getTimestamp()) * 1000);

                    boolean isMatchPre = calPre.get(Calendar.YEAR) == calItem.get(Calendar.YEAR) && calPre.get(Calendar.DAY_OF_YEAR) == calItem.get(Calendar.DAY_OF_YEAR);

                    if (isMatchPre) {
                        responsList.get(i).setCreatedAt(calculateTime(responsList.get(i).getTimestamp()));
                        list.add(responsList.get(i));
                    } else {
                        ChatHistoryMessagesPojoItem tmpPojo = new ChatHistoryMessagesPojoItem();
                        tmpPojo.setDateHeader(formater.format(calPre.getTime()));
                        list.add(tmpPojo);
                        responsList.get(i).setCreatedAt(calculateTime(responsList.get(i).getTimestamp()));
                        list.add(responsList.get(i));
                    }
                } else {
                    calPre.setTimeInMillis(Long.parseLong(commonPojo.getTimestamp()) * 1000);
                    calItem.setTimeInMillis(Long.parseLong(responsList.get(i).getTimestamp()) * 1000);

                    boolean isMatchPre = calPre.get(Calendar.YEAR) == calItem.get(Calendar.YEAR) && calPre.get(Calendar.DAY_OF_YEAR) == calItem.get(Calendar.DAY_OF_YEAR);

                    if (isMatchPre) {
                        responsList.get(i).setCreatedAt(calculateTime(responsList.get(i).getTimestamp()));
                        list.add(responsList.get(i));
                    } else {
                        ChatHistoryMessagesPojoItem tmpPojo = new ChatHistoryMessagesPojoItem();
                        tmpPojo.setDateHeader(formater.format(calPre.getTime()));
                        list.add(tmpPojo);
                        responsList.get(i).setCreatedAt(calculateTime(responsList.get(i).getTimestamp()));
                        list.add(responsList.get(i));
                    }
                }
            }

        } else if (type.equals(RETRIVE_TYPE.historical)) {

            for (int i = 0; i < responsList.size(); i++) {

                Calendar calPre = Calendar.getInstance();
                Calendar calItem = Calendar.getInstance();

                if (i > 0) {
                    calPre.setTimeInMillis(Long.parseLong(responsList.get(i - 1).getTimestamp()) * 1000);
                    calItem.setTimeInMillis(Long.parseLong(responsList.get(i).getTimestamp()) * 1000);

                    boolean isMatchPre = calPre.get(Calendar.YEAR) == calItem.get(Calendar.YEAR) && calPre.get(Calendar.DAY_OF_YEAR) == calItem.get(Calendar.DAY_OF_YEAR);

                    if (isMatchPre) {
                        responsList.get(i).setCreatedAt(calculateTime(responsList.get(i).getTimestamp()));
                        list.add(responsList.get(i));
                    } else {
                        ChatHistoryMessagesPojoItem tmpPojo = new ChatHistoryMessagesPojoItem();
                        tmpPojo.setDateHeader(formater.format(calPre.getTime()));
                        list.add(tmpPojo);
                        responsList.get(i).setCreatedAt(calculateTime(responsList.get(i).getTimestamp()));
                        list.add(responsList.get(i));
                    }
                } else {
                    calPre.setTimeInMillis(Long.parseLong(commonPojo.getTimestamp()) * 1000);
                    calItem.setTimeInMillis(Long.parseLong(responsList.get(i).getTimestamp()) * 1000);

                    boolean isMatchPre = calPre.get(Calendar.YEAR) == calItem.get(Calendar.YEAR) && calPre.get(Calendar.DAY_OF_YEAR) == calItem.get(Calendar.DAY_OF_YEAR);

                    if (isMatchPre) {
                        responsList.get(i).setCreatedAt(calculateTime(responsList.get(i).getTimestamp()));
                        list.add(responsList.get(i));
                    } else {
                        ChatHistoryMessagesPojoItem tmpPojo = new ChatHistoryMessagesPojoItem();
                        tmpPojo.setDateHeader(formater.format(calPre.getTime()));
                        list.add(tmpPojo);
                        responsList.get(i).setCreatedAt(calculateTime(responsList.get(i).getTimestamp()));
                        list.add(responsList.get(i));
                    }
                }
            }
        } else {

            /** Retrieve Type Initial */

            int tmpSize = responsList.size();

            for (int i = 0; i < responsList.size(); i++) {

                Calendar calPre = Calendar.getInstance();
                Calendar calItem = Calendar.getInstance();

                if (i > 0) {

                    calPre.setTimeInMillis(Long.parseLong(responsList.get(i - 1).getTimestamp()) * 1000);
                    calItem.setTimeInMillis(Long.parseLong(responsList.get(i).getTimestamp()) * 1000);

                    boolean isMatchPre = calPre.get(Calendar.YEAR) == calItem.get(Calendar.YEAR) && calPre.get(Calendar.DAY_OF_YEAR) == calItem.get(Calendar.DAY_OF_YEAR);

                    if (isMatchPre) {

                        if (i == (tmpSize - 1)) {
                            ChatHistoryMessagesPojoItem tmpPojo = new ChatHistoryMessagesPojoItem();
                            tmpPojo.setDateHeader(formater.format(calPre.getTime()));
                            responsList.get(i).setCreatedAt(calculateTime(responsList.get(i).getTimestamp()));
                            list.add(responsList.get(i));
                            list.add(tmpPojo);
                        } else {
                            responsList.get(i).setCreatedAt(calculateTime(responsList.get(i).getTimestamp()));
                            list.add(responsList.get(i));
                        }
                    } else {
                        ChatHistoryMessagesPojoItem tmpPojo = new ChatHistoryMessagesPojoItem();
                        /*if (i == 1) {
                            tmpPojo.setDateHeader("Today");
                        } else {
                            tmpPojo.setDateHeader(formater.format(calPre.getTime()));
                        }*/
                        tmpPojo.setDateHeader(formater.format(calPre.getTime()));
                        list.add(tmpPojo);
                        responsList.get(i).setCreatedAt(calculateTime(responsList.get(i).getTimestamp()));
                        list.add(responsList.get(i));
                    }
                } else {
                    if (tmpSize == 1) {
                        ChatHistoryMessagesPojoItem tmpPojo = new ChatHistoryMessagesPojoItem();
//                        tmpPojo.setDateHeader("Today");
                        tmpPojo.setDateHeader(formater.format(calPre.getTime()));
                        responsList.get(i).setCreatedAt(calculateTime(responsList.get(i).getTimestamp()));
                        list.add(responsList.get(i));
                        list.add(tmpPojo);
                    } else {
                        responsList.get(i).setCreatedAt(calculateTime(responsList.get(i).getTimestamp()));
                        list.add(responsList.get(i));
                    }
                }
            }
        }

        return list;
    }

    private void calculateNewTime(String previousItemTimestamp, String currentItemTimestamp) {

        //Group by Date
        long previousTs = 0;

//        if (position >= 1) {
        // previousMessage = messages.get(position-1);
//            ChatHistoryMessagesPojoItem previousItem = messagesPojoItemList.get(position - 1);
        previousTs = Long.parseLong(previousItemTimestamp);
//        }

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

//        cal1.setTimeInMillis(Long.parseLong(messages.get(position).getSentTime()) * 1000);
        cal1.setTimeInMillis(Long.parseLong(currentItemTimestamp) * 1000);
        cal2.setTimeInMillis(previousTs * 1000);

        boolean isSameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
//        int dayDiff = calendarCurrent.get(Calendar.DAY_OF_YEAR) - cal1.get(Calendar.DAY_OF_YEAR);
//        Log.e("ZZZ", "Day Diff " + dayDiff);
    }

    private String calculateTime(String resSeconds) {

        Calendar cal = Calendar.getInstance();
        Date currentDate = cal.getTime();

        Date responseDate = new Date();
        responseDate.setTime(Long.parseLong(resSeconds) * 1000);

        /**
         * resArray first element for date and second element for time */
        //        String[] resArray = new String[2];

        String res;
//        MyLogs.e("TAG", "COMPARE " + responseDate.compareTo(currentDate));

        if (responseDate.compareTo(currentDate) == 1) {
//            Log.e("TAG", "responseDate is same to currentDate");
            /**
             * If both date are same it means msg today send so that
             * only display time with AM/PM
             * */

            cal.setTime(responseDate);

//            resArray[0] = "Today";

            int hh = cal.get(Calendar.HOUR);
            if (hh == 0) {
                hh = 12;
            }
            res = (hh <= 9 ? "0" + hh : hh) + ":"
                    + (cal.get(Calendar.MINUTE) <= 9 ? "0" + cal.get(Calendar.MINUTE) : cal.get(Calendar.MINUTE)) + " "
                    + (cal.get(Calendar.AM_PM) == 1 ? "PM" : "AM");


            /*res = (cal.get(Calendar.HOUR) <= 9 ? "0" + cal.get(Calendar.HOUR) : cal.get(Calendar.HOUR)) + ":"
                    + (cal.get(Calendar.MINUTE) <= 9 ? "0" + cal.get(Calendar.MINUTE) : cal.get(Calendar.MINUTE)) + " "
                    + (cal.get(Calendar.AM_PM) == 1 ? "PM" : "AM");*/

        } else if (responseDate.compareTo(currentDate) < 0) {
//            Log.e("TAG", "responseDate is before currentDate");
            /**
             * If responseDate before currentDate then findout no of days difference
             * if day equals to 1 then yesterday other wise print time only */

            long diff = currentDate.getTime() - responseDate.getTime();
//            long day = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            long day = diff / (1000 * 60 * 60 * 24);

            cal.setTime(responseDate);

//            MyLogs.e("TAG", "DIF " + day);

            if (day == 0) {
//                resArray[0] = "Today";
                int hh = cal.get(Calendar.HOUR);
                if (hh == 0) {
                    hh = 12;
                }

                res = (hh <= 9 ? "0" + hh : hh) + ":"
                        + (cal.get(Calendar.MINUTE) <= 9 ? "0" + cal.get(Calendar.MINUTE) : cal.get(Calendar.MINUTE)) + " "
                        + (cal.get(Calendar.AM_PM) == 1 ? "PM" : "AM");

                /*res = (cal.get(Calendar.HOUR) <= 9 ? "0" + cal.get(Calendar.HOUR) : cal.get(Calendar.HOUR)) + ":"
                        + (cal.get(Calendar.MINUTE) <= 9 ? "0" + cal.get(Calendar.MINUTE) : cal.get(Calendar.MINUTE)) + " "
                        + (cal.get(Calendar.AM_PM) == 1 ? "PM" : "AM");*/
            } else if (day == 1) {
//                resArray[0] = "Yesterday";
                int hh = cal.get(Calendar.HOUR);
                if (hh == 0) {
                    hh = 12;
                }

                res = (hh <= 9 ? "0" + hh : hh) + ":"
                        + (cal.get(Calendar.MINUTE) <= 9 ? "0" + cal.get(Calendar.MINUTE) : cal.get(Calendar.MINUTE)) + " "
                        + (cal.get(Calendar.AM_PM) == 1 ? "PM" : "AM");

                /*res = (cal.get(Calendar.HOUR) <= 9 ? "0" + cal.get(Calendar.HOUR) : cal.get(Calendar.HOUR)) + ":"
                        + (cal.get(Calendar.MINUTE) <= 9 ? "0" + cal.get(Calendar.MINUTE) : cal.get(Calendar.MINUTE)) + " "
                        + (cal.get(Calendar.AM_PM) == 1 ? "PM" : "AM");*/
            } else {

//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM, YYYY");
//                resArray[0] = simpleDateFormat.format(cal.getTime());

                int hh = cal.get(Calendar.HOUR);
                if (hh == 0) {
                    hh = 12;
                }

                res = (hh <= 9 ? "0" + hh : hh) + ":"
                        + (cal.get(Calendar.MINUTE) <= 9 ? "0" + cal.get(Calendar.MINUTE) : cal.get(Calendar.MINUTE)) + " "
                        + (cal.get(Calendar.AM_PM) == 1 ? "PM" : "AM");

                /*res = (cal.get(Calendar.HOUR) <= 9 ? "0" + cal.get(Calendar.HOUR) : cal.get(Calendar.HOUR)) + ":"
                        + (cal.get(Calendar.MINUTE) <= 9 ? "0" + cal.get(Calendar.MINUTE) : cal.get(Calendar.MINUTE)) + " "
                        + (cal.get(Calendar.AM_PM) == 1 ? "PM" : "AM");*/
            }

        } else {//if (dateTest.compareTo(cal.getTime()) < 0){
            //Log.e("TAG", "responseDate is after currentDate");
//            resArray[0] = "error";
            res = "non";
        }

        return res;
    }

    /**
     * This request must be in form-data
     */
    private void apiCallForSendMessage(String strMsg, boolean isAttachment) {

        // This is latest
        /*ChatHistoryMessagesPojoItem pojoItem = new ChatHistoryMessagesPojoItem();
        pojoItem.setContent("Tmp 1");
        pojoItem.setImage("");
        pojoItem.setUserId(prefsUtil.GetUserID());
        pojoItem.setCreatedAt("2 hour");

        // this is old
        ChatHistoryMessagesPojoItem pojoItem2 = new ChatHistoryMessagesPojoItem();
        pojoItem2.setContent("Tmp 2");
        pojoItem2.setImage("");
        pojoItem2.setUserId(prefsUtil.GetUserID());
        pojoItem2.setCreatedAt("2 hour");

        List<ChatHistoryMessagesPojoItem> tmp = new ArrayList<>();
        tmp.add(pojoItem);
        tmp.add(pojoItem2);*/

        /*messagesPojoItemList.addAll(0, tmp);
        adapter.notifyDataSetChanged();*/

        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add(BaseUrl.deviceAuthToken);
        value.add(prefsUtil.getDeviceAuthToken());

        param.add("user_id");
        value.add(prefsUtil.GetUserID());

        param.add("chat_id");
        value.add(chatRoomId);

        if (!isAttachment) {
            param.add("message");
            value.add(strMsg);
        } else {
            MyLogs.w("Async", "Image Attached : " + strMsg);
        }

        MyLogs.e("Async", "URL : " + BaseUrl.sendMessage);
        MyLogs.e("Async", "PARAM : " + param);
        MyLogs.e("Async", "VALUE : " + value);

        new ParseJSON(mContext, BaseUrl.sendMessage, param, value, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    edtMessage.setText("");

                    apiCallForGetChatHistory(RETRIVE_TYPE.initial);

                } else {
                    Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, (isAttachment ? "message" : null), (isAttachment ? new File(strMsg) : null));
    }


    @Override
    public void onImageClicked(int pos, String image) {
        Intent intent = new Intent(mContext, ImagePreviewActivity.class);
        intent.putExtra("imagePath", image);
        startActivityForResult(intent, 123);
    }
}
