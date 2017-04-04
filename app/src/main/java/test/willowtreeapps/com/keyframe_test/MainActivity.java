package test.willowtreeapps.com.keyframe_test;

import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.facebook.keyframes.KeyframesDrawable;
import com.facebook.keyframes.KeyframesDrawableBuilder;
import com.facebook.keyframes.deserializers.KFImageDeserializer;
import com.facebook.keyframes.model.KFImage;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GestureOverlayView.OnGesturePerformedListener, KeyframesDrawable.OnAnimationEnd {

    private static final String TAG = "MainActivity";

    private GestureLibrary gestureLibrary;
    private KeyframesDrawable kfDrawable = null;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gestureLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if (!gestureLibrary.load()) {
            finish();
        }

        GestureOverlayView gestureOverlayView = ((GestureOverlayView) findViewById(R.id.gOverlay));
        gestureOverlayView.addOnGesturePerformedListener(this);

        try {
            InputStream stream = getResources().getAssets().open("sample.json");
            KFImage kfImage = KFImageDeserializer.deserialize(stream);
            kfDrawable = new KeyframesDrawableBuilder().withImage(kfImage).build();
        } catch (IOException e) {
            e.printStackTrace();
        }

        image = ((ImageView) findViewById(R.id.image));
        image.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        image.setImageAlpha(0);
        image.setImageDrawable(kfDrawable);

        kfDrawable.setAnimationListener(this);
    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        ArrayList<Prediction> predictions = gestureLibrary.recognize(gesture);

        if (predictions.size() > 0 && predictions.get(0).score > 1.0) {
            image.setVisibility(View.VISIBLE);
            RectF gestureRectF = gesture.getBoundingBox();
            image.setX(gestureRectF.centerX()-400f - (gestureRectF.width()/2));
            image.setY(gestureRectF.centerY()-400f - (gestureRectF.height()/2));

            kfDrawable.setAnimationListener(this);
            kfDrawable.playOnce();
        }
    }

    @Override
    public void onAnimationEnd() {
        image.setVisibility(View.GONE);
        kfDrawable.setAnimationListener(null);
    }
}
