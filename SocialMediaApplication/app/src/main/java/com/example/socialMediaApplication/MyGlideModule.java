package com.example.socialMediaApplication;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;


//This module useful for easy loading and uploading of pictures between firebase database and ImageView
//Bu modül sayesinde ImagiView lara Firebase database den kolayca görüntü dosyaları aktarılabiliyor.
@GlideModule
public class MyGlideModule extends AppGlideModule {
    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        registry.append(StorageReference.class, InputStream.class,
                new FirebaseImageLoader.Factory());
    }
}