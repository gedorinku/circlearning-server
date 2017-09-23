package com.example.owner.myapplicationipracticenowin201709;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.kurume_nct.studybattle.R;


public class LotteryActivity extends Activity{

    ImageView card1;
    ImageView card2;
    ImageView card3;
    ImageView item;
    final int item_sum=4;
    TextView result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery);

        Resources resources = getResources();

        card1 = (ImageView) findViewById(R.id.card1);
        card2 = (ImageView) findViewById(R.id.card2);
        card3 = (ImageView) findViewById(R.id.card3);
        item = (ImageView) findViewById(R.id.result_card);
        result=(TextView)findViewById(R.id.result_text);


        Bitmap bmp1= BitmapFactory.decodeResource(getResources(),R.drawable.lotterycard);
        card1.setImageBitmap(bmp1);
        Bitmap bmp2= BitmapFactory.decodeResource(getResources(),R.drawable.lotterycard);
        card2.setImageBitmap(bmp2);
        Bitmap bmp3= BitmapFactory.decodeResource(getResources(),R.drawable.lotterycard);
        card3.setImageBitmap(bmp3);

        item.setVisibility(View.INVISIBLE);
        card1.setVisibility(View.VISIBLE);
        card2.setVisibility(View.VISIBLE);
        card3.setVisibility(View.VISIBLE);

        card1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mainToReturn();
            }
        });
        card2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mainToReturn();
            }
        });

        card3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mainToReturn();
            }
        });
    }




    void mainToReturn(){
        //もし表示されてないなら表示する
        if (item.getVisibility() != View.VISIBLE) {
            TypedArray typedArray = getResources().obtainTypedArray(R.array.item);
            int i = (int) (Math.floor(Math.random() * item_sum));
            Drawable drawable = typedArray.getDrawable(i);
            item.setImageDrawable(drawable);
            item.setVisibility(View.VISIBLE);
            if(i==0)result.setText("爆弾GET！");
            else if(i==1)result.setText("2倍カードGET!");
            else if(i==2)result.setText("マジックハンドGET！");
            else result.setText("シールドGET!");

            final Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                   // Intent intent=new Intent(getApplication(),MainActivity.class);
                    //startActivity(intent);
                }
            },2500);
        }


    }

}



