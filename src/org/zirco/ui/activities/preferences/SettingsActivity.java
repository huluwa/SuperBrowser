package org.zirco.ui.activities.preferences;

import org.zirco.R;
import org.zirco.ui.view.PublicTopNavigateView;
import org.zirco.utils.LogUtils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.Toast;
import br.com.dina.ui.model.ViewItem;
import br.com.dina.ui.widget.UITableView;
import br.com.dina.ui.widget.UITableView.ClickListener;

public class SettingsActivity extends BaseSettingActivity {

	private PublicTopNavigateView mPublicTopNavigateView;
	private UITableView mTableView01;
	private UITableView mTableView02;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mPublicTopNavigateView = new PublicTopNavigateView(this, R.layout.settings_layout);
		setContentView(mPublicTopNavigateView);
		
		buildTable01();
		buildTable02();
	}
	
	private void buildTable01() {
		
		mTableView01 = (UITableView) findViewById(R.id.tableView01);
		
		mTableView01.addBasicItem("账号管理");
		LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout v1 = (RelativeLayout) mInflater.inflate(R.layout.custom_view2, null);
		ViewItem v2 = new ViewItem(v1);
		v2.setClickable(false);
		mTableView01.addViewItem(v2);
		mTableView01.addBasicItem("通用设置");
		mTableView01.addBasicItem("高级设置");
		
		ClickListener listener = new ClickListener() {
			@Override
			public void onClick(int index) {
				
				switch (index) {
				case 0:
					break;
					
				case 1:
					break;
					
				case 2:
					Intent i2 = new Intent(SettingsActivity.this, CommonSettingsActivity.class);
					startActivity(i2);
					break;
					
				case 3:
					Intent i3 = new Intent(SettingsActivity.this, PrimeSettingsActivity.class);
					startActivity(i3);
					break;
				}
					
				Toast.makeText(SettingsActivity.this, "item clicked: " + index,
						Toast.LENGTH_SHORT).show();
			}
		};
		mTableView01.setClickListener(listener);
		mTableView01.commit();
	}
	
	
	private void buildTable02() {
		mTableView02 = (UITableView) findViewById(R.id.tableView02);
		
		mTableView02.addBasicItem("清理缓存", "xxM");
		
		LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout v1 = (RelativeLayout) mInflater.inflate(R.layout.custom_view3, null);
		ViewItem v3 = new ViewItem(v1);
		v3.setClickable(false);
		mTableView02.addViewItem(v3);
		
		mTableView02.addBasicItem("欢迎界面");
		mTableView02.addBasicItem("推荐给好友");
		mTableView02.addBasicItem("意见反馈");
		mTableView02.addBasicItem("关于五鱼");
		
		ClickListener listener = new ClickListener() {
			@Override
			public void onClick(int index) {
				Toast.makeText(SettingsActivity.this, "item clicked: " + index,
						Toast.LENGTH_SHORT).show();
			}
		};
		mTableView02.setClickListener(listener);
		mTableView02.commit();
	}
}
