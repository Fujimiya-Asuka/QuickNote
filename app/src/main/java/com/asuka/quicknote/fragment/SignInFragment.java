package com.asuka.quicknote.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.asuka.quicknote.R;
import com.asuka.quicknote.activity.MainActivity;
import com.asuka.quicknote.domain.User;
import com.asuka.quicknote.domain.ResponseResult;
import com.asuka.quicknote.utils.NetWorkUtil;
import com.asuka.quicknote.utils.db.NoteCRUD;
import com.asuka.quicknote.utils.db.ToDoCRUD;
import com.asuka.quicknote.utils.TimeUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignInFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignInFragment extends Fragment {

    private final String TAG = "SignInFragment";
    private EditText username_Edit,password_Edit;


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
        final FragmentActivity fragmentActivity = requireActivity();

        final Button login_btn = fragmentActivity.findViewById(R.id.sign_in_btn);
        //登录页面的账户输入框
        username_Edit = fragmentActivity.findViewById(R.id.account_edit_sign_in);
        //登录页面的密码输入框
        password_Edit = fragmentActivity.findViewById(R.id.password_edit_sign_in);


        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取输入框的账户和密码
                String username = username_Edit.getText().toString();
                String password = password_Edit.getText().toString();
//                判断输入框是否为空
                if ("".equals(username) || "".equals(password)) {
                    Toast.makeText(fragmentActivity, "账号密码不能为空", Toast.LENGTH_SHORT).show();
                }else {
                    login(username,password);
                }
            }
        });

    }

    /**
     * 登录的方法
     * @param username
     * 用户名
     * @param password
     * 密码
     */
    private void login(final String username, final String password){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "获取输入的用户名和密码："+username+"+"+password);
                //执行异步请求
                new NetWorkUtil(getActivity().getApplicationContext()).login(username,password).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "onFailure: "+e.toString());
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(requireActivity(),"网络连接失败",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseBody = response.body().string();
                        Log.d(TAG, "responseBody:"+responseBody);
                        String jsonStr = "["+responseBody+"]";
                        Log.d(TAG, "jsonStr:"+jsonStr);
                        List<ResponseResult> responseResultsList = new Gson().fromJson(jsonStr, new TypeToken<List<ResponseResult>>(){}.getType());
                        for (ResponseResult responseResult : responseResultsList) {
                            Log.d(TAG, "onResponse: "+responseResult.getCode()+"="+responseResult.getMessage());
                            //登录成功
                            if (responseResult.getCode()==1){
                                requireActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(requireActivity(), "登陆成功", Toast.LENGTH_SHORT).show();
                                        //利用SharedPreference将已经登录信息存储到config文件中
                                        SharedPreferences.Editor editor = requireActivity().getSharedPreferences("config", requireActivity().MODE_PRIVATE).edit();
                                        editor.putInt("isLogin", 1);
                                        editor.putString("user",username);
                                        editor.putString("note_tableName","note_"+username);
                                        editor.putString("todo_tableName","todo_"+username);
                                        editor.apply();
                                        Intent intent = new Intent(requireActivity(), MainActivity.class);
                                        intent.putExtra("login",1);
                                        startActivity(intent);
                                        requireActivity().finish();
                                    }
                                });

                            }else {
                                requireActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(requireActivity(), "账户名或密码错误", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }
                });
            }
        }).start();
    }


}