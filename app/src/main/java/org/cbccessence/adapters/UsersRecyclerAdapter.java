package org.cbccessence.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.cbccessence.cch.model.User;
import org.cbccessence.R;
import org.digitalcampus.oppia.application.DbHelper;
import org.digitalcampus.oppia.model.CbccUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aangjnr on 01/03/2017.
 */

public class UsersRecyclerAdapter extends RecyclerView.Adapter<UsersRecyclerAdapter.UsersViewHolder> {

    DbHelper databaseHandler;
    List<User> users;
    private Context context;
    OnItemClickListener mItemClickListener;
    OnItemLongClickListener mItemLongClickListener;


    public UsersRecyclerAdapter(Context c, List<User> users){
        this.context = c;
        this.users = users;
        databaseHandler =   DbHelper.getInstance(context);

    }

    @Override
    public UsersViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_itemview, viewGroup, false);

        return new UsersViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final UsersViewHolder viewHolder, final int position) {
        User user = users.get(position);



        viewHolder.name.setText(user.getFirstName() + " " + user.getLastName());
        if (user.getGender() != null)viewHolder.gender.setText(user.getGender().substring(0, 1).toUpperCase());
        viewHolder.id.setText(user.getNationalId());

        if(user.getAfyaStartWeek() != null && user.getAfyaChannel() != null){

            if(!user.getAfyaStartWeek().equals("") || !user.getAfyaChannel().equals("")){
                viewHolder.afya.setVisibility(View.VISIBLE);

            }

        }

       // if(user.getSyncStatus() == 0) viewHolder.sync.setVisibility(View.VISIBLE);


    }



    public class UsersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        CardView cardView;
        TextView name;
        TextView id;
        TextView gender;
        ImageView sync;
        TextView afya;



        UsersViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.user_cardView);
            name = (TextView) itemView.findViewById(R.id.name);
            id = (TextView) itemView.findViewById(R.id.id);
            gender = (TextView) itemView.findViewById(R.id.gender);
            afya = (TextView) itemView.findViewById(R.id.afya);
            sync = (ImageView) itemView.findViewById(R.id.sync);


            cardView.setOnClickListener(this);
            cardView.setOnLongClickListener(this);


         }




    @Override
    public void onClick(View v) {
        if (mItemClickListener != null) {
            mItemClickListener.onItemClick(itemView, getAdapterPosition());


        }

    }

        @Override
        public boolean onLongClick(View v) {
            if (mItemLongClickListener != null) {
                mItemLongClickListener.onLongClick(itemView, getAdapterPosition());


            }

            return false;
        }
    }



    public void setOnItemCickListener(final UsersRecyclerAdapter.OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public void setOnItemLongClickListener(final UsersRecyclerAdapter.OnItemLongClickListener mItemLongClickListener) {
        this.mItemLongClickListener = mItemLongClickListener;
    }




    public interface OnItemClickListener {
    void onItemClick(View view, int position);
}

    public interface OnItemLongClickListener {
        boolean onLongClick(View view, int position);
    }




    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return users.size();

    }
}
