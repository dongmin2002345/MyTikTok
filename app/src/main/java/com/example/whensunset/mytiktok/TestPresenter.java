package com.example.whensunset.mytiktok;

import com.example.annotation.inject.Inject;
import com.example.mvps.BasePresenter;

/**
 * Created by whensunset on 2018/8/6.
 */

public class TestPresenter extends BasePresenter {
  
  @Inject("mTextString")
  String mTextString;
  
}
