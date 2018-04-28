package com.santoshdevadiga.kiosksample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SecondAcitivity extends AppCompatActivity {

    private Button btn_call_third_activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_acitivity);
        btn_call_third_activity=(Button) findViewById(R.id.btn_call_third_activity);
        btn_call_third_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SecondAcitivity.this,ThirdActivity.class);
                startActivity(intent);
            }
        });
    }
}
