package com.asuka.quicknote.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.asuka.quicknote.domain.ResponseResult;
import com.asuka.quicknote.domain.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
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
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    private final String TAG="RegisterFragment";

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

        final EditText account_Edit = fragmentActivity.findViewById(R.id.account_edit_resister);
        final EditText password_Edit = fragmentActivity.findViewById(R.id.password_edit_resister);
        final EditText enter_password_Edit = fragmentActivity.findViewById(R.id.enter_password_edit_resister);

        logon_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = account_Edit.getText().toString();//获取当前账号输入框的内容
                String password = password_Edit.getText().toString();//获取当前密码输入框的内容
                String enter_password = enter_password_Edit.getText().toString();//获取当前确认密码输入框的内容
                //判断输入框是否为空
                if("".equals(username) || "".equals(password)||"".equals(enter_password)){
                    Toast.makeText(fragmentActivity,"账号密码不能为空",Toast.LENGTH_SHORT ).show();
                }else if (!password.equals(enter_password)){
                    Toast.makeText(fragmentActivity,"两次输入的密码不一样",Toast.LENGTH_SHORT).show();
                }else{
                    register(username,password);
                }
            }
        });
    }

    private void register(final String username, final String password){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "获取输入的用户名和密码："+username+"+"+password);
                //创建客户端
                OkHttpClient client = new OkHttpClient();
                //根据用户输入的用户名和密码创建User对象并转换为json
                String jsonStr = new Gson().toJson(new User(username,password));
                //设置传输数据类型
                MediaType mediaType = MediaType.parse("application/json");
                //设置请求体
                final RequestBody requestBody = RequestBody.create(mediaType,jsonStr);
                Request request = new Request.Builder()
                        .url("http://8.129.51.177:8080/QuickNoteServlet/register")
                        .post(requestBody)
                        .build();
                //执行异步请求
                client.newCall(request).enqueue(new Callback() {
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
                                        Toast.makeText(requireActivity(), "注册成功", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }else {
                                requireActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(requireActivity(), "用户已存在", Toast.LENGTH_SHORT).show();
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