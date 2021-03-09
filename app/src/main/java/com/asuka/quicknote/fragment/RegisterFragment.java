package com.asuka.quicknote.fragment;

import android.annotation.SuppressLint;
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

import com.asuka.quicknote.R;
import com.asuka.quicknote.db.AccountDatabaseHelper;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
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
        return inflater.inflate(R.layout.fragment_register, container, false);


    }

    //逻辑写在这里
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final FragmentActivity fragmentActivity = requireActivity();
        Button logon_btn = fragmentActivity.findViewById(R.id.resister_btn);
        final AccountDatabaseHelper account_dbHelper;
        account_dbHelper = new AccountDatabaseHelper(fragmentActivity,"Account.db",null,1);
        final EditText account_Edit = fragmentActivity.findViewById(R.id.account_edit_resister);
        final EditText password_Edit = fragmentActivity.findViewById(R.id.password_edit_resister);
        final EditText enter_password_Edit = fragmentActivity.findViewById(R.id.enter_password_edit_resister);
        logon_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db = account_dbHelper.getWritableDatabase();
                String target = account_Edit.getText().toString();//获取当前账号输入框的内容
                String password = password_Edit.getText().toString();//获取当前密码输入框的内容
                //判断输入框是否为空
                if("".equals(target) || "".equals(password)){
                    Toast.makeText(fragmentActivity,"账号密码不能为空",Toast.LENGTH_SHORT ).show();
                }else {
                    @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT account FROM account WHERE account=?",new String[]{target});
                    int test = cursor.getCount();//获取查询结果的行数
                    //判断要注册的账号是否已经存在
                    if(test==0){
                        //存在 写入数据
                        db.execSQL("INSERT INTO account (account,password) values(?,?)",new String[]{target,password});
                        Toast.makeText(fragmentActivity,"注册成功",Toast.LENGTH_SHORT).show();
                        db.close();
                    }else{
                        Toast.makeText(fragmentActivity,"账号已存在",Toast.LENGTH_SHORT).show();
                        db.close();
                    }
                }
            }
        });

    }
}