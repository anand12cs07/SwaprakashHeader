package com.progressrecyclerview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;

import androidx.appcompat.widget.AppCompatTextView;

public class AnimUtils {
    public static void incrementTextValueAnimation(float startValue, float finalValue,int duration, final AppCompatTextView textView){
        ValueAnimator animator = ValueAnimator.ofFloat(startValue, finalValue);
        animator.setDuration(duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {

                textView.setText(String.format("%.2f", Float.parseFloat(animation.getAnimatedValue().toString())));
            }
        });
        animator.start();
    }

    public static void typeWriterAnimation(final AppCompatTextView textView, final String value, int duration){
        textView.setText("");

        ValueAnimator animator = ValueAnimator.ofInt(0, value.length() - 1);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(duration);
        final Int integer = new Int(-1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int currentIndex = Integer.parseInt(valueAnimator.getAnimatedValue().toString());
                if (integer.getInt() != currentIndex){
                    textView.setText(textView.getText().toString().concat(String.valueOf(value.charAt(currentIndex))));
                }
                integer.setInt(currentIndex);
            }
        });
        animator.start();
    }

    public static void progressUpdateAnimation(final ProgressBar progressBar, int progressTo,int max, int duration){
        progressBar.setMax(max * 100);

        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, progressTo * 100);
        animation.setDuration(duration);
        animation.setInterpolator(new LinearInterpolator());
        animation.start();
    }
}
