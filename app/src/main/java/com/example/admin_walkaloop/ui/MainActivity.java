package com.example.admin_walkaloop.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.admin_walkaloop.network.APIClient;
import com.example.admin_walkaloop.network.APIInterface;
import com.example.admin_walkaloop.R;
import com.example.admin_walkaloop.adapter.Place_adapter;
import com.example.admin_walkaloop.model.MarkersItem;
import com.example.admin_walkaloop.model.Response;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView list_place;
    List<Response> lis_response= new ArrayList<>();
    private RecyclerView recyclerView;
    private Place_adapter adapter;
    public FloatingActionButton add_fab;
    private APIInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        changeStatusBarColor();
        apiInterface = APIClient.getClient().create(APIInterface.class);
        list_place = findViewById(R.id.list_place);
        add_fab = findViewById(R.id.add_fab);
        lis_response = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.list_place);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        Call<MarkersItem> call_request = apiInterface.getallDetails();
        call_request.enqueue(new Callback<MarkersItem>() {
            @Override
            public void onResponse(Call<MarkersItem> call, retrofit2.Response<MarkersItem> response) {
              // List<MarkersItem> markersItem = response.body();
               // Toast.makeText(MainActivity.this,response.body()+"",Toast.LENGTH_LONG).show();
                MarkersItem markersItems = (MarkersItem) response.body();
                lis_response = markersItems.getListofplaces();
                if(lis_response!=null&&lis_response.size()>0) {
                    adapter = new Place_adapter(lis_response, MainActivity.this);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<MarkersItem> call, Throwable t) {
                Toast.makeText(MainActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        add_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivity.this, activity_map.class);
                GeneralData.response = null;
                startActivity(in);
            }
        });
//        Response response = new Response();
//        response.setPName("Park");
//        response.setIsdefault(1);
//        PlaceInfo markersItem = new PlaceInfo();
//        markersItem.setPlace_name("Entrance");
//        markersItem.setPlace_desc("The definition of a description is a statement that gives details about someone or something");
//        List<PlaceInfo> markersItems = new ArrayList<>();
//        markersItems.add(markersItem);
//        response.setMarkers(markersItems);
//        lis_response.add(response);


    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        if(GeneralData.response!=null) {
//            lis_response.add(GeneralData.response);
//            adapter.notifyDataSetChanged();
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
        if (GeneralData.isSubmit&&GeneralData.response != null) {
            lis_response.add(GeneralData.response);
            adapter.notifyDataSetChanged();
            GeneralData.isSubmit = false;
        }
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.login_bk_color));
        }
    }
}