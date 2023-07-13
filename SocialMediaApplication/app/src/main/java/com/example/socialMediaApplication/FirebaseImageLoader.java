package com.example.socialMediaApplication;

import android.util.Log;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.MessageDigest;

public class FirebaseImageLoader implements ModelLoader<StorageReference, InputStream> {

    private static final String TAG = "FirebaseImageLoader";


    /**
     * Factory to create {@link FirebaseImageLoader}.
     */
    public static class Factory implements ModelLoaderFactory<StorageReference, InputStream> {


        @Override
        public ModelLoader<StorageReference, InputStream> build( MultiModelLoaderFactory factory) {
            return new FirebaseImageLoader();
        }

        @Override
        public void teardown() {
            // No-op
        }
    }


    @Override
    public LoadData<InputStream> buildLoadData( StorageReference reference,
                                               int height,
                                               int width,
                                                Options options) {
        return new LoadData<>(
                new FirebaseStorageKey(reference),
                new FirebaseStorageFetcher(reference));
    }

    @Override
    public boolean handles( StorageReference reference) {
        return true;
    }

    private static class FirebaseStorageKey implements Key {

        private StorageReference mRef;

        public FirebaseStorageKey(StorageReference ref) {
            mRef = ref;
        }

        @Override
        public void updateDiskCacheKey( MessageDigest digest) {
            digest.update(mRef.getPath().getBytes(Charset.defaultCharset()));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FirebaseStorageKey key = (FirebaseStorageKey) o;

            return mRef.equals(key.mRef);
        }

        @Override
        public int hashCode() {
            return mRef.hashCode();
        }
    }

    private static class FirebaseStorageFetcher implements DataFetcher<InputStream> {

        private StorageReference mRef;
        private StreamDownloadTask mStreamTask;
        private InputStream mInputStream;

        public FirebaseStorageFetcher(StorageReference ref) {
            mRef = ref;
        }

        @Override
        public void loadData( Priority priority,
                              final DataCallback<? super InputStream> callback) {
            mStreamTask = mRef.getStream();
            mStreamTask.addOnSuccessListener(snapshot -> {
                        mInputStream = snapshot.getStream();
                        callback.onDataReady(mInputStream);
                    })
                    .addOnFailureListener(e -> callback.onLoadFailed(e));
        }

        @Override
        public void cleanup() {
            // Close stream if possible
            if (mInputStream != null) {
                try {
                    mInputStream.close();
                    mInputStream = null;
                } catch (IOException e) {
                    Log.w(TAG, "Could not close stream", e);
                }
            }
        }

        @Override
        public void cancel() {
            // Cancel task if possible
            if (mStreamTask != null && mStreamTask.isInProgress()) {
                mStreamTask.cancel();
            }
        }


        @Override
        public Class<InputStream> getDataClass() {
            return InputStream.class;
        }


        @Override
        public DataSource getDataSource() {
            return DataSource.REMOTE;
        }
    }
}