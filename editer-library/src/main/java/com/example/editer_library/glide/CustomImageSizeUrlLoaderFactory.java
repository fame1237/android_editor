package com.example.editer_library.glide;

import com.bumptech.glide.load.model.*;

import java.io.InputStream;


class CustomImageSizeUrlLoaderFactory implements ModelLoaderFactory<CustomImageSizeModel, InputStream> {
    private final ModelCache<CustomImageSizeModel, GlideUrl> modelCache = new ModelCache<>(1000);

    @Override
    public ModelLoader<CustomImageSizeModel, InputStream> build(MultiModelLoaderFactory multiFactory) {
        ModelLoader<GlideUrl, InputStream> modelLoader = multiFactory.build(GlideUrl.class, InputStream.class);
        return new CustomImageSizeUrlLoader(modelLoader, modelCache);
    }

    @Override
    public void teardown() {

    }
}