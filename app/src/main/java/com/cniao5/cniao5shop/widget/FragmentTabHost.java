package com.cniao5.cniao5shop.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;

import java.util.ArrayList;
/**
 * 功能描述：修改过的FragmentTabHost,保存fragment实例不销毁
 */
public class FragmentTabHost extends TabHost implements
        TabHost.OnTabChangeListener {
    private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
    private FrameLayout mRealTabContent;//用来设置TabContent，用android.R.id.tabcontent来标识
    private Context mContext;
    private FragmentManager mFragmentManager;//fragment管理器
    private int mContainerId;//TabContent的id
    private OnTabChangeListener mOnTabChangeListener;//tab切换监听
    private TabInfo mLastTab;//tab类的封装
    private boolean mAttached;//选项卡是否被选中

    static final class TabInfo {
        private final String tag;
        private final Class<?> clss;
        private final Bundle args;
        private Fragment fragment;

        TabInfo(String _tag, Class<?> _class, Bundle _args) {
            tag = _tag;
            clss = _class;
            args = _args;
        }
    }

    /**
     * TabContent数据初始化，当选项卡被选择时调用
     */
    static class DummyTabFactory implements TabContentFactory {
        private final Context mContext;

        public DummyTabFactory(Context context) {
            mContext = context;
        }

        /**
         * 用tag来标识一个context，在实现类里可以使用LayoutInflater填充出来。
         *
         * @param tag
         * @return
         */
        @Override
        public View createTabContent(String tag) {
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }
    }

    /**
     * BaseSavedState是View的一个静态内部类，把控件的属性打包到parcel容器，
     * Activity的onSaveInstanceState、onRestoreInstanceState最终也会调用到控件的这两个同名方法。
     */
    static class SavedState extends BaseSavedState {
        String curTab;

        /**
         * 当类创建实例的时候调用
         */
        SavedState(Parcelable superState) {
            super(superState);
        }

        /**
         * 当读取parcel时调用，Parcel是一个容器，Android系统中的binder进程间通信(IPC)就使用了Parcel类来进行
         *
         * @param in
         */
        private SavedState(Parcel in) {
            super(in);
            curTab = in.readString();
        }

        /**
         * 写入接口函数，打包
         *
         * @param out
         * @param flags
         */
        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeString(curTab);
        }


        @Override
        public String toString() {
            return "FragmentTabHost.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " curTab=" + curTab + "}";
        }

        /**
         * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例出来。
         * 因为实现类在这里还是不可知的，所以需要用到模板的方法名通过模板参数传入
         * 为了实现模板参数的传入，这里定义了Creator嵌入接口，内含两个接口函数分别返回单个和多个继承类实例
         */
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }


    public FragmentTabHost(Context context) {
        // Note that we call through to the version that takes an AttributeSet,
        // because the simple Context construct can result in a broken object!
        super(context, null);
        initFragmentTabHost(context, null);
    }

    public FragmentTabHost(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFragmentTabHost(context, attrs);
    }

    /**
     * obtainStyledAttributes:返回一个设计样式属性包含了set里面的attrs参数
     * set：现在检索的属性值；
     * attrs：制定的检索的属性值
     * defStyleAttr：指向当前theme某个item描述的style 该style指定了一些默认值为这个TypedArray；
     * defStyleRes:defStyleRes找不到或者为0，可以直接指定某个style
     *
     * @param context
     * @param attrs
     */
    private void initFragmentTabHost(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                new int[]{android.R.attr.inflatedId}, 0, 0);
        mContainerId = a.getResourceId(0, 0);
        a.recycle();

        super.setOnTabChangedListener(this);
    }

    /**
     * 判断是否设置tab显示的布局
     *
     * @param context
     */
    private void ensureHierarchy(Context context) {
        // If owner hasn'mDatas made its own view hierarchy, then as a convenience
        // we will construct a standard one here.
        if (findViewById(android.R.id.tabs) == null) {
            //设置布局
            LinearLayout ll = new LinearLayout(context);
            ll.setOrientation(LinearLayout.VERTICAL);
            addView(ll, new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            //添加tab样式
            TabWidget tw = new TabWidget(context);
            tw.setId(android.R.id.tabs);
            tw.setOrientation(TabWidget.HORIZONTAL);
            ll.addView(tw, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 0));

            //设置fragmentLayout样式
            FrameLayout fl = new FrameLayout(context);
            fl.setId(android.R.id.tabcontent);
            ll.addView(fl, new LinearLayout.LayoutParams(0, 0, 0));

            mRealTabContent = fl = new FrameLayout(context);
            mRealTabContent.setId(mContainerId);
            ll.addView(fl, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
        }
    }

    /**
     * @deprecated Don'mDatas call the original TabHost setup, you must instead call
     * {@link #setup(Context, FragmentManager)} or
     * {@link #setup(Context, FragmentManager, int)}.
     */
    @Override
    @Deprecated
    public void setup() {
        throw new IllegalStateException(
                "Must call setup() that takes a Context and FragmentManager");
    }

    /**
     * 加载tab布局
     *
     * @param context
     * @param manager
     */
    public void setup(Context context, FragmentManager manager) {
        ensureHierarchy(context); // Ensure views required by super.setup()
        super.setup();
        mContext = context;
        mFragmentManager = manager;
        ensureContent();
    }

    public void setup(Context context, FragmentManager manager, int containerId) {
        ensureHierarchy(context); // Ensure views required by super.setup()
        super.setup();
        mContext = context;
        mFragmentManager = manager;
        mContainerId = containerId;
        ensureContent();
        mRealTabContent.setId(containerId);

        // We must have an ID to be able to save/restore our state. If
        // the owner hasn'mDatas set one at this point, we will set it ourself.
        if (getId() == View.NO_ID) {
            setId(android.R.id.tabhost);
        }
    }

    /**
     * 判断是否设置tab显示的样式
     */
    private void ensureContent() {
        if (mRealTabContent == null) {
            mRealTabContent = (FrameLayout) findViewById(mContainerId);
            if (mRealTabContent == null) {
                throw new IllegalStateException(
                        "No tab content FrameLayout found for id "
                                + mContainerId);
            }
        }
    }

    /**
     * tab切换监听
     *
     * @param l
     */
    @Override
    public void setOnTabChangedListener(OnTabChangeListener l) {
        mOnTabChangeListener = l;
    }

    /**
     * 添加tab选项卡
     *
     * @param tabSpec 选项卡的indicator，content，tag数据封装，用来跟踪选项卡
     * @param clss    选项卡显示的类
     * @param args    数据传递
     */
    public void addTab(TabSpec tabSpec, Class<?> clss, Bundle args) {
        tabSpec.setContent(new DummyTabFactory(mContext));
        String tag = tabSpec.getTag();

        TabInfo info = new TabInfo(tag, clss, args);

        if (mAttached) {
            // If we are already attached to the window, then check to make
            // sure this tab's fragment is inactive if it exists. This shouldn'mDatas
            // normally happen.
            info.fragment = mFragmentManager.findFragmentByTag(tag);
            if (info.fragment != null && !info.fragment.isDetached()) {
                FragmentTransaction ft = mFragmentManager.beginTransaction();
//               ft.detach(info.fragment);
                ft.hide(info.fragment);
                ft.commit();
            }
        }

        mTabs.add(info);
        addTab(tabSpec);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        String currentTab = getCurrentTabTag();//获取当前tab

        // Go through all tabs and make sure their fragments match
        // the correct state.
        FragmentTransaction ft = null;
        for (int i = 0; i < mTabs.size(); i++) {
            TabInfo tab = mTabs.get(i);
            tab.fragment = mFragmentManager.findFragmentByTag(tab.tag);
//          if (tab.fragment != null && !tab.fragment.isDetached()) {
            if (tab.fragment != null) {
                if (tab.tag.equals(currentTab)) {
                    // The fragment for this tab is already there and
                    // active, and it is what we really want to have
                    // as the current tab. Nothing to do.
                    mLastTab = tab;//获取选中的tab
                } else {
                    // This fragment was restored in the active state,
                    // but is not the current tab. Deactivate it.
                    if (ft == null) {
                        ft = mFragmentManager.beginTransaction();
                    }
//                    ft.detach(tab.fragment);
                    ft.hide(tab.fragment);//隐藏fragment
                }
            }
        }

        // We are now ready to go. Make sure we are switched to the
        // correct tab.
        mAttached = true;
        ft = doTabChanged(currentTab, ft);//当tab被选中，提交事务
        if (ft != null) {
            ft.commitAllowingStateLoss();
            mFragmentManager.executePendingTransactions();
        }
    }

    /**
     * 当view从窗体分离时调用
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAttached = false;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);//将控件属性打包到Parcel容器
        ss.curTab = getCurrentTabTag();//初始化控件当前tab
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setCurrentTabByTag(ss.curTab);
    }

    @Override
    public void onTabChanged(String tabId) {
        if (mAttached) {
            FragmentTransaction ft = doTabChanged(tabId, null);
            if (ft != null) {
                ft.commit();
            }
        }
        if (mOnTabChangeListener != null) {
            mOnTabChangeListener.onTabChanged(tabId);
        }
    }

    /**
     * tab选中事务处理
     *
     * @param tabId
     * @param ft
     * @return
     */
    private FragmentTransaction doTabChanged(String tabId,
                                             FragmentTransaction ft) {
        TabInfo newTab = null;
        for (int i = 0; i < mTabs.size(); i++) {
            TabInfo tab = mTabs.get(i);
            if (tab.tag.equals(tabId)) {
                newTab = tab;
            }
        }
        if (newTab == null) {
            throw new IllegalStateException("No tab known for tag " + tabId);
        }
        if (mLastTab != newTab) {
            if (ft == null) {
                ft = mFragmentManager.beginTransaction();
            }
            if (mLastTab != null) {
                if (mLastTab.fragment != null) {
//                    ft.detach(mLastTab.fragment);
                    ft.hide(mLastTab.fragment);
                }
            }

            //如果tab不为空，则显示，否则获取tab数据
            if (newTab != null) {
                if (newTab.fragment == null) {
                    newTab.fragment = Fragment.instantiate(mContext,
                            newTab.clss.getName(), newTab.args);
                    ft.add(mContainerId, newTab.fragment, newTab.tag);
                } else {
//                    ft.attach(newTab.fragment);
                    ft.show(newTab.fragment);
                }
            }

            mLastTab = newTab;//获取选中的tab
        }
        return ft;
    }
}
