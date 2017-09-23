package com.example.owner.myapplicationipracticenowin201709;

import com.kurume_nct.studybattle.R;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.ImageView;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.TextView;
import android.os.Handler;
import java.io.FileDescriptor;
import java.io.IOException;



public class CameraModeActivity extends Activity {

    private final static int RESULT_CAMERA = 1001;
    private static final int RESULT_PICK_IMAGEFILE = 1000;

    private ImageView imageview;
    private Button libraryButton;
    private TextView commnent_;
    private ImageView shock;
    private int flag=0;


    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_camera_mode);
        // 宣言
        commnent_ = (TextView) findViewById(R.id.comment);
        //commnent_.setText("ギャラリーのpath: " + getGalleryPath());//ギャラリーのpathを取得する
        imageview = (ImageView) findViewById((R.id.answer));//解答の写真
        libraryButton = (Button) findViewById(R.id.library_button);
        final Button submissionButton=(Button) findViewById(R.id.submission_button);
        final Button returnButton = (Button) findViewById(R.id.return_button1);
        Button passButton=(Button) findViewById(R.id.pass_button);
        shock=(ImageView) findViewById(R.id.shock_character);

        shock.setVisibility(View.INVISIBLE);


        libraryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ファイルを選択
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                //開けるものだけ表示
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                //イメージのみを表示するフィルタ
                intent.setType("image/*");
                startActivityForResult(intent, RESULT_PICK_IMAGEFILE);
            }
        });

        //カメラ撮影ボタン
        final Button cameraButton = (Button) findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, RESULT_CAMERA);
            }
        });

        //戻るボタン
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //パスするボタン
        passButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //他の画面に遷移しないようにする
                returnButton.setEnabled(false);
                submissionButton.setEnabled(false);
                cameraButton.setEnabled(false);
                libraryButton.setEnabled(false);

                commnent_.setText("次は頑張ろう.....");
                shock.setVisibility(View.VISIBLE);
                Bitmap bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.shock);
                shock.setImageBitmap(bitmap);


                final Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                },2500);
            }
        });

        //提出するボタン
        submissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag==1) {
                    Intent intent1 = new Intent(getApplication(), LotteryActivity.class);
                    startActivity(intent1);
                }else commnent_.setText("解答を提出してください");
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_CAMERA) {
            //カメラ撮影の処理
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imageview.setImageBitmap(bitmap);
        }else if (requestCode == RESULT_PICK_IMAGEFILE && resultCode == Activity.RESULT_OK) {
            //ギャラリーからの写真を選択
            Uri uri=null;
            if (data!= null) {
                uri= data.getData();
                Log.i("","Uri: "+uri.toString());

                try {
                    Bitmap bmp=getBitmapFromUri(uri);
                    imageview.setImageBitmap(bmp);
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        flag=1;
        commnent_.setText("解答を提出してね");
    }

    //ギャラリーpath取得関数
    private String getGalleryPath() {
        return Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM + "/";
    }

    //画像を選んで取得して返す？（多分）
    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    }

