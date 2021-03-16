package com.asuka.quicknote.fragment;

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

import com.asuka.quicknote.activity.MainActivity;
import com.asuka.quicknote.R;
import com.asuka.quicknote.db.AccountDatabaseHelper;
import com.asuka.quicknote.db.NoteCRUD;
import com.asuka.quicknote.myClass.Time;

import java.util.Date;

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

        Button login_btn = fragmentActivity.findViewById(R.id.sign_in_btn);
        //登录页面的账户输入框
        final EditText account_Edit = fragmentActivity.findViewById(R.id.account_edit_sign_in);
        //登录页面的密码输入框
        final EditText password_Edit = fragmentActivity.findViewById(R.id.password_edit_sign_in);

        account_dbHelper = new AccountDatabaseHelper(fragmentActivity, "Account.db", null, 1);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db = account_dbHelper.getWritableDatabase();
                String search = account_Edit.getText().toString();
                String target = password_Edit.getText().toString();
//                判断输入框是否为空
                if ("".equals(search) || "".equals(target)) {
                    Toast.makeText(fragmentActivity, "账号密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT password FROM account WHERE account=?", new String[]{search});
                    cursor.getCount();
                    if (cursor.getCount() == 0) {
                        Toast.makeText(fragmentActivity, "账号不存在", Toast.LENGTH_SHORT).show();
                    } else {
                        cursor.moveToNext();
                        String account = cursor.getString(cursor.getColumnIndex("password"));
                        if (account.equals(target)) {
                            Toast.makeText(fragmentActivity, "登陆成功", Toast.LENGTH_SHORT).show();
                            //利用SharedPreference将已经登录信息存储到config文件中
                            SharedPreferences.Editor editor = fragmentActivity.getSharedPreferences("config", Context.MODE_PRIVATE).edit();
                            editor.putInt("isLogin", 1);
                            editor.apply();

                            //测试代码
                            NoteCRUD testCURD = new NoteCRUD(fragmentActivity);
                            for (int i = 0; i < 2; i++) {
                                initNotes(testCURD);
                            }
                            testCURD.closeDB();

                            Intent intent = new Intent(fragmentActivity, MainActivity.class);
                            startActivity(intent);
                            fragmentActivity.finish();
                        } else {
                            Toast.makeText(fragmentActivity, "密码错误", Toast.LENGTH_SHORT).show();
                        }
                        db.close();
                    }

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
        noteCRUD.addNote(title1,data1,new Time(new Date()).getTime());
        noteCRUD.addNote(title2,data2,new Time(new Date()).getTime());
        noteCRUD.addNote(title3,data3,new Time(new Date()).getTime());
    }

}