package org.zirco.ui.activities.preferences;

import org.zirco.R;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.Toast;
import br.com.dina.ui.model.BasicItem;
import br.com.dina.ui.model.ViewItem;
import br.com.dina.ui.widget.UITableView;
import br.com.dina.ui.widget.UITableView.ClickListener;

/**
 * 通用设置
 * @author chenyueguo
 *
 */
public class CommonSettingsActivity extends BaseSettingActivity {

	private UITableView mTableView01;
	private UITableView mTableView02;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_layout);
		
		buildTable01();
		buildTable02();
	}
	
	private void buildTable01() {
		mTableView01 = (UITableView) findViewById(R.id.tableView01);
		
		mTableView01.addBasicItem("设置主页", "设置默认主页");
		mTableView01.addBasicItem("缩放级别", "设置默认缩放级别");
		mTableView01.addBasicItem("Example 2", "Summary text 2");
		mTableView01.addBasicItem(new BasicItem("Disabled item", "this is a disabled item", false));
		mTableView01.addBasicItem("Example 3", "Summary text 3");
		mTableView01.addBasicItem(new BasicItem("Disabled item", "this is a disabled item", false));
		
		ClickListener listener = new ClickListener() {
			@Override
			public void onClick(int index) {
				Toast.makeText(CommonSettingsActivity.this, "item clicked: " + index,
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
				Toast.makeText(CommonSettingsActivity.this, "item clicked: " + index,
						Toast.LENGTH_SHORT).show();
			}
		};
		mTableView02.setClickListener(listener);
		mTableView02.commit();
	}
}
