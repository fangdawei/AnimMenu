package com.david.animmenu.widget;

import android.view.View;
import android.widget.PopupWindow;

/**
 * Created by david on 2017/3/9.
 */

public class DismissListenPopupWindow extends PopupWindow {

  private DismissListener dismissListener;

  public DismissListenPopupWindow(View contentView, int width, int height, boolean focusable) {
    super(contentView, width, height, focusable);
  }

  @Override public void dismiss() {
    if(dismissListener == null || !dismissListener.preDismiss()){
      super.dismiss();
      if(dismissListener != null){
        dismissListener.afterDismiss();
      }
    }
  }

  public void setDismissListener(DismissListener listener) {
    this.dismissListener = listener;
  }

  interface DismissListener {
    boolean preDismiss();//返回true中断dismiss，返回false执行dismiss
    void afterDismiss();
  }
}
