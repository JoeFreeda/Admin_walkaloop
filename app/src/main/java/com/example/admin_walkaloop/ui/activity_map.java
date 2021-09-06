package com.example.admin_walkaloop.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin_walkaloop.network.APIClient;
import com.example.admin_walkaloop.network.APIInterface;
import com.example.admin_walkaloop.R;
import com.example.admin_walkaloop.model.PlaceInfo;
import com.example.admin_walkaloop.model.Response;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;

public class activity_map extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    //    HashMap<Integer,PlaceInfo>  has_place = new HashMap<>();
    private GoogleMap mMap;
    private Uri selectedImage;
    private String part_image;
    ImageView img_preview;
    TextView tv_preview;
    int n_place = 0;
    private boolean isEdit, isNew = true;
    PlaceInfo place_edit = null;
    private int ntag;
    private String encoded;
    List<PlaceInfo> lst_place = new ArrayList<>();
    Button btn_submit;
    public Response response;
    private boolean isPlacelist = false;
    private AlertDialog dialog;
    private APIInterface apiInterface;
    boolean isFirst = false;

    public static final String DATE_FORMAT = "yyyyMMdd_HHmmss";
    public static final String IMAGE_DIRECTORY = "ImageScalling";
    private File file;
    private SimpleDateFormat dateFormatter;

    private File destFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        file = new File(Environment.getExternalStorageDirectory()
                + "/" + IMAGE_DIRECTORY);
        if (!file.exists()) {
            file.mkdirs();
        }

        dateFormatter = new SimpleDateFormat(
                DATE_FORMAT, Locale.US);
        changeStatusBarColor();
        apiInterface = APIClient.getClient().create(APIInterface.class);
        isFirst = true;
        btn_submit = findViewById(R.id.btn_submit);
//        Bundle bundle = getIntent().getExtras();
        if (GeneralData.response != null) {
            response = GeneralData.response;
            lst_place.addAll(response.getMarkers());
            isPlacelist = true;
            btn_submit.setText("Close");
        }
//// getting the string back
//        if (bundle != null) {
//            Bundle getvalue = bundle.getBundle("value");
//            if (getvalue != null) {
//                getvalue.getBoolean("islist", false);
//                if(MainActivity.response!=null) {
//                    response = MainActivity.response;
//                    lst_place.addAll(MainActivity.response.getMarkers());
//                    isPlacelist = true;
//                }
//            }
//        }

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lst_place.size() > 0 &&!isPlacelist) {
                    response.setMarkers(lst_place);
                    response.setNoofPlaces(lst_place.size());
                    GeneralData.response = response;
                    Call<String> call_request = apiInterface.insertMarker(response);
                    call_request.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                            Toast.makeText(activity_map.this,response.message()+"",Toast.LENGTH_LONG).show();
                            GeneralData.isSubmit = true;
                            activity_map.this.finish();
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(activity_map.this,t.getMessage()+"",Toast.LENGTH_LONG).show();
                            GeneralData.isSubmit = false;
                            activity_map.this.finish();
                        }
                    });
                   // activity_map.this.finish();
                }else{
                    activity_map.this.finish();
                }
            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        isEdit = true;
        getInformation(marker.getPosition(), marker.getTitle());
        return true;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLng london = new LatLng(51.5074, 0.1278);
        mMap.setMinZoomPreference(10);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(london));
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                isEdit = false;
                if(!isPlacelist) {
                    getInformation(latLng, null);
                }
            }
        });

        if (isPlacelist) {
            isNew = false;
            LatLng lng_main = new LatLng(Double.parseDouble(response.getLatitude()), Double.parseDouble(response.getLongitude()));
            mMap.addMarker(new MarkerOptions().position(lng_main).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_pin)).title("-1"));

            // below lin is use to zoom our camera on map.
            mMap.animateCamera(CameraUpdateFactory.zoomTo(18.0f));
           // mMap.setMinZoomPreference(17);
            // below line is use to move our camera to the specific location.
            mMap.moveCamera(CameraUpdateFactory.newLatLng(lng_main));

            PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
            for (int i = 0; i <= lst_place.size(); i++) {
                if(i!=lst_place.size()) {
                    PlaceInfo place = lst_place.get(i);
                    LatLng lng = new LatLng(Double.parseDouble(place.getLatitude()), Double.parseDouble(place.getLongitude()));
                    mMap.addMarker(new MarkerOptions().position(lng).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_pin)).title(i+""));
                    mMap.setOnMarkerClickListener(activity_map.this::onMarkerClick);
                    // below lin is use to zoom our camera on map.
