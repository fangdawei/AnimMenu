package com.david.animmenu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.david.animmenu.widget.AnimationMenu;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AnimationMenu.MenuOnSelectedListener {

  private TextView tvHello;
  private AnimationMenu animMenu;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    tvHello = (TextView)findViewById(R.id.tv_hello);
    tvHello.setOnClickListener(this);
    animMenu = new AnimationMenu(this);
    List<AnimationMenu.MenuItem> items = new ArrayList<>();
    items.add(new AnimationMenu.MenuItem(this, R.drawable.icon_store_track));
    items.add(new AnimationMenu.MenuItem(this, R.drawable.icon_store_track));
    items.add(new AnimationMenu.MenuItem(this, R.drawable.icon_store_track));
    items.add(new AnimationMenu.MenuItem(this, R.drawable.icon_store_track));
    items.add(new AnimationMenu.MenuItem(this, R.drawable.icon_store_track));
    animMenu.setItemList(items);
    animMenu.setDuration(300);
    animMenu.setStep(100);
    animMenu.setMenuOnSelectedListener(this);
  }

  @Override public void onClick(View v) {
    switch (v.getId()){
      case R.id.tv_hello:
        animMenu.menuShow(tvHello.getRootView());
        break;
      default:break;
    }
  }

  @Override public void onMenuSelected(AnimationMenu.MenuItem item, int position) {
    Toast.makeText(this, position + " selected", Toast.LENGTH_SHORT).show();
  }
}
