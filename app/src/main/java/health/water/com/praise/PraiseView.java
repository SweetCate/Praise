package health.water.com.praise;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * ================================================
 * 作    者：Cate Emial:liuh@80pm.com
 * 版    本：1.0
 * 创建日期：2017/11/2.
 * 描    述：
 * 修订历史：
 * ================================================
 */

public class PraiseView extends View {

    //当前数字
    private int currentNum = 0;

    //前面不需要动的数字
    private String preNoChangeNum = "0";
    //需要动的数字
    private String oldAnimationNum = "";
    private String newAnimationNum = "0";


    private String oldNum = "";
    private String newNum = "";

    //方向 1 向上 2 向下
    private int direction = 1;
    //基准线
    private float baseLineHeight;
    private float numAnimateScale = 0;


    private float numWidth;
    private float preNumWidth;
    private float numHeight;

    /**
     * 文字动画
     */
    private boolean isNumAnimating = false;


    //点赞动画
    private Bitmap mUnPraiseBitmap;
    private Bitmap mPraiseBitmap;
    private Bitmap mBlingBitmap;

    //点赞图片动画中
    private boolean isBitmapAnimating = false;
    private boolean isPraised = false;


    //图片的宽
    private float bitmapWidth;
    //发光图片的偏移
    private float blingBitmapOffeset;
    //点赞图片偏移
    private float bitmapXOffeset = 0;
    private float bitmapYOffeset = 0;



    //点赞的动画
    private ObjectAnimator praisedAnimal;
    private float praiseAnimateScale;
    //点赞圆圈绘制系数
    private float circleScale;
    private int circleAlpha;


    private TextPaint mTextPaint;
    private Paint mCirclePaint;


    public PraiseView(Context context) {
        super(context);
        init();
    }

