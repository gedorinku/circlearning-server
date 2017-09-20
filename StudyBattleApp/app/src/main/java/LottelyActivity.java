package com.example.owner.myapplicationipracticenowin201709;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


public class LottelyActivity extends Activity{

    ImageView card1;
    ImageView card2;
    ImageView card3;
    ImageView item;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery);

        Resources resources=getResources();

        card1=(ImageView)findViewById(R.id.card1);
        card2=(ImageView)findViewById(R.id.card2);
        card3=(ImageView)findViewById(R.id.card3);
        item=(ImageView)findViewById(R.id.result_card);

        item.setVisibility(View.VISIBLE);


    }

    public void onClick(ImageView v){
        if(v==card1||v==card2||v==card3){
            TypedArray typedArray = getResources().obtainTypedArray(R.array.item);
            int i =(int)(Math.floor(Math.random()*3));
            Drawable drawable =typedArray.getDrawable(i);
            item.setImageDrawable(drawable);
            item.setVisibility(View.GONE);
        }
    }

}
