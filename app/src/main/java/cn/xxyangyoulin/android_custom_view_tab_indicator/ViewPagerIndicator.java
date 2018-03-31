package cn.xxyangyoulin.android_custom_view_tab_indicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mnn.nnn.viewpagerzhidinyi.R;

import java.util.List;

/**
 * Created by mnn on 2016/9/5.
 *
 * mIndicator.setTabShowCount(4); //一页显示的tab数
 *mIndicator.setWordColor(0x88FFFFFF);//字体颜色
 *mIndicator.setWordSize(16);//字体大小
 *mIndicator.setTabTitles(mTitles);//tab集合
 *mViewPager.setAdapter(mAdapter);//VP设置adapter
 *mIndicator.setViewPager(mViewPager, 0);//关联VP
 *mIndicator.setOnPageChangeListener//监听vp滑动
 */

public class ViewPagerIndicator extends LinearLayout {
    private static final int COUNT_DEFINT_TAB = 4;//tab默认显示数
    private static final int WORD_SIZE_NORMAL = 18;//字体默认大小
    private static final int WORD_COLOR_NORMAL = 0x99FFFFFF;//字体颜色默认大小
    private static final int HIGHT_LIGHT_TEXT_NORMAL = 0xFFFFFFFF;//默认高亮原色
    private final int TRIANGLE_WIDTH_MAX = (int) (getScreenWidth() / 3 * PROPORTION);//三角形最大值
    private int WORD_COLOR = WORD_COLOR_NORMAL; //颜色默认
    private int mWordSize = WORD_SIZE_NORMAL;//字号默认
    private Paint mPaint;
    private Path mPath;

    private float triangleHeight;
    private float triangleWidth;

    private float positionX;
    private float positionOffsetX;

    private static final float PROPORTION = 1f / 6;

    private int mTabShowCount;//tab显示数

    private ViewPager mViewPager;

    private OnPageChangeListener mListener;

    public interface OnPageChangeListener {
        void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        void onPageSelected(int position);

        void onPageScrollStateChanged(int state);
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mListener = listener;
    }


    public ViewPagerIndicator(Context context) {
        this(context, null);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setDither(true);
        mPaint.setPathEffect(new CornerPathEffect(3));
        TypedArray array = null;
        try {
            array = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicator);
            mTabShowCount = array.getInt(R.styleable.ViewPagerIndicator_tab_count, COUNT_DEFINT_TAB);

            if (mTabShowCount < 1) {
                mTabShowCount = COUNT_DEFINT_TAB;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("fuck!");
        } finally {
            if (array != null) {
                array.recycle();
            }
        }

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        int screenWidth = getScreenWidth();

        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            LinearLayout.LayoutParams lp = (LayoutParams) view.getLayoutParams();
            lp.weight = 0;
            lp.width = screenWidth / mTabShowCount;
            view.setLayoutParams(lp);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        triangleWidth = w / mTabShowCount * PROPORTION;
        triangleWidth = Math.min(triangleWidth, TRIANGLE_WIDTH_MAX);
        triangleHeight = triangleWidth * (2 / 6f);
        positionX = w / mTabShowCount / 2f - triangleWidth / 2f + positionOffsetX;

        onItemClickEvent();//点击事件
        paintTriangle();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        canvas.save();

        canvas.translate(positionX + positionOffsetX, getHeight());
        canvas.drawPath(mPath, mPaint);

        canvas.restore();
        super.dispatchDraw(canvas);
    }

    private void paintTriangle() {
        mPath = new Path();
        mPath.moveTo(0, 0);
        mPath.lineTo(triangleWidth, 0);
        mPath.lineTo(triangleWidth / 2, -triangleHeight);
        mPath.close();
    }

    public void scroll(int position, float offset) {
        float tabWidth = getScreenWidth() / mTabShowCount;
        positionOffsetX = position * tabWidth + offset * tabWidth;

        //滚动tab
        if (mTabShowCount < getChildCount() && offset > 0 && mTabShowCount - position <= 1) {
            if (mTabShowCount != 1) {
                this.scrollTo((int) (tabWidth * (position - mTabShowCount + 1) + tabWidth * offset), 0);
            } else {
                this.scrollTo((int) (tabWidth * position + tabWidth * offset), 0);
            }
        }

        invalidate();
    }


    public void setViewPager(ViewPager viewPager, final int pos) {
        mViewPager = viewPager;
        highLightTextView(pos);
        if (mViewPager != null) {
            mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    scroll(position, positionOffset);
                    if (mListener != null) {
                        mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                    }
                }

                @Override
                public void onPageSelected(int position) {
                    highLightTextView(position);
                    if (mListener != null) {
                        mListener.onPageSelected(position);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if (mListener != null) {
                        mListener.onPageScrollStateChanged(state);
                    }
                }
            });

            mViewPager.setCurrentItem(pos);
        }
    }

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    public int getScreenWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);

        return metrics.widthPixels;
    }

    /**
     * 设置可显示的Tab数量
     *
     * @param count
     */
    public void setTabShowCount(int count) {
        if (count > 0) {
            mTabShowCount = count;
        }
    }

    /**
     * 字体大小设置
     *
     * @param size
     */
    public void setWordSize(int size) {
        mWordSize = size;
        System.out.println("set--->" + mWordSize);
    }

    public void setWordColor(int color) {
        WORD_COLOR = color;
    }

    /**
     * 设置所有的Tab
     *
     * @param list
     */
    public void setTabTitles(List<String> list) {
        if (list != null && list.size() > 0) {
            this.removeAllViews();

            for (String title : list) {
                this.addView(createTextView(title));
            }
        }
    }

    private View createTextView(String text) {
        TextView tv = new TextView(getContext());
        LinearLayout.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        lp.width = getScreenWidth() / mTabShowCount;
        tv.setGravity(Gravity.CENTER);
        tv.setText(text);
        System.out.println("---->" + mWordSize);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, mWordSize);
        tv.setTextColor(WORD_COLOR);
        tv.setLayoutParams(lp);

        return tv;
    }

    /**
     * 设置高亮
     *
     * @param pos
     */
    private void highLightTextView(int pos) {
        resetTextViewColor();
        View view = getChildAt(pos);
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(HIGHT_LIGHT_TEXT_NORMAL);
        }
    }

    /**
     * 还原颜色
     */
    private void resetTextViewColor() {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(WORD_COLOR);
            }
        }
    }

    private void onItemClickEvent() {
        for (int i = 0; i < getChildCount(); i++) {
            final int j = i;
            View view = getChildAt(i);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(j);
                }
            });
        }
    }
}

