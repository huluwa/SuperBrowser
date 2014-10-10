package org.zirco.ui.fragment;

import android.support.v4.app.Fragment;

public abstract class BaseFragment extends Fragment {
	
	public final static int TYPE_CELL_FRAGMENT = 101; //导航页
	public final static int TYPE_WEB_FRAGMENT = 102; //网页
	
	protected int mType;
	
	public void setType(int type) {
		mType = type;
	}
	
	public int getType() {
		return mType;
	}
//
//
//    /** 根布局 */
//    protected RelativeLayout mRootLayout;
//
//    /** 内容布局 */
//    protected RelativeLayout mBodyLayout;
//    
//    protected Activity mActivity;
//
//
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        Log.i("chenyg", "onAttach()");
//        
//        mActivity = activity;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Log.i("chenyg", "onCreate()");
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//            Bundle savedInstanceState) {
//        Log.i("chenyg", "onCreateView()");
//
//        mRootLayout = (RelativeLayout) inflater.inflate(
//                R.layout.base_fragment_layout, container, false);
//
//        findView();
//
//        return mRootLayout;
//    }
//
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        Log.i("chenyg", "onViewCreated()");
//    }
//
//    private void findView() {
//        mBodyLayout = (RelativeLayout) mRootLayout
//                .findViewById(R.id.body_content);
//        
//    }
//
//    /**
//     * 设置内容布局
//     * 
//     * @param layoutid
//     *            layout布局id
//     */
//    public void setBodyContentView(int layoutid) {
//        View view = LayoutInflater.from(mActivity).inflate(layoutid, null);
//        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(
//                LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
//        mBodyLayout.addView(view, rl);
//    }

}
