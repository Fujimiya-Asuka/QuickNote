package com.asuka.quicknote.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.asuka.quicknote.R;
import com.asuka.quicknote.domain.Note;
import com.asuka.quicknote.utils.NetWorkUtil;
import com.asuka.quicknote.utils.db.NoteCRUD;
import com.asuka.quicknote.utils.TimeUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class NoteEditActivity extends AppCompatActivity {
    private final String TAG = "NoteEditActivity";
    public static final int CHOSE_PHOTO = 1;
    private final Context mContext = NoteEditActivity.this;
    private EditText note_title_edit, note_data_edit;
    private String oldNoteTitle, oldNoteData,imagePath;
    private long noteID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);
        Toolbar toolbar = findViewById(R.id.toolbar_edit);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        //在toolbar上显示返回按钮
        if(actionBar!=null){

            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        }

        note_title_edit = findViewById(R.id.todo_title_edit);
        note_data_edit = findViewById(R.id.todo_data_edit);

        Intent intent = getIntent();
        //获取noteID
        noteID = intent.getLongExtra("Note_id", 0);
        //noteID有效，展示数据
        if (noteID != 0) {
            Note note = new NoteCRUD(mContext).getNote(noteID);
            note_title_edit.setText(note.getTitle());
            oldNoteTitle = note.getTitle();
            note_data_edit.setText(note.getData());
            oldNoteData = note.getData();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "code"+requestCode+"==="+resultCode);
        switch (requestCode){
            case CHOSE_PHOTO : {
                if (resultCode==RESULT_OK){
                    if(Build.VERSION.SDK_INT>=19){
                        handleImageOnKitkat(data);
                    }
                }
            }
        }
    }

    //加载toolbar的菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_note_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
                //返回按钮
            case android.R.id.home:
                gotoMainActivity();
                break;
                //提交按钮
            case R.id.submit_toolbarBtn_noteEdit :
                addOrUpdateNote();
                break;
                //删除按钮
            case R.id.delete_toolbarBtn_noteEdit :
                NoteCRUD noteCRUD = new NoteCRUD(mContext);
                if (noteID>0){
                    noteCRUD.deleteNote(noteID);
                    gotoMainActivity();
                }else {
                    Toast.makeText(mContext,"不能删除不存在的标签哦",Toast.LENGTH_SHORT).show();
                }
                break;
                //选择图片按钮
            case R.id.chose_photoBtn_noteEdit :
                Log.d(TAG, "chose_photoBtn_noteEdit ");
                //检查存储卡权限
                if(ContextCompat.checkSelfPermission(NoteEditActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(NoteEditActivity.this,new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE },1);
                }else {
                    openAlbum();
                }
                break;
            default:
                break;
        }
        return true;
    }

    private void addOrUpdateNote() {
        String newNoteTitle = note_title_edit.getText().toString();
        String newNoteData = note_data_edit.getText().toString();
        String time = new TimeUtil(new Date()).getTimeString();
        NoteCRUD noteCRUD = new NoteCRUD(mContext);
        //是已存在的便签
        if (noteID > 0) {
            // 输入框发生了改变，执行更新操作
            if (!newNoteTitle.equals(oldNoteTitle) || !newNoteData.equals(oldNoteData)) {
                noteCRUD.upDateNote(noteID, newNoteTitle, newNoteData, "图片名", time);
                //发送网络请求上传
                new NetWorkUtil(getApplicationContext()).uploadNote(new NoteCRUD(getApplicationContext()).getNote(noteID)).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        int resultCode = Integer.parseInt(response.header("resultCode"));
                        int noteID = Integer.parseInt(response.header("noteID"));
                        int modify = Integer.parseInt(response.header("modify"));
                        Log.d(TAG, "resultCode："+resultCode);
                        Log.d(TAG, "noteID："+noteID);
                        Log.d(TAG, "modify："+modify);
                        if (resultCode==1){
                            new NoteCRUD(getApplicationContext()).updateNoteStateModify(noteID,2,modify);
                        }
                    }
                });
                gotoMainActivity();
            }else{
                gotoMainActivity();
            }
        }
        //是新的便签
         else if (noteID==0) {
            //如果输入标题为空
            if ("".equals(newNoteTitle)) {
                Toast.makeText(NoteEditActivity.this, "标题不能为空", Toast.LENGTH_SHORT).show();
            }
            //输入框不为空，执行添加操作
            else {
                if (imagePath!=null){
                    noteID = noteCRUD.addNote(newNoteTitle, newNoteData, "1", time);
                }else {
                    noteID = noteCRUD.addNote(newNoteTitle, newNoteData, "0", time);
                }
                //发送网络请求上传
                new NetWorkUtil(getApplicationContext()).uploadNote(new NoteCRUD(getApplicationContext()).getNote(noteID)).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        // todo 此处可能会获取不到信息，要try
                        int resultCode = Integer.parseInt(response.header("resultCode"));
                        int noteID = Integer.parseInt(response.header("noteID"));
                        int modify = Integer.parseInt(response.header("modify"));
                        Log.d(TAG, "resultCode："+resultCode);
                        Log.d(TAG, "noteID："+noteID);
                        Log.d(TAG, "modify："+modify);
                        if (resultCode==1){
                            new NoteCRUD(getApplicationContext()).updateNoteStateModify(noteID,2,modify);
                        }
                    }
                });
                if (imagePath!=null){
                    //上传图片
                    new NetWorkUtil(getApplicationContext()).uploadImage((int)noteID,imagePath).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {

                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                        }
                    });
                }

                gotoMainActivity();
            }
        }

    }

    //打开相册
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOSE_PHOTO);
    }

    @TargetApi(19)
    private void handleImageOnKitkat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this,uri)){
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID+"="+ id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            imagePath = getImagePath(uri,null);
        }else if ("file".equalsIgnoreCase(uri.getScheme())){
            imagePath = uri.getPath();
        }
        Log.d(TAG, "handleImageOnKitkat: "+imagePath);
        this.imagePath=imagePath;
    }

    private String getImagePath(Uri uri,String selection){
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor!=null){
            if (cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void gotoMainActivity(){
        Intent intent = new Intent(mContext, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
