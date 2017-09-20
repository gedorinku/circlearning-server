package com.example.owner.myapplicationipracticenowin201709;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Interpolator;
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

import java.io.FileDescriptor;
import java.io.IOException;



public class CameraModeActivity extends Activity {

    private final static int RESULT_CAMERA = 1001;
    private static final int RESULT_PICK_IMAGEFILE = 1000;

    private ImageView imageview;
    private Button libraryButton;
    private TextView dcimPath;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_camera_mode);

        dcimPath = (TextView) findViewById(R.id.text_view2);
        //ギャラリーのpathを取得する
        dcimPath.setText("ギャラリーのpath: " + getGalleryPath());

        imageview = (ImageView) findViewById((R.id.image_view));

        libraryButton = (Button) findViewById(R.id.library_button);
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


        Button cameraButton = (Button) findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, RESULT_CAMERA);
            }
        });


        Button returnButton = (Button) findViewById(R.id.return_button1);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button passButton=(Button) findViewById(R.id.pass_button);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(getApplication(),LottelyActivity.class);
                startActivity(intent1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_CAMERA) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imageview.setImageBitmap(bitmap);
        }else if (requestCode == RESULT_PICK_IMAGEFILE && resultCode == Activity.RESULT_OK) {
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
    }

    //ギャラリーpath取得関数
    private String getGalleryPath() {
        return Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM + "/";
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    }