    public PraiseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PraiseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.parseColor("#979797"));
        mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, getContext().getResources().getDisplayMetrics()));


        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(Color.parseColor("#E4583e"));
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(8);

        numHeight = mTextPaint.getFontMetrics().bottom - mTextPaint.getFontMetrics().top;
        baseLineHeight = -mTextPaint.getFontMetrics().top;


        mUnPraiseBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_messages_like_unselected);
        mPraiseBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_messages_like_selected);
        mBlingBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_messages_like_selected_shining);


        float width = mUnPraiseBitmap.getWidth();
        float height = mUnPraiseBitmap.getHeight() + mBlingBitmap.getHeight() / 2;


        bitmapYOffeset = mBlingBitmap.getHeight() / 2;


        if (height > width) {
            bitmapXOffeset = (height - width) / 2f;
            bitmapWidth = height;
        } else {
            bitmapYOffeset = bitmapYOffeset + (width - height) / 2f;
            bitmapWidth = width;
        }

        //发光图片位置横向居中
        blingBitmapOffeset = bitmapWidth / 2f - mBlingBitmap.getWidth() / 2;


        Log.i("PRAISE", "offset: " + blingBitmapOffeset);
        Log.i("PRAISE", "init: " + mTextPaint.getFontMetrics().top);
        Log.i("PRAISE", "init: " + mTextPaint.getFontMetrics().bottom);
        Log.i("PRAISE", "init: " + -mTextPaint.getFontMetrics().top);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawPraiseImage(canvas);
        drawNumber(canvas);
    }

    /**
     * 绘制点赞图片
     * @param canvas
     */
    private void drawPraiseImage(Canvas canvas) {
        canvas.save();
        canvas.clipRect(0, 0, bitmapWidth, bitmapWidth);
        if (!isBitmapAnimating) {
            if (isPraised) {
                canvas.drawBitmap(mPraiseBitmap, bitmapXOffeset, bitmapYOffeset, null);
                canvas.drawBitmap(mBlingBitmap, blingBitmapOffeset, 0, null);
            } else {
                canvas.drawBitmap(mUnPraiseBitmap, bitmapXOffeset, bitmapYOffeset, null);
            }
        } else {
            if (isPraised) {
                mCirclePaint.setAlpha(circleAlpha);  //绘制发散的圆圈
                canvas.drawCircle(bitmapWidth / 2f, bitmapWidth / 2f, bitmapWidth / 2 * circleScale, mCirclePaint);
                if (praiseAnimateScale >= 1) {  //当缩放系数大于1时，绘制发光效果
                    canvas.drawBitmap(mBlingBitmap, blingBitmapOffeset, 0, null);
                }

                canvas.scale(praiseAnimateScale, praiseAnimateScale, bitmapWidth / 2, baseLineHeight / 2);
                canvas.drawBitmap(mPraiseBitmap, bitmapXOffeset, bitmapYOffeset, null);
            } else {

                canvas.scale(praiseAnimateScale, praiseAnimateScale, bitmapWidth / 2, baseLineHeight / 2);
                canvas.drawBitmap(mUnPraiseBitmap, bitmapXOffeset, bitmapYOffeset, null);
            }
        }
        canvas.restore();
    }


    public void setPraised(boolean praised) {
        isPraised = praised;
        praisedAnimal();
    }


    private void setPraiseAnimateScale(float scale) {
        this.praiseAnimateScale = scale;
        postInvalidate();
    }

    private void setCircleScale(float scale) {
        this.circleScale = scale;
        postInvalidate();
    }

    private void setCircleAlpha(int scale) {
        this.circleAlpha = scale;
        postInvalidate();
    }



    /**
     * 点赞动画
     */
    private void praisedAnimal() {
        if (praisedAnimal != null && praisedAnimal.isRunning()) {
            praisedAnimal.cancel();
        }
//        praisedAnimal = ObjectAnimator.ofFloat(this, "praiseAnimateScale", 0.9f, 1.1f,1f);


        if (isPraised) {
            PropertyValuesHolder praiseScale = PropertyValuesHolder.ofFloat("praiseAnimateScale", 1, 0.9f, 1.1f, 1f);
            PropertyValuesHolder cicle = PropertyValuesHolder.ofFloat("circleScale", 0, 1);
            PropertyValuesHolder alpha = PropertyValuesHolder.ofInt("circleAlpha", 255, 0);
            praisedAnimal = ObjectAnimator.ofPropertyValuesHolder(this, praiseScale, cicle, alpha);
            praisedAnimal.setDuration(500);
        } else {
            PropertyValuesHolder praiseScale = PropertyValuesHolder.ofFloat("praiseAnimateScale", 1, 0.9f, 1f);
            praisedAnimal = ObjectAnimator.ofPropertyValuesHolder(this, praiseScale);
            praisedAnimal.setDuration(300);
        }


        praisedAnimal.setInterpolator(new DecelerateInterpolator());
        isBitmapAnimating = true;
        praisedAnimal.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isBitmapAnimating = false;
            }
        });
        praisedAnimal.start();
    }


    public boolean getPraised() {
        return isPraised;
    }

    /**
     * 绘制文字动画
     *
     * @param canvas
     */
    private void drawNumber(Canvas canvas) {
        //文字动画
        canvas.save();
        canvas.translate(bitmapWidth, mBlingBitmap.getHeight() / 2);
        canvas.clipRect(0, 0, bitmapWidth + numWidth, numHeight);
        if (!isNumAnimating) {
            canvas.drawText(newNum, 0, baseLineHeight, mTextPaint);
        } else {
            numAnimateScale *= -direction;
            canvas.drawText(preNoChangeNum, 0, baseLineHeight, mTextPaint);
            canvas.drawText(oldAnimationNum, 0 + preNumWidth, baseLineHeight + numHeight * numAnimateScale, mTextPaint);
            canvas.drawText(newAnimationNum, 0 + preNumWidth, baseLineHeight + numHeight * (numAnimateScale + direction), mTextPaint);
        }
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureMeasureSpec(widthMeasureSpec, (int) (bitmapWidth + numWidth)), measureMeasureSpec(heightMeasureSpec, (int) bitmapWidth));
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int measureMeasureSpec(int measureSpec, int defaultVal) {
        int result = 0;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        switch (mode) {
            case MeasureSpec.EXACTLY:
                result = size;
                break;
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                result = defaultVal;
                break;
        }
        return result;
    }

    /**
     * 设置当前数值
     *
     * @param num
     */
    public void setNum(int num) {
        if (num > currentNum) {// 加数字  向上
            direction = 1;
        } else {  //减数字 向下
            direction = -1;
        }
        currentNum = num;
        oldNum = newNum;
        newNum = String.valueOf(num);
        handleNum();

        if (direction > 0) {
            setPraised(true);
        } else {
            setPraised(false);
        }
        numAnim();
    }

    /**
     * 获取当前数值
     *
     * @return
     */
    public int getCurrentNum() {
        return currentNum;
    }

    /**
     * 处理要变化的数值
     */
    private void handleNum() {
        if (oldNum.length() != newNum.length()) {  //两个数字长度不相等，代表是尾数的变化，所有数字都需要滚动
            preNoChangeNum = "";
            oldAnimationNum = oldNum;
            newAnimationNum = newNum;
        } else {
            int difNum = 0;
            for (int i = 0; i < oldNum.length(); i++) {
                if (oldNum.charAt(i) != newNum.charAt(i)) {
                    difNum = i;
                    break;
                }
            }
            preNoChangeNum = oldNum.substring(0, difNum);
            oldAnimationNum = oldNum.substring(difNum, oldNum.length());
            newAnimationNum = newNum.substring(difNum, newNum.length());
        }

        preNumWidth = mTextPaint.measureText(preNoChangeNum);

        float oldWidth = mTextPaint.measureText(oldNum);
        float newWidth = mTextPaint.measureText(newNum);

        numWidth = oldWidth > newWidth ? oldWidth : newWidth;

        Log.i("PRAISE", "oldWidth: " + oldWidth);
        Log.i("PRAISE", "numWidth: " + newWidth);
    }


    private ObjectAnimator numAnimator;

    private void numAnim() {
        if (numAnimator != null && numAnimator.isRunning()) {
            numAnimator.cancel();
        }
        numAnimator = ObjectAnimator.ofFloat(this, "numAnimateScale", 0f, 1f);
        numAnimator.setDuration(500);
        isNumAnimating = true;
        numAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isNumAnimating = false;
            }
        });
        numAnimator.start();
    }

    public void setNumAnimateScale(float animateScale) {
        this.numAnimateScale = animateScale;
        postInvalidate();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

    }


}
