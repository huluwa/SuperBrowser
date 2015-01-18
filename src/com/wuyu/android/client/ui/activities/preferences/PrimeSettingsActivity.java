package com.wuyu.android.client.ui.activities.preferences;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import br.com.dina.ui.model.BasicItem;
import br.com.dina.ui.model.ViewItem;
import br.com.dina.ui.widget.UITableView;
import br.com.dina.ui.widget.UITableView.ClickListener;

import com.wuyu.android.client.R;
import com.wuyu.android.client.ui.view.PublicTopNavigateView;

/**
 * 高级设置
 * @author chenyueguo
 *
 */
public class PrimeSettingsActivity extends BaseSettingActivity {

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
		
		mTableView01.addBasicItem("用户代理", "设置用户代理");
		
		LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout v1 = (RelativeLayout) mInflater.inflate(R.layout.custom_view3, null);
		((TextView)v1.findViewById(R.id.title)).setText("启用JavaScript");
		ViewItem v2 = new ViewItem(v1);
		mTableView01.addViewItem(v2);
		
		RelativeLayout v12 = (RelativeLayout) mInflater.inflate(R.layout.custom_view3, null);
		((TextView)v12.findViewById(R.id.title)).setText("加载图像");
		ViewItem v22 = new ViewItem(v12);
		mTableView01.addViewItem(v22);
		
		
		RelativeLayout v13 = (RelativeLayout) mInflater.inflate(R.layout.custom_view3, null);
		((TextView)v13.findViewById(R.id.title)).setText("启用插件");
		ViewItem v23 = new ViewItem(v13);
		mTableView01.addViewItem(v23);
		
		mTableView01.addBasicItem("白名单", "管理广告阻止白名单");
		mTableView01.addBasicItem("书签/历史记录");
		mTableView01.addBasicItem("Example 3", "Summary text 3");
		mTableView01.addBasicItem(new BasicItem("Disabled item", "this is a disabled item", false));
		
		ClickListener listener = new ClickListener() {
			@Override
			public void onClick(int index) {
				Toast.makeText(PrimeSettingsActivity.this, "item clicked: " + index,
						Toast.LENGTH_SHORT).show();
			}
		};
		mTableView01.setClickListener(listener);
		mTableView01.commit();
	}
	
	
	private void buildTable02() {
		mTableView02 = (UITableView) findViewById(R.id.tableView02);
		
		LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout v1 = (RelativeLayout) mInflater.inflate(R.layout.custom_view3, null);
		ViewItem v3 = new ViewItem(v1);
		v3.setClickable(false);
		mTableView02.addViewItem(v3);
		
		mTableView02.addBasicItem("关于五鱼");
		mTableView02.addBasicItem("退出");
		
		ClickListener listener = new ClickListener() {
			@Override
			public void onClick(int index) {
				Toast.makeText(PrimeSettingsActivity.this, "item clicked: " + index,
						Toast.LENGTH_SHORT).show();
			}
		};
		mTableView02.setClickListener(listener);
		mTableView02.commit();
		
	}
}
