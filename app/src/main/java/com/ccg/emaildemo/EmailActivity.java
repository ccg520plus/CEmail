package com.ccg.emaildemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.ccg.emaillib.entries.Email;
import com.ccg.emaillib.interfaces.OnEmailResultListener;
import com.ccg.emaildemo.adapter.EmailAdapter;
import com.ccg.emaildemo.email.EmailManager;

import java.util.List;

public class EmailActivity extends AppCompatActivity implements OnEmailResultListener {
    private static final String TAG = "EmailActivity";

    private RecyclerView recyclerView;
    private LinearLayout loadingLayout;

    private EmailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        loadingLayout = findViewById(R.id.ll_loading);
        recyclerView = findViewById(R.id.rv_email_list);

        loadingLayout = findViewById(R.id.ll_loading);
        recyclerView = findViewById(R.id.rv_email_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        adapter =new EmailAdapter(this);
        recyclerView.setAdapter(adapter);

        String box = getIntent().getStringExtra("box");
        imap(box);

    }

    private void imap(String box){
        EmailManager.getInstance()
                .getClient()
                .readingAsync(0, 10, box,this);
    }

    @Override
    public void onReceiveResult(List<Email> emails) {
        Log.e(TAG, "onReceiveResult: "+emails.size() );
        if (adapter != null){
            adapter.addDatas(emails);
        }
        loadingLayout.setVisibility(View.GONE);
    }

    @Override
    public void onSendResult() {

    }

    @Override
    public void onGetFolders(List<String> folderNames) {

    }

    @Override
    public void onFailed(Exception e) {
        loadingLayout.setVisibility(View.GONE);
    }
}
