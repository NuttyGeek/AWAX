package com.example.bhati.routeapplication.Activities;

import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.bhati.routeapplication.Adapter.AlbumAdapter;
import com.example.bhati.routeapplication.Database.DBHelper;
import com.example.bhati.routeapplication.R;
import com.example.bhati.routeapplication.Rest.Album;

import java.util.ArrayList;
import java.util.List;

public class FileScreenActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AlbumAdapter adapter;
    private List<Album> albumList;
    private DBHelper myDb;
    private  List<String> sizeList;
    private double total_size;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_file_screen);

//        ActionBar actionBar = getActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);

        myDb = new DBHelper(this);
        recyclerView = findViewById(R.id.recycler_view);
        TextView textView  = findViewById(R.id.text);
        TextView txtViewTotalFiles  = findViewById(R.id.txtTotalFiles);
        TextView txtViewTotalSize  = findViewById(R.id.txtTotalSize);

        ImageButton backButton = (ImageButton) findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileScreenActivity.super.onBackPressed();
            }
        });

        albumList = new ArrayList<>();
        sizeList = new ArrayList<>();

        albumList = myDb.getAllData();
        Cursor cursor = myDb.getAllSize();
        total_size = 0;
        while (cursor.moveToNext())
        {
            String a =cursor.getString(cursor.getColumnIndex(DBHelper.SIZE));
            a  = a.replace(" MB","");
            sizeList.add(a);
        }

        for (int i = 0 ; i< sizeList.size() ; i++)
        {
           total_size = total_size + Double.parseDouble(sizeList.get(i));
        }
        String size_in_mbs = String.format("%.2f", total_size);
       txtViewTotalFiles.setText(albumList.size()+" Files");
        txtViewTotalSize.setText("Size "+size_in_mbs+" (MBs)");

        if (albumList.size() <= 0)
        {
            textView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            return;
        }
        adapter = new AlbumAdapter(this, albumList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        startActivity(new Intent(this ,Home.class));
//        finish();
    }
}
