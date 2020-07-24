package com.ccg.emaildemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.ccg.emaildemo.adapter.OnItemClickListener;
import com.ccg.emaildemo.adapter.StringAdapter;
import com.ccg.emaildemo.email.EmailManager;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private RecyclerView recyclerView;
    private StringAdapter adapter;

    private LinearLayout loadingLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadingLayout = findViewById(R.id.ll_loading);
        recyclerView = findViewById(R.id.rv_email_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        adapter =new StringAdapter(this);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String box = adapter.getItemData(position);
                Intent intent = new Intent(MainActivity.this,EmailActivity.class);
                intent.putExtra("box",box);
                startActivity(intent);
            }
        });

        imap();
    }

    private void clear(){
        if (adapter != null){
            adapter.removeAll();
        }
    }

    private void imap(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<String> folders = EmailManager.getInstance()
                        .getClient()
                        .getFolders();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (adapter != null){
                            adapter.addDatas(folders);
                        }
                        loadingLayout.setVisibility(View.GONE);
                    }
                });
            }
        }).start();
    }

}
