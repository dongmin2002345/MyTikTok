package com.example.whensunset.mytiktok;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.annotation.field.Field;

public class MainActivity extends AppCompatActivity {
  
  @Field("mTextString")
  String mTextString = "mTextString";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    TestPresenter testPresenter = new TestPresenter();
    testPresenter.init(getWindow().getDecorView());
    testPresenter.bind(this);
  }
}
