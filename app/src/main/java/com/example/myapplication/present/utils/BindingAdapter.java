package com.example.myapplication.present.utils;

import static com.example.myapplication.app.Constants.URL_IMG;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.myapplication.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class BindingAdapter {
    @androidx.databinding.BindingAdapter("android:imageMv")
    public static void setImageMv(ImageView imageView, String img_path) {
        if (img_path == null || img_path.isEmpty()) {
            imageView.setImageResource(R.drawable.default_img);
            return;
        }

        try {
            Uri uri = Uri.parse(URL_IMG + img_path);
            Picasso.get()
                    .load(uri)
                    .placeholder(R.drawable.default_img)
                    .error(R.drawable.error_img)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d("BindingAdapter", "Image loaded successfully: " + img_path);
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e("BindingAdapter", "Failed to load image: " + img_path, e);
                        }
                    });
        } catch (Exception e) {
            Log.e("loglog", "Exception while loading image: " + img_path, e);
            imageView.setImageResource(R.drawable.error_img);
        }
    }

    @androidx.databinding.BindingAdapter("android:star")
    public static void setStar(ImageView imageView, boolean isFav){
        imageView.setImageResource( isFav ? R.drawable.star_filled : R.drawable.star);

    }

    @androidx.databinding.BindingAdapter("android:avatar")
    public static void setAvatar(ImageView imageView, Bitmap bitmap){

        if(bitmap !=null){
            imageView.setImageBitmap(bitmap);
        }else{
            imageView.setImageResource(R.mipmap.ic_launcher);
        }

    }


//    @androidx.databinding.BindingAdapter("android:checkedSex")
//    public static void setCheckedSex(RadioGroup radioGroup, int sex) {
//        radioGroup.check(sex==1 ? R.id.male_radio_edit : R.id.female_radio_edit);
//    }
//
//    public static int getCheckedSex(RadioGroup radioGroup) {
//        return (radioGroup.getCheckedRadioButtonId() == R.id.male_radio_edit) ? 1:0;
//    }
//
//    @androidx.databinding.BindingAdapter("checkedSexAttrChanged")
//    public static void setListeners(RadioGroup radioGroup, InverseBindingListener listener) {
//        if (listener != null) {
//            radioGroup.setOnCheckedChangeListener((group, checkedId) -> listener.onChange());
//        }
//    }
    @androidx.databinding.BindingAdapter("android:adult")
    public static void setAdult(ImageView imageView, boolean a){
        imageView.setVisibility(a? View.VISIBLE :View.GONE);
    }


}
