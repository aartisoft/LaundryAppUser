package com.smartloan.smtrick.user_laundryapp.Fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smartloan.smtrick.user_laundryapp.Adapters.AddsAdapter;
import com.smartloan.smtrick.user_laundryapp.Adapters.Providers_Adapter;
import com.smartloan.smtrick.user_laundryapp.Adapters.Request_Adapter;
import com.smartloan.smtrick.user_laundryapp.CallBack.CallBack;
import com.smartloan.smtrick.user_laundryapp.Constants.Constant;
import com.smartloan.smtrick.user_laundryapp.Constants.Constants;
import com.smartloan.smtrick.user_laundryapp.Listeners.OnImageClickListener;
import com.smartloan.smtrick.user_laundryapp.Listeners.OnRecycleClickListener;
import com.smartloan.smtrick.user_laundryapp.Models.Requests;
import com.smartloan.smtrick.user_laundryapp.Models.ServiceProviderServices;
import com.smartloan.smtrick.user_laundryapp.Models.TimeSlot;
import com.smartloan.smtrick.user_laundryapp.Models.Types;
import com.smartloan.smtrick.user_laundryapp.Models.Upload;
import com.smartloan.smtrick.user_laundryapp.Models.User;
import com.smartloan.smtrick.user_laundryapp.Models.UserServices;
import com.smartloan.smtrick.user_laundryapp.Models.Wash;
import com.smartloan.smtrick.user_laundryapp.Preferences.AppSharedPreference;
import com.smartloan.smtrick.user_laundryapp.R;
import com.smartloan.smtrick.user_laundryapp.Repository.Impl.LeedRepositoryImpl;
import com.smartloan.smtrick.user_laundryapp.Repository.Impl.UserRepositoryImpl;
import com.smartloan.smtrick.user_laundryapp.Repository.LeedRepository;
import com.smartloan.smtrick.user_laundryapp.Repository.UserRepository;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class Fragment_Send_Request extends Fragment implements View.OnClickListener,OnRecycleClickListener, OnImageClickListener {


    //recyclerview object
    private RecyclerView recyclerView;
    Button SendRequest;
    EditText edtDateTime;

    //adapter object
    private RecyclerView.Adapter adapter;
    Providers_Adapter adapter_new;
    //database reference
    private DatabaseReference mDatabase;
    //progress dialog
    private ProgressDialog progressDialog;

    //list to hold all the uploaded images
    private List<UserServices> uploads;
    static private List<String> serList;
    private List<Upload> adds;
    private List<Upload> adds1;
    private ArrayList<User> userList;

    private String subitem;
    AppSharedPreference appSharedPreference;
    String userId;
    User user0;
    User user1;

    LeedRepository leedRepository;
    private DatePickerDialog mDatePickerDialog;
    String fdate;
    int mHour;
    int mMinute;

    Spinner spinnerwash, spinnerTime, spinnerWeights;
    EditText edtVenders, edtRandomTime;
    RelativeLayout layoutRandomTime;
    LinearLayout sliderDotspanel;
    ViewPager viewPager;
    private ImageView[] dots;
    private static int NUM_PAGES = 0;
    UserRepository userRepository;
    ArrayList<User> UserArraylist;
    List<String> ServiceProviders;
    ArrayList<ServiceProviderServices> ServicesList;

    ArrayList<Wash> washList;
    ArrayList<String> wash;
    Wash wash2;
    ArrayList<TimeSlot> TimeList;
    ArrayList<String> Time;
    TimeSlot timeSlot;
    ArrayList<Types> TypeList;
    ArrayList<String> type;
    Types types;

    ArrayList<String> commonList;
    ArrayList<String> commonList3;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_send_request, container, false);

        uploads = new ArrayList<>();
        serList = new ArrayList<>();
        adds = new ArrayList<>();
        adds1 = new ArrayList<>();
        UserArraylist = new ArrayList<>();
        ServiceProviders = new ArrayList<>();
        ServicesList = new ArrayList<>();
        userList = new ArrayList<>();

        washList = new ArrayList<>();
        TimeList = new ArrayList<>();
        TypeList = new ArrayList<>();

        wash = new ArrayList<>();
        Time = new ArrayList<>();
        type = new ArrayList<>();
        commonList = new ArrayList<>();
        commonList3 = new ArrayList<>();

        appSharedPreference = new AppSharedPreference(getContext());
        progressDialog = new ProgressDialog(getContext());
        leedRepository = new LeedRepositoryImpl();
        userRepository = new UserRepositoryImpl();

        String[] washType = new String[]{"Select Wash Types",
                "Wash and Fold",
                "Wash and Iron", "Iron", "Dry Clean"};

        String[] TimeSlot = new String[]{"Select Time Slot",
                "24 Hours",
                "12 Hours", "48 Hours", "Random"};

        String[] Weights = new String[]{"Select Types",
                "Kg Wise",
                "piece Wise"};

        SendRequest = (Button) view.findViewById(R.id.request);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        layoutRandomTime = (RelativeLayout) view.findViewById(R.id.layoutrandomtime);
        sliderDotspanel = (LinearLayout) view.findViewById(R.id.SliderDots);
        viewPager = view.findViewById(R.id.viewPager);

        spinnerwash = (Spinner) view.findViewById(R.id.spinnerwashtype);
        spinnerTime = (Spinner) view.findViewById(R.id.spinnertimeslot);
        edtVenders = (EditText) view.findViewById(R.id.edtvenders);
        edtRandomTime = (EditText) view.findViewById(R.id.txtotherrelationship1);
        spinnerWeights = (Spinner) view.findViewById(R.id.spinnerweights);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                getContext(), R.layout.sppinner_layout_listitem, washType);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerwash.setAdapter(spinnerArrayAdapter);

        ArrayAdapter<String> spinnerArrayAdapter3 = new ArrayAdapter<String>(
                getContext(), R.layout.sppinner_layout_listitem, TimeSlot);
        spinnerArrayAdapter3.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerTime.setAdapter(spinnerArrayAdapter3);

        ArrayAdapter<String> spinnerArrayAdapter2 = new ArrayAdapter<String>(
                getContext(), R.layout.sppinner_layout_listitem, Weights);
        spinnerArrayAdapter2.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerWeights.setAdapter(spinnerArrayAdapter2);

        Query queryadds = FirebaseDatabase.getInstance().getReference("Advertise");
        queryadds.addValueEventListener(valueEventListener1);

        dots = new ImageView[0];

        mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);

        SendRequest.setOnClickListener(this);

        spinnerwash.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String washItem = spinnerwash.getSelectedItem().toString();
                userRepository.readWash(new CallBack() {
                    @Override
                    public void onSuccess(Object object) {
                        washList = (ArrayList<Wash>) object;
                        for (int i = 0; i < washList.size(); i++) {
                            wash2 = washList.get(i);

                        }
                        try {
                            wash.clear();
                            if (washItem.equalsIgnoreCase("Wash and Fold")) {
                                wash.addAll(wash2.getWashandfold());

                            } else if (washItem.equalsIgnoreCase("Wash and Iron")) {
                                wash.addAll(wash2.getWashandiron());

                            } else if (washItem.equalsIgnoreCase("Iron")) {
                                wash.addAll(wash2.getIron());

                            } else if (washItem.equalsIgnoreCase("Dry Clean")) {
                                wash.addAll(wash2.getDryclean());

                            }

                        } catch (Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onError(Object object) {

                    }
                });


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String time = spinnerTime.getSelectedItem().toString();
                userRepository.readTimeSlot(new CallBack() {
                    @Override
                    public void onSuccess(Object object) {
                        TimeList = (ArrayList<TimeSlot>) object;
                        for (int i = 0; i < TimeList.size(); i++) {
                            timeSlot = TimeList.get(i);

                        }
                        try {
                            Time.clear();
                            if (time.equalsIgnoreCase("24 Hours")) {
                                Time.addAll(timeSlot.getOneday());
                                hideotherRelation();

                            } else if (time.equalsIgnoreCase("12 Hours")) {
                                Time.addAll(timeSlot.getHalfday());
                                hideotherRelation();

                            } else if (time.equalsIgnoreCase("48 Hours")) {
                                Time.addAll(timeSlot.getTwodays());
                                hideotherRelation();

                            } else if (time.equalsIgnoreCase("Random")) {
                                Time.addAll(timeSlot.getRandom());
                                showotherRelation();
                            } else {
                                hideotherRelation();
                            }

                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onError(Object object) {

                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerWeights.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String weight = spinnerWeights.getSelectedItem().toString();
                userRepository.readTypes(new CallBack() {
                    @Override
                    public void onSuccess(Object object) {
                        TypeList = (ArrayList<Types>) object;
                        for (int i = 0; i < TypeList.size(); i++) {
                            types = TypeList.get(i);

                        }
                        try {
                            type.clear();
                            if (weight.equalsIgnoreCase("Kg Wise")) {
                                type.addAll(types.getKg());

                            } else if (weight.equalsIgnoreCase("piece Wise")) {
                                type.addAll(types.getPiece());

                            }

                        } catch (Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onError(Object object) {

                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        edtVenders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog1 = new Dialog(getContext());
                dialog1.getWindow().setBackgroundDrawableResource(R.drawable.dialogboxanimation);
                dialog1.setContentView(R.layout.dialog_service_providers);

                RecyclerView Providers_recycle = (RecyclerView) dialog1.findViewById(R.id.recycler_view_service_provicers);
                Providers_recycle.setHasFixedSize(true);
                Providers_recycle.setLayoutManager(new LinearLayoutManager(getContext()));

                String wash5 = spinnerwash.getSelectedItem().toString();
                String time5 = spinnerTime.getSelectedItem().toString();
                String weight5 = spinnerWeights.getSelectedItem().toString();
                if (!wash5.equalsIgnoreCase("Select Wash Types") && time5.equalsIgnoreCase("Select Time Slot") && weight5.equalsIgnoreCase("Select Types")) {
                    ReadServiseProviders(wash, Providers_recycle, dialog1);

                } else if (wash5.equalsIgnoreCase("Select Wash Types") && !time5.equalsIgnoreCase("Select Time Slot") && weight5.equalsIgnoreCase("Select Types")) {
                    ReadServiseProviders(Time, Providers_recycle, dialog1);

                } else if (wash5.equalsIgnoreCase("Select Wash Types") && time5.equalsIgnoreCase("Select Time Slot") && !weight5.equalsIgnoreCase("Select Types")) {
                    ReadServiseProviders(type, Providers_recycle, dialog1);

                } else if (!wash5.equalsIgnoreCase("Select Wash Types") && !time5.equalsIgnoreCase("Select Time Slot") && weight5.equalsIgnoreCase("Select Types")) {

                    commonList.clear();
                    for (int i = 0; i < wash.size(); i++) {
                        for (int j = 0; j < Time.size(); j++) {
                            if (wash.get(i).equalsIgnoreCase(Time.get(j))) {
                                commonList.add(wash.get(i));
//                                Toast.makeText(Send_Request_Activity.this, commonList.get(i), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    ReadServiseProviders(commonList, Providers_recycle, dialog1);

                } else if (!wash5.equalsIgnoreCase("Select Wash Types") && time5.equalsIgnoreCase("Select Time Slot") && !weight5.equalsIgnoreCase("Select Types")) {
                    commonList3.clear();
                    for (int i = 0; i < wash.size(); i++) {
                        for (int j = 0; j < type.size(); j++) {
                            if (wash.get(i).equalsIgnoreCase(type.get(j))) {
                                commonList3.add(wash.get(i));
//                                Toast.makeText(Send_Request_Activity.this, commonList3.get(i), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    ReadServiseProviders(commonList3, Providers_recycle, dialog1);

                } else if (wash5.equalsIgnoreCase("Select Wash Types") && !time5.equalsIgnoreCase("Select Time Slot") && !weight5.equalsIgnoreCase("Select Types")) {
                    commonList3.clear();
                    for (int i = 0; i < Time.size(); i++) {
                        for (int j = 0; j < type.size(); j++) {
                            if (Time.get(i).equalsIgnoreCase(type.get(j))) {
                                commonList3.add(Time.get(i));
//                                Toast.makeText(Send_Request_Activity.this, commonList3.get(i), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    ReadServiseProviders(commonList3, Providers_recycle, dialog1);

                } else if (!wash5.equalsIgnoreCase("Select Wash Types") && !time5.equalsIgnoreCase("Select Time Slot") && !weight5.equalsIgnoreCase("Select Types")) {

                    commonList.clear();
                    commonList3.clear();
                    for (int i = 0; i < wash.size(); i++) {
                        for (int j = 0; j < Time.size(); j++) {
                            if (wash.get(i).equalsIgnoreCase(Time.get(j))) {
                                commonList.add(wash.get(i));
//                                Toast.makeText(Send_Request_Activity.this, commonList.get(i), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    for (int i = 0; i < commonList.size(); i++) {
                        for (int j = 0; j < type.size(); j++) {
                            if (commonList.get(i).equalsIgnoreCase(type.get(j))) {
                                commonList3.add(commonList.get(i));
//                                Toast.makeText(Send_Request_Activity.this, commonList3.get(i), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    ReadServiseProviders(commonList3, Providers_recycle, dialog1);
                }

                dialog1.show();
                Window window = dialog1.getWindow();
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }

        });
        
        return view;
    }

    private void ReadServiseProviders(ArrayList<String> commonList, RecyclerView recyclerView, Dialog dialog1) {
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        userList.clear();
        for (int i = 0; i < commonList.size(); i++) {
            String id = commonList.get(i);
            userRepository.readServiceProviderById(id, new CallBack() {
                @Override
                public void onSuccess(Object object) {
                    if (object != null) {
                        user1 = (User) object;
                        userList.add(user1);
                    }

                    adapter_new = new Providers_Adapter(getContext(), userList, (OnRecycleClickListener) getActivity(), dialog1);
                    //adding adapter to recyclerview
                    recyclerView.setAdapter(adapter_new);
                    progressDialog.dismiss();
                }

                @Override
                public void onError(Object object) {
                    progressDialog.dismiss();
                }
            });
        }
    }


    @Override
    public void onClick(View v) {
        if (v == SendRequest) {
            final Dialog dialog1 = new Dialog(getContext());
            dialog1.getWindow().setBackgroundDrawableResource(R.drawable.dialogboxanimation);
            dialog1.setContentView(R.layout.dialog_select_date);

            edtDateTime = (EditText) dialog1.findViewById(R.id.txtdatetime);
            Button Add = (Button) dialog1.findViewById(R.id.btnsendrequest);
            Button cancle = (Button) dialog1.findViewById(R.id.btncancle);

            String name = edtVenders.getText().toString();
            userRepository.readServiceProviderByName(name, new CallBack() {
                @Override
                public void onSuccess(Object object) {
                    user0 = (User) object;
                }

                @Override
                public void onError(Object object) {

                }
            });

            setDateTimeField();
            edtDateTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDatePickerDialog.show();

                }
            });

            Add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Requests requests = new Requests();
                    requests.setServiceProviderId(user0.getUserid());
                    requests.setUserId(appSharedPreference.getUserid());
                    requests.setUserName(appSharedPreference.getName());
                    requests.setUserAddress(appSharedPreference.getAddress());
                    requests.setUserMobile(appSharedPreference.getNumber());
                    requests.setUserPinCode(appSharedPreference.getPincode());
                    requests.setDate(edtDateTime.getText().toString());
                    requests.setServiceList(serList);
                    requests.setStatus(Constant.STATUS_GENERATED);
                    requests.setRequestId(Constant.REQUESTS_TABLE_REF.push().getKey());
                    leedRepository.sendRequest(requests, new CallBack() {
                        @Override
                        public void onSuccess(Object object) {

                            sendFCMPush(user0.getTokan());
                            Toast.makeText(getContext(), "Request Sent", Toast.LENGTH_SHORT).show();
                            dialog1.dismiss();
                        }

                        @Override
                        public void onError(Object object) {

                        }
                    });
                }
            });
            cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog1.dismiss();
                }
            });

            dialog1.show();
        }
    }

    private void setDateTimeField() {
        Calendar newCalendar = Calendar.getInstance();
        mDatePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy");
                final Date startDate = newDate.getTime();
                fdate = sd.format(startDate);

                timePicker();
            }

            private void timePicker() {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                mHour = hourOfDay;
                                mMinute = minute;

                                edtDateTime.setText(fdate + " " + hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }

    @Override
    public void onImageClick(String imageData, boolean isChecked) {
        if (isChecked) {
            serList.add(imageData);
        } else if (!isChecked) {
            int i = serList.indexOf(imageData);
            serList.remove(i);
        }
    }

    @Override
    public void onRecycleClick(User user) {
        //user0 = user;
        edtVenders.setText(user.getName());
        uploads.clear();
        Query query = FirebaseDatabase.getInstance().getReference("UserServices").orderByChild("userId").equalTo(user.getUserid());

        query.addValueEventListener(valueEventListener);
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            uploads.clear();
            progressDialog.dismiss();
            //iterating through all the values in database
            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                UserServices upload = postSnapshot.getValue(UserServices.class);

                uploads.add(upload);
            }
            //creating adapter
            adapter = new Request_Adapter(getContext(), uploads);
            //adding adapter to recyclerview
            recyclerView.setAdapter(adapter);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            progressDialog.dismiss();

        }

    };


    public void showotherRelation() {
        RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) layoutRandomTime.getLayoutParams();
        params1.height = -1;
        layoutRandomTime.setLayoutParams(params1);
    }

    public void hideotherRelation() {
        RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) layoutRandomTime.getLayoutParams();
        params1.height = 0;
        layoutRandomTime.setLayoutParams(params1);
    }

    ValueEventListener valueEventListener1 = new ValueEventListener() {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            adds.clear();
            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                Upload upload = postSnapshot.getValue(Upload.class);

                adds.add(upload);
                int size = adds.size() - 1;
                adds1.clear();
                for (int i = size; i >= 0; i--) {
                    adds1.add(adds.get(i));
                }

            }
            NUM_PAGES = adds1.size();
//            showDots();
            AddsAdapter adapter = new AddsAdapter(getContext(), adds1);
            viewPager.setAdapter(adapter);

            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new SliderTimer(), 500, 4000);

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };


    private class SliderTimer extends TimerTask {

        @Override
        public void run() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (viewPager.getCurrentItem() < NUM_PAGES - 1) {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                    } else {
                        viewPager.setCurrentItem(0);
                    }
                }
            });
        }
    }

    private void sendFCMPush(String Token) {

        String Legacy_SERVER_KEY = "AIzaSyCM5Eb6ZrYBWhzGRSsm5WKYlzlT7BlhuKs";
        String msg = "New Order From" + appSharedPreference.getName();
        String title = "New Order Has Been Received";
        String token = Token;

        JSONObject obj = null;
        JSONObject objData = null;
        JSONObject dataobjData = null;

        try {
            obj = new JSONObject();
            objData = new JSONObject();

            try {
                objData.put("body", msg);
                objData.put("title", title);
                objData.put("sound", "default");
                objData.put("icon", "icon_name"); //   icon_name image must be there in drawable
                objData.put("tag", token);
                objData.put("priority", "high");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            dataobjData = new JSONObject();
            dataobjData.put("text", msg);
            dataobjData.put("title", title);

            obj.put("to", token);
            //obj.put("priority", "high");

            obj.put("notification", objData);
            obj.put("data", dataobjData);
            Log.e("!_@rj@_@@_PASS:>", obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, Constants.FCM_PUSH_URL, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("!_@@_SUCESS", response + "");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("!_@@_Errors--", error + "");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "key=" + Legacy_SERVER_KEY);
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        int socketTimeout = 1000 * 60;// 60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }

}