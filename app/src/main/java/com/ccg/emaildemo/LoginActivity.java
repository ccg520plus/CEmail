package com.ccg.emaildemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.ccg.emaillib.EmailConfig;
import com.ccg.emaillib.EmailRecource;
import com.ccg.emaildemo.email.EmailManager;

public class LoginActivity extends AppCompatActivity {

    private EditText mEtUsername;
    private EditText mEtPassword;
    private Button mBtnLogin;

    private int type ;
    private final int GMAIL = 1;
    private final int HOTMAIL = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEtUsername = findViewById(R.id.username);
        mEtPassword = findViewById(R.id.password);
        mBtnLogin = findViewById(R.id.login);
        RadioGroup radioGroup = findViewById(R.id.type);
        type = GMAIL;
        radioGroup.check(R.id.gmail);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.gmail:
                        type = GMAIL;
                        break;
                    case R.id.hotmail:
                        type = HOTMAIL;
                        break;
                        default:
                }
            }
        });

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = mEtUsername.getText().toString();
                final String password = mEtPassword.getText().toString();
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)){
                    Toast.makeText(LoginActivity.this,"please input username or password",Toast.LENGTH_SHORT).show();
                }else{

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            EmailConfig emailConfig = new EmailConfig()
                                    .setUsername(username)
                                    .setPassword(password)
                                    .setDebug(true);

                            if (type == GMAIL){
                                emailConfig.setHost("imap.gmail.com")
                                        .setPort(993)
                                        .setProtocolType(EmailRecource.IMAP);
                            }else if (type ==HOTMAIL){
                                emailConfig.setHost("outlook.office365.com")
                                        .setPort(993)
                                        .setProtocolType(EmailRecource.IMAP);
                            }

                            final boolean loginSuccess = EmailManager.getInstance().config(emailConfig)
                                    .getClient()
                                    .loginAuth(null);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (loginSuccess){
                                        Intent intent =  new Intent(LoginActivity.this,MainActivity.class);
                                        startActivity(intent);
                                    }else{
                                        Toast.makeText(LoginActivity.this,"login failed.",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }).start();

                }

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