//            mMap.animateCamera(CameraUpdateFactory.zoomTo(18.0f));
//
//            // below line is use to move our camera to the specific location.
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(lng));
                    options.add(lng);
                    mMap.addPolyline(options);
                }else if(i==lst_place.size()){
                    PlaceInfo place = lst_place.get(0);
                    LatLng lng = new LatLng(Double.parseDouble(place.getLatitude()), Double.parseDouble(place.getLongitude()));
                    options.add(lng);
                    mMap.addPolyline(options);
                }
            }
        }

    }

    private void getInformation(LatLng latLng, String tag) {
        AlertDialog.Builder builder
                = new AlertDialog.Builder(this);
        Button btnSave;
        Button btn_cancel;
        Button btn_upload;
        TextInputEditText txt_placename, txt_mainname;
        EditText textArea_information;
        LinearLayout lay_upload,lay_confirm;
        CheckBox chk_isdefault;

        // set the custom layout
        final View view
                = getLayoutInflater()
                .inflate(
                        R.layout.alert_placeinfo,
                        null);
        btnSave = view.findViewById(R.id.btn_save);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_upload = view.findViewById(R.id.btn_upload);
        img_preview = view.findViewById(R.id.img_preview);
//        tv_preview = view.findViewById(R.id.tv_preview);
        txt_mainname = view.findViewById(R.id.txt_mainname);
        txt_placename = view.findViewById(R.id.txt_placename);
        textArea_information = view.findViewById(R.id.textArea_information);
        lay_upload = view.findViewById(R.id.lay_upload);
        lay_confirm = view.findViewById(R.id.lay_confirm);
        chk_isdefault = view.findViewById(R.id.chk_isdefault);
        if(isFirst){
            txt_placename.setVisibility(View.GONE);
            textArea_information.setVisibility(View.GONE);
            lay_upload.setVisibility(View.GONE);
            txt_mainname.setVisibility(View.VISIBLE);
            chk_isdefault.setVisibility(View.VISIBLE);
            img_preview.setVisibility(View.GONE);
            isFirst = false;
        }else{
            chk_isdefault.setVisibility(View.GONE);
        }

        if(isPlacelist){
            lay_confirm.setVisibility(View.GONE);
        }

        txt_placename.setVisibility(View.GONE);
        if (tag != null) {
            ntag = Integer.parseInt(tag);
            if (ntag != -1) {
                txt_mainname.setVisibility(View.VISIBLE);
                textArea_information.setVisibility(View.VISIBLE);
                img_preview.setVisibility(View.VISIBLE);
                txt_placename.setVisibility(View.GONE);
                lay_upload.setVisibility(View.VISIBLE);
                chk_isdefault.setVisibility(View.GONE);
                place_edit = lst_place.get(ntag);
                //txt_mainname.setText(place_edit.getStr_name());
                txt_mainname.setText(place_edit.getStr_name());
                textArea_information.setText(place_edit.getPlace_desc());
                byte[] decodedString = Base64.decode(place_edit.getImg_encode(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                img_preview.setImageBitmap(decodedByte);
            } else {
                txt_placename.setVisibility(View.GONE);
                textArea_information.setVisibility(View.GONE);
                lay_upload.setVisibility(View.GONE);
                txt_mainname.setVisibility(View.VISIBLE);
                chk_isdefault.setVisibility(View.VISIBLE);
                txt_mainname.setText(response.getpName());
                if(response.getIsdefault()==1){
                    chk_isdefault.setChecked(true);
                }else{
                    chk_isdefault.setChecked(false);
                }
                img_preview.setVisibility(View.GONE);
            }
        }
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Open Gallery"), 1);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEdit) {
                    if (isNew) {
                        response = new Response();
                        response.setpName(txt_mainname.getText().toString());
                        response.setLatitude(latLng.latitude + "");
                        response.setLongitude(latLng.longitude + "");
                        if(chk_isdefault.isChecked()) {
                            response.setIsdefault(1);
                        }else{
                            response.setIsdefault(0);
                        }
                        isNew = false;
                        mMap.setMinZoomPreference(10);
                        mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_pin)).title("-1"));
                        mMap.setOnMarkerClickListener(activity_map.this::onMarkerClick);
                        // below lin is use to zoom our camera on map.
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(18.0f));

                        // below line is use to move our camera to the specific location.
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    } else {
                        PlaceInfo placeInfo = new PlaceInfo();
                        placeInfo.setStr_name(txt_mainname.getText().toString());
                        //placeInfo.setStr_name(txt_name.getText().toString());
                        placeInfo.setPlace_desc(textArea_information.getText().toString());
                        placeInfo.setLatitude(latLng.latitude + "");
                        placeInfo.setLongitude(latLng.longitude + "");
                        placeInfo.setImg_encode(encoded);

//                    has_place.put(n_place,placeInfo);
                        lst_place.add(placeInfo);
                        n_place = lst_place.indexOf(placeInfo);
                        lst_place.get(n_place).setNpostion(n_place);
                        mMap.setMinZoomPreference(10);
                        mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_pin)).title(n_place + ""));
                        mMap.setOnMarkerClickListener(activity_map.this::onMarkerClick);
//                        // below lin is use to zoom our camera on map.
//                        mMap.animateCamera(CameraUpdateFactory.zoomTo(18.0f));
//
//                        // below line is use to move our camera to the specific location.
//                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    }

                } else {
                    if (ntag != -1) {
                        place_edit.setPlace_name(txt_placename.getText().toString());
                        place_edit.setStr_name(txt_mainname.getText().toString());
                        place_edit.setPlace_desc(textArea_information.getText().toString());
                     //   lst_place.add(ntag, place_edit);
                    } else {
                        response.setpName(txt_mainname.getText().toString());
                    }
//                    has_place.put(ntag,place_edit);
                }
                dialog.dismiss();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        builder.setView(view);

        dialog = builder.create();
        dialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                selectedImage = data.getData();                                                         // Get the image file URI
                String[] imageProjection = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, imageProjection, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    int indexImage = cursor.getColumnIndex(imageProjection[0]);
                    part_image = cursor.getString(indexImage);
                   // decodeFile(new File(part_image));
//                       tv_preview.setText(part_image);                                                        // Get the image file absolute path
                    Bitmap bitmap = null;
                    Bitmap scaled = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
//                        int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
//                        scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
                        scaled = getResizedBitmap(bitmap,200);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        scaled.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    img_preview.setImageBitmap(scaled);                                                       // Set the ImageView with the bitmap of the image
                }
            }
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.login_bk_color));
        }
    }

    /**
     * reduces the size of the image
     * @param image
     * @param maxSize
     * @return
     */
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}
