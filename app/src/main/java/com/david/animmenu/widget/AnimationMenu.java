package com.david.animmenu.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by david on 2017/3/8.
 */

public class AnimationMenu implements View.OnClickListener {

  private List<MenuItem> menuItemList = new ArrayList<>();
  private boolean toRefresh;
  private boolean canDismiss;
  private DismissListenPopupWindow popupWindow;
  private Context context;
  private int duration = 500;
  private int step = 150;
  private Interpolator interpolator = new LinearInterpolator();
  private MenuOnSelectedListener menuOnSelectedListener;
  private int background = 0x88000000;
  private boolean isAnimationPlaying = false;

  public AnimationMenu(Context context) {
    this.context = context;
    this.toRefresh = true;
  }

  public void setItemList(List<MenuItem> items) {
    menuItemList.clear();
    menuItemList.addAll(items);
    toRefresh = true;
    canDismiss = false;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  public void setStep(int step) {
    this.step = step;
  }

  public void setInterpolator(Interpolator interpolator) {
    this.interpolator = interpolator;
  }

  public void setMenuOnSelectedListener(MenuOnSelectedListener menuOnSelectedListener) {
    this.menuOnSelectedListener = menuOnSelectedListener;
  }

  public void setBackground(int background) {
    this.background = background;
  }

  private void createView() {
    int itemCount = menuItemList.size();
    RelativeLayout root = new RelativeLayout(context);
    Resources res = context.getResources();
    int screenWidth = res.getDisplayMetrics().widthPixels;
    int width = itemCount != 0 ? screenWidth / itemCount : screenWidth;
    View leftView = null;
    for (int i = 0; i < menuItemList.size(); i++) {
      MenuItem item = menuItemList.get(i);
      item.contentView.setTag(i);
      item.contentView.setAlpha(0f);
      item.contentView.setOnClickListener(this);
      final RelativeLayout itemContainer = item.root;
      root.addView(itemContainer);
      RelativeLayout.LayoutParams paramsBox = (RelativeLayout.LayoutParams) itemContainer.getLayoutParams();
      paramsBox.width = width;
      paramsBox.height = ViewGroup.LayoutParams.WRAP_CONTENT;
      paramsBox.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
      if (leftView != null) {
        paramsBox.addRule(RelativeLayout.RIGHT_OF, leftView.getId());
      } else {
        paramsBox.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
      }
      itemContainer.setLayoutParams(paramsBox);
      leftView = itemContainer;
      //等到root的高度确定，设置其BottomMargin，使其隐藏
      itemContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override public void onGlobalLayout() {
          int height = itemContainer.getHeight();
          RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) itemContainer.getLayoutParams();
          params.bottomMargin = -height;
          itemContainer.setLayoutParams(params);
          if (Build.VERSION.SDK_INT < 16) {
            itemContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
          } else {
            itemContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
          }
        }
      });
    }
    popupWindow = new DismissListenPopupWindow(root, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, true);
    popupWindow.setFocusable(true);
    popupWindow.setOutsideTouchable(true);
    popupWindow.setBackgroundDrawable(new ColorDrawable(background));
    popupWindow.setDismissListener(new DismissListenPopupWindow.DismissListener() {
      @Override public boolean preDismiss() {
        if (!canDismiss) {//动画未播放，暂时不Dismiss
          menuHide();
          return true;
        } else {//动画已经播放完成，Dismiss
          return false;
        }
      }

      @Override public void afterDismiss() {

      }
    });
  }

  public void menuShow(View location) {
    if (isAnimationPlaying) {
      return;
    }
    if (toRefresh) {//重新布局视图
      createView();
      toRefresh = false;
    }
    popupWindow.showAtLocation(location, Gravity.NO_GRAVITY, 0, 0);
    isAnimationPlaying = true;
    for (int i = 0; i < menuItemList.size(); i++) {
      MenuItem item = menuItemList.get(i);
      if (i + 1 == menuItemList.size()) {
        animShowMenuItem(item, i * step, duration, new Animator.AnimatorListener() {
          @Override public void onAnimationStart(Animator animation) {

          }

          @Override public void onAnimationEnd(Animator animation) {
            isAnimationPlaying = false;
          }

          @Override public void onAnimationCancel(Animator animation) {

          }

          @Override public void onAnimationRepeat(Animator animation) {

          }
        });
      } else {
        animShowMenuItem(item, i * step, duration);
      }
    }
    canDismiss = false;
  }

  private void animShowMenuItem(final MenuItem item, long timeDelay, int dur) {
    animShowMenuItem(item, timeDelay, dur, null);
  }

  private void animShowMenuItem(final MenuItem item, long timeDelay, int dur, Animator.AnimatorListener listener) {
    ObjectAnimator oaContent = ObjectAnimator.ofFloat(item.contentView, "alpha", 0f, 1f);
    float current = item.root.getTranslationY();
    float screenHeight = context.getResources().getDisplayMetrics().heightPixels;
    ObjectAnimator oaRoot = ObjectAnimator.ofFloat(item.root, "translationY", current, current - screenHeight / 2);
    AnimatorSet set = new AnimatorSet();
    set.playTogether(oaContent, oaRoot);
    if (listener != null) {
      set.addListener(listener);
    }
    set.setDuration(dur);
    set.setInterpolator(interpolator);
    set.setStartDelay(timeDelay);
    set.start();
  }

  public void menuHide() {
    if (isAnimationPlaying) {
      return;
    }
    isAnimationPlaying = true;
    for (int i = 0; i < menuItemList.size(); i++) {
      MenuItem item = menuItemList.get(i);
      if (i == 0) {
        animHideMenuItem(item, (menuItemList.size() - i) * step, duration, new Animator.AnimatorListener() {
          @Override public void onAnimationStart(Animator animation) {

          }

          @Override public void onAnimationEnd(Animator animation) {
            canDismiss = true;
            isAnimationPlaying = false;
            popupWindow.dismiss();
          }

          @Override public void onAnimationCancel(Animator animation) {

          }

          @Override public void onAnimationRepeat(Animator animation) {

          }
        });
      } else {
        animHideMenuItem(item, (menuItemList.size() - i) * step, duration);
      }
    }
  }

  private void animHideMenuItem(final MenuItem item, long timeDelay, int dur) {
    animHideMenuItem(item, timeDelay, dur, null);
  }

  private void animHideMenuItem(final MenuItem item, long timeDelay, int dur, Animator.AnimatorListener listener) {
    ObjectAnimator oaContent = ObjectAnimator.ofFloat(item.contentView, "alpha", 1f, 0f);
    float current = item.root.getTranslationY();
    float screenHeight = context.getResources().getDisplayMetrics().heightPixels;
    ObjectAnimator oaRoot = ObjectAnimator.ofFloat(item.root, "translationY", current, current + screenHeight / 2);
    AnimatorSet set = new AnimatorSet();
    set.playTogether(oaContent, oaRoot);
    if (listener != null) {
      set.addListener(listener);
    }
    set.setDuration(dur);
    set.setInterpolator(interpolator);
    set.setStartDelay(timeDelay);
    set.start();
  }

  @Override public void onClick(View v) {
    Integer index = (Integer) v.getTag();
    if (index != null) {
      MenuItem item = menuItemList.get(index);
      if (menuOnSelectedListener != null) {
        menuOnSelectedListener.onMenuSelected(item, index);
      }
      popupWindow.dismiss();
    }
  }

  public static class MenuItem {

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    private View contentView;
    private RelativeLayout root;

    public MenuItem(Context context, int imageResourceId) {
      ImageView imageView = new ImageView(context);
      imageView.setImageResource(imageResourceId);
      root = new RelativeLayout(context);
      root.setId(MenuItem.generateViewId());
      contentView = imageView;
      root.addView(contentView);
      RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) contentView.getLayoutParams();
      params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
      params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
      params.addRule(RelativeLayout.CENTER_IN_PARENT);
      contentView.setLayoutParams(params);
    }

    /**
     * 动态生成View ID
     */
    public static int generateViewId() {
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
        for (; ; ) {
          final int result = sNextGeneratedId.get();
          // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
          int newValue = result + 1;
          if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
          if (sNextGeneratedId.compareAndSet(result, newValue)) {
            return result;
          }
        }
      } else {
        return View.generateViewId();
      }
    }
  }

  public interface MenuOnSelectedListener {
    void onMenuSelected(MenuItem item, int position);
  }
}
