package com.example.quicknote;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignInFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignInFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SignInFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignInFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignInFragment newInstance(String param1, String param2) {
        SignInFragment fragment = new SignInFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final AccountDatabaseHelper account_dbHelper;
        final FragmentActivity fragmentActivity = requireActivity();
        SharedPreferences preferences =fragmentActivity.getSharedPreferences("config", Context.MODE_PRIVATE);
        int isLogin = preferences.getInt("isLogin",0);//提取是否已登录信息
        if(isLogin==1){
            Intent intent = new Intent(fragmentActivity,MainActivity.class);
            startActivity(intent);
            fragmentActivity.finish();
        }else {
            Button login_btn = fragmentActivity.findViewById(R.id.sign_in_btn);
            //登录页面的账户输入框
            final EditText account_Edit = fragmentActivity.findViewById(R.id.account_edit_sign_in);
            //登录页面的密码输入框
            final EditText password_Edit = fragmentActivity.findViewById(R.id.password_edit_sign_in);

            account_dbHelper = new AccountDatabaseHelper(fragmentActivity,"Account.db",null,1);

            login_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SQLiteDatabase db = account_dbHelper.getWritableDatabase();
                    String search = account_Edit.getText().toString();
                    String target = password_Edit.getText().toString();

//                判断输入框是否为空
                    if("".equals(search) || "".equals(target)){
                        Toast.makeText(fragmentActivity,"账号密码不能为空",Toast.LENGTH_SHORT).show();
                    }else {
                        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT password FROM account WHERE account=?",new String[]{search});
                        cursor.getCount();

                        if(cursor.getCount()==0){
                            Toast.makeText(fragmentActivity,"账号不存在",Toast.LENGTH_SHORT).show();
                        }else{
                            cursor.moveToNext();
                            String account = cursor.getString(cursor.getColumnIndex("password"));
                            if (account.equals(target)){
                                Toast.makeText(fragmentActivity,"登陆成功",Toast.LENGTH_SHORT).show();
                                //利用SharedPreference将已经登录信息存储到config文件中
                                SharedPreferences.Editor editor = fragmentActivity.getSharedPreferences("config",Context.MODE_PRIVATE).edit();
                                editor.putInt("isLogin",1);
                                editor.apply();

                                Intent intent = new Intent(fragmentActivity,MainActivity.class);
                                startActivity(intent);
                                fragmentActivity.finish();
                            }else {
                                Toast.makeText(fragmentActivity,"密码错误",Toast.LENGTH_SHORT ).show();
                            }
                            db.close();
                        }

                    }
                }

            });


        }
//        final EditText editText = requireActivity().findViewById(R.id.account_edit123);
//        Button button = requireActivity().findViewById(R.id.login_btn123);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(requireActivity(),editText.getText().toString(),Toast.LENGTH_SHORT).show();
//            }
//        });
    }
}