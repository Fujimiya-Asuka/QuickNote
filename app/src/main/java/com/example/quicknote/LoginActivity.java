package com.example.quicknote;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {


    private EditText account_Edit,password_Edit;
    private AccountDatabaseHelper account_dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences =getSharedPreferences("config",MODE_PRIVATE   );
        int isLogin = preferences.getInt("isLogin",0);//提取是否已登录信息
        if(isLogin==1){
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }else {
            setContentView(R.layout.activity_login);
            Button login_btn = findViewById(R.id.login_btn);
            Button logon_btn = findViewById(R.id.test_btn);
            account_Edit = findViewById(R.id.account_edit);
            password_Edit = findViewById(R.id.password_edit);
            account_dbHelper = new AccountDatabaseHelper(this,"Account.db",null,1);


            login_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SQLiteDatabase db = account_dbHelper.getWritableDatabase();
                    String search = account_Edit.getText().toString();
                    String target = password_Edit.getText().toString();

//                判断输入框是否为空
                    if("".equals(search) || "".equals(target)){
                        Toast.makeText(LoginActivity.this,"账号密码不能为空",Toast.LENGTH_SHORT).show();
                    }else {
                        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT password FROM account WHERE account=?",new String[]{search});
                        cursor.getCount();

                        if(cursor.getCount()==0){
                            Toast.makeText(LoginActivity.this,"账号不存在",Toast.LENGTH_SHORT).show();
                        }else{
                            cursor.moveToNext();
                            String account = cursor.getString(cursor.getColumnIndex("password"));
                            if (account.equals(target)){
                                Toast.makeText(LoginActivity.this,"登陆成功",Toast.LENGTH_SHORT).show();
                                //利用SharedPreference将已经登录信息存储到config文件中
                            SharedPreferences.Editor editor = getSharedPreferences("config",MODE_PRIVATE).edit();
                            editor.putInt("isLogin",1);
                            editor.apply();

                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                            }else {
                                Toast.makeText(LoginActivity.this,"密码错误",Toast.LENGTH_SHORT ).show();
                            }
                            db.close();
                        }

                    }
                }

            });


            logon_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SQLiteDatabase db = account_dbHelper.getWritableDatabase();
                    String target = account_Edit.getText().toString();//获取当前账号输入框的内容
                    String password = password_Edit.getText().toString();//获取当前密码输入框的内容
                    //判断输入框是否为空
                    if("".equals(target) || "".equals(password)){
                        Toast.makeText(LoginActivity.this,"账号密码不能为空",Toast.LENGTH_SHORT ).show();
                    }else {
                        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT account FROM account WHERE account=?",new String[]{target});
                        int test = cursor.getCount();//获取查询结果的行数
                        //判断要注册的账号是否已经存在
                        if(test==0){
                            //存在 写入数据
                            db.execSQL("INSERT INTO account (account,password) values(?,?)",new String[]{target,password});
                            Toast.makeText(LoginActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                            db.close();
                        }else{
                            Toast.makeText(LoginActivity.this,"账号已存在",Toast.LENGTH_SHORT).show();
                            db.close();
                        }
                    }
                }
            });
        }




    }
}
