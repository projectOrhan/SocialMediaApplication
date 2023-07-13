package com.example.socialMediaApplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MyService extends Service {
    String myUid;

    //this class sets user online while he uses app
    //bu sınıf kullanıcı uygulamayı kullanırken statusunu online yapıyor.

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //starts when user logged in
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //get user id user logged in.
        checkUserStatus();
        checkOnlineStatus("Online");
        return super.onStartCommand(intent, flags, startId);
    }

    //stops when application closed totally
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        //get timestamp
        String timeStamp = String.valueOf(System.currentTimeMillis());
        //set offline with last seen time stamp
        checkOnlineStatus(timeStamp);
        super.onTaskRemoved(rootIntent);
        this.stopSelf();
    }

    private void checkOnlineStatus(String status)
    {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("profileDetail").child(myUid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("onlineStatus", status);
        //update value of onlineStatus of current user
        dbRef.updateChildren(hashMap);
    }

    private void checkUserStatus(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            myUid = user.getUid();
        }
        else {
            Toast.makeText(getApplicationContext(),"Kullanıcı bilgisi alınamadı", Toast.LENGTH_SHORT).show();
        }
}
}
