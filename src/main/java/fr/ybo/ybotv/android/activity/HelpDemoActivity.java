package fr.ybo.ybotv.android.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.animation.Animation;
import com.octo.android.robodemo.*;

public class HelpDemoActivity extends DemoActivity {

    private static final int DEFAULT_FONT_SIZE = 14;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DrawView) findViewById( R.id.drawView_move_content_demo)).setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    @Override
    public DrawViewAdapter getDrawViewAdapter() {
        Drawable drawable = getResources().getDrawable( R.drawable.ic_lockscreen_handle_pressed );
        TextPaint textPaint = new TextPaint();
        textPaint.setColor( getResources().getColor( android.R.color.white ) );
        textPaint.setShadowLayer( 2.0f, 0, 2.0f, getResources().getColor( android.R.color.black ) );
        textPaint.setAntiAlias( true );
        // http://stackoverflow.com/questions/3061930/how-to-set-unit-for-paint-settextsize
        textPaint.setTextSize( TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_FONT_SIZE, getResources().getDisplayMetrics()) );
        return new DefaultDrawViewAdapter( this, drawable, textPaint, getListPoints() );
    }
}
