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

    private void initNotes(NoteCRUD noteCRUD){
        //测试代码：
        final String title1 = "冰灯";
        final String data1 = "冰灯是流行于中国北方的一种古老的民间艺术形式。因为独特的地域优势，黑龙江可以说是制作冰灯最早的地方。" +
                "传说在很早以前，每到冬季的夜晚，在松嫩平原上，人们总会看到三五成群的农夫和渔民在悠然自得地喂马和" +
                "捕鱼，他们所使用的照明工具就是用冰做成的灯笼。这便是最早的冰灯。当时制作冰灯的工艺也很简单，" +
                "把水放进木桶里冻成冰坨，凿出空心，放个油灯在里面，用以照明，冰罩挡住了凛冽的寒风，黑夜里便有了" +
                "不灭的灯盏，冰灯成了人们生活中不可缺少的帮手。后来，每逢新春佳节和上元之夜，人们又把它加以装饰，" +
                "而成为供人观赏的独特的艺术表现形式。清代《黑龙江外纪》里对此有过详细的记载：“上元，城中张灯五夜，" +
                "村落妇女来观剧者，车声彻夜不绝。有镂五六尺冰为寿星灯者，中燃双炬，望之如水晶人。”";
        final String title2 = "中国的汉字";
        final String data2 = "在世界的文字之林中，中国的汉字确乎是异乎寻常的。" +
                "它的创造契机显示出中国人与世不同的文明传统和感知世界的方式。" +
                "但它是强有力的、自成系统的，它用一个个方块字培育了五千年古老的文化，" +
                "维系了一个统一的大国的存在，不管这块东方的土地上有多少种不同的语言，" +
                "讲着多少互相听不懂的方言，但这汉字的魅力却成了交响乐队的总指挥！面对着科学的飞跃，" +
                "人们在慨叹中国技术的落后，想在困惑中寻求摆脱这种象形文字带来的同世界的阻隔，" +
                "因而发出了实行汉字拼音化的震撼灵魂的呐喊。是的，这种呼唤曾经搅动得热血沸腾，" +
                "但却有点唐吉诃德攻打风车的憨态。中国的汉字以其瑰丽雄健的生命力证明了自己的存在价值。" +
                "是电脑接受了汉字，而不是电脑改变了汉字。";
        final String title3 = "听雨";
        final String data3 = "那一刻，我的心情随着雨声沸腾着，一些旧时光里的人或者事，就像雨珠一样飘飞，穿过时光，" +
                "越过天涯，轻轻地落在心上，溅起一层层涟漪。而在这涟漪的波光里，仿佛一切都生动起来，鲜活起来。若雨，" +
                "丝丝缠绵，滴滴惊心。真是应了宋代词人蒋捷那句词：“悲欢离合总无情，一任阶前点滴到天明" +
                "雨是最能渲染情绪的，不同的人听雨，就能听出不一样的感觉。不同的心境，感受也就各异。" +
                "心情好的人，听出了欢喜，细品出了轻快；忧伤的人，听出了忧愁，浅尝出了失落，寂寞。" +
                "而纵观古今，很多文人墨客都喜欢借雨抒发情怀，吟唱心灵之歌。";
        noteCRUD.addNote(title1,data1,new TimeUtil(new Date()).getTimeString());
        noteCRUD.addNote(title2,data2,new TimeUtil(new Date()).getTimeString());
        noteCRUD.addNote(title3,data3,new TimeUtil(new Date()).getTimeString());
    }

    private void initToDo(ToDoCRUD toDoCRUD){
        final String title = "待办事项";
        toDoCRUD.addTodo(title,new TimeUtil(new Date()).getTimeString(),-1);
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
                //创建客户端
                OkHttpClient client = new OkHttpClient();
                //根据用户输入的用户名和密码创建User对象并转换为json
                String jsonStr = new Gson().toJson(new User(username,password));
                //设置传输数据类型
                MediaType mediaType = MediaType.parse("application/json");
                //设置请求体
                final RequestBody requestBody = RequestBody.create(mediaType,jsonStr);
                Request request = new Request.Builder()
                        .url("http://8.129.51.177:8080/QuickNoteServlet/login")
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
                                        Toast.makeText(requireActivity(), "登陆成功", Toast.LENGTH_SHORT).show();
                                        //利用SharedPreference将已经登录信息存储到config文件中
                                        SharedPreferences.Editor editor = requireActivity().getSharedPreferences("config", requireActivity().MODE_PRIVATE).edit();
                                        editor.putInt("isLogin", 1);
                                        editor.putString("user",username);
                                        editor.putString("note_tableName","note_"+username);
                                        editor.putString("todo_tableName","todo_"+username);
                                        editor.apply();
                                        Intent intent = new Intent(requireActivity(), MainActivity.class);
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