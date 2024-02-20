//package com.hui.tally;
//
//import android.content.Context;
//import android.graphics.Outline;
//import android.util.AttributeSet;
//import android.view.View;
//import android.view.ViewOutlineProvider;
//import android.widget.RelativeLayout;
//
//public class RoundRelativeLayout extends RelativeLayout {
//    private static final int DEFAULT_ROUND_SIZE = 20;
//
//    public RoundRelativeLayout(Context context) {
//        super(context,null);
//    }
//
//    public RoundRelativeLayout(Context context, AttributeSet attrs) {
//        super(context, attrs,0);
//    }
//
//    public RoundRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr,0);
//    }
//
//    public RoundRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        relativeLayoutinit();
//    }
//
//    private void relativeLayoutinit() {
//        RoundViewOutlineProvider outlineProvider=new RoundViewOutlineProvider(ScreenUtil.dp2px(20));
//        setOutlineProvider(outlineProvider);
//        setClipToOutline(true);
//    }
//
//
//    /**
//     * 圆角ViewOutlineProvider
//     */
//    private static class RoundViewOutlineProvider extends ViewOutlineProvider {
//        private final int roundSize;
//        public RoundViewOutlineProvider(int roundSize) {
//            this.roundSize = roundSize;
//        }
//        @Override
//        public void getOutline(View view, Outline outline) {
//            outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), roundSize);
//        }
//    }
//
//    public class ScreenUtil {
//        public int dp2px(Context context, int dpValue) {
//            float density = context.getResources().getDisplayMetrics().density;
//            return (int) (dpValue * density + 0.5f);
//        }
//    }
//
//}
