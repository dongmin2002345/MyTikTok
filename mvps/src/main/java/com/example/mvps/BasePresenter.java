package com.example.mvps;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.view.View;

import com.example.annotation.inject.Injector;
import com.example.annotation.inject.Injectors;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by whensunset on 2018/7/28.
 */

public class BasePresenter implements Presenter {
  
  private View mRootView;
  private List<Presenter> mChildPresenterList = new ArrayList<>();
  private boolean isValid = true;
  private boolean isInitialized = false;
  private Injector mInjector = null;
  
  
  @Override
  public void init(View view) {
    if (isInitialized()) {
      
      throw new IllegalStateException("Presenter只能被初始化一次!");
    }
  
    try {
      mRootView = view;
      
      ButterKnife.bind(this, view);
    
      onInit();
    
      initChildren();
    } catch (Exception e) {
      isValid = false;
      // TODO 创建失败，之后打log或者埋点
    }
    isInitialized = true;
  }
  
  protected void onInit() {
  
  }
  
  private void initChildren() {
    for (Presenter childPresenter: mChildPresenterList) {
      childPresenter.init(mRootView);
    }
  }
  
  @Override
  public void bind(Object... callerContext) {
    if (!isValid) {
      return;
    }
    
    if (!isInitialized) {
      throw new IllegalStateException("Presenter必须先初始化!");
    }
  
    if (mInjector == null) {
      mInjector = Injectors.injector(getClass());
    }
  
    mInjector.reset(this);
  
    if (callerContext != null) {
      for (Object context : callerContext) {
        mInjector.inject(this, context);
      }
    }
    
    onBind(callerContext);
    
    bindChild(callerContext);
  }
  
  protected void onBind(Object... callerContext) {
  
  }
  
  private void bindChild(Object... callerContext) {
    for (Presenter childPresenter : mChildPresenterList) {
      childPresenter.bind(callerContext);
    }
  }
  
  @Override
  public void destroy() {
    if (!isValid) {
      return;
    }
  
    if (!isInitialized) {
      throw new IllegalStateException("Presenter必须先初始化!");
    }
    
    onDestroy();
    
    destroyChild();
  }
  
  protected void onDestroy() {
  
  }
  
  private void destroyChild() {
    for (Presenter childPresenter : mChildPresenterList) {
      childPresenter.destroy();
    }
  }
  
  @Override
  public boolean isInitialized() {
    return isInitialized;
  }
  
  @Override
  public Activity getActivity() {
    Context context = getContext();
    while (context instanceof ContextWrapper) {
      if (context instanceof Activity) {
        return (Activity) context;
      }
      context = ((ContextWrapper) context).getBaseContext();
    }
    return (Activity) getContext();
  }
  
  @Override
  public Presenter add(Presenter presenter) {
    if (presenter == null) {
      return this;
    }
    mChildPresenterList.add(presenter);
    
    if (isInitialized()) {
      presenter.init(mRootView);
    }
    return this;
  }
  
  public View getRootView() {
    return mRootView;
  }
  
  protected final Context getContext() {
    return mRootView == null ? null : mRootView.getContext();
  }
  
  protected final Resources getResources() {
    if (getContext() == null) {
      return null;
    }
    return getContext().getResources();
  }
  
  protected final String getString(int id) {
    if (getContext() == null) {
      return null;
    }
    return getContext().getString(id);
  }
}
