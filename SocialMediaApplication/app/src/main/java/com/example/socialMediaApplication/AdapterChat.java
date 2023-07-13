package com.example.socialMediaApplication;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.socialMediaApplication.model.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder>{


    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    Context context;
    List<Chat> chatList;
    String imageUrl;

    FirebaseUser fUser;

    public AdapterChat (Context context, List<Chat> chatList, String imageUrl)
    {
        this.context = context;
        this.chatList = chatList;
        this.imageUrl = imageUrl;
    }

    @Override
    public MyHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        //inflate the layouts right left
        if(viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, parent, false);
            return new MyHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, parent, false);
            return new MyHolder(view);
        }
    }

    @Override
    public void onBindViewHolder( MyHolder myHolder, int position) {

        //mesajı al, get the Message
        String message = chatList.get(position).getMessage();
        //timestamp convert, zamanı hesaplama
        String timeStamp = chatList.get(position).getTimeStamp();
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();

        myHolder.messageTV.setText(message);
        myHolder.timeStampTV.setText(dateTime);
        try {
            loadImageFromDB(imageUrl,myHolder.profileIV);
        }catch (Exception e){

        }

        if (position == chatList.size()-1) {
            if (chatList.get(position).getSeen()) {
                myHolder.isSeenIV.setImageResource(R.drawable.seen_double_tick);
            } else {
                myHolder.isSeenIV.setImageResource(R.drawable.delivered_double_tick);
            }
        }
        else
        {
            myHolder.isSeenIV.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }
    //view holder class


    @Override
    public int getItemViewType(int position) {
        //get currently signed in user, hesaba giriş yapmış olan kullanıcıyı al
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if(chatList.get(position).getSender().equals(fUser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else
        {
            return MSG_TYPE_LEFT;
        }

    }

    class MyHolder extends ViewHolder{

        ImageView profileIV, isSeenIV;
        TextView messageTV, timeStampTV;
        public MyHolder( View itemView) {
            super(itemView);

            profileIV = itemView.findViewById(R.id.profileIV);
            messageTV = itemView.findViewById(R.id.messageTV);
            timeStampTV = itemView.findViewById(R.id.timeTV);
            isSeenIV = itemView.findViewById(R.id.isSeenIV);
        }

    }
    private void loadImageFromDB(String stringProfileId, ImageView imageView) {
        //String DATABASE_URL = "your-database-url.com";
        String DATABASE_URL = "gs://meething-a97ed.appspot.com/";
        //image will be fetched from this url below. fotoğraf dbdeki bu url üzerinden çekilecek.
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(DATABASE_URL+ stringProfileId+"/image");

        GlideApp.with(context)
                .load(ref)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView);
    }
}
