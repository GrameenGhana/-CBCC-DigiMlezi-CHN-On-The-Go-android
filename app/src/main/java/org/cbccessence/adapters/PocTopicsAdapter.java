package org.cbccessence.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import org.digitalcampus.mobile.learningGF.R;
import org.digitalcampus.oppia.application.MobileLearning;
import org.digitalcampus.oppia.model.Topic;

import java.io.File;
import java.util.List;

/**
 * Created by aangjnr on 14/02/2017.
 */

public class PocTopicsAdapter extends RecyclerView.Adapter<PocTopicsAdapter.PocTopicsViewHolder>{

    private Context context;
    private List<Topic> sections;
    OnItemClickListener mItemClickListener;

    public PocTopicsAdapter(Context ctx, List<Topic> sections){
        this.context = ctx;
        this.sections = sections;

    }

    @Override
    public PocTopicsAdapter.PocTopicsViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.poc_topic_item_view, parent, false);



        return new PocTopicsAdapter.PocTopicsViewHolder(v);
    }



    @Override

    public void onBindViewHolder(PocTopicsAdapter.PocTopicsViewHolder holder, int position){






        Topic topic = sections.get(position);
        holder.topic_name.setText(topic.getTopicName());









        if(MobileLearning.doesTopicsFileExist(MobileLearning.POC_ROOT + topic.getShortName() + File.separator) != null)  holder.topics_cloud.setVisibility(View.GONE);
        else holder.topics_cloud.setVisibility(View.VISIBLE);


    }

    @Override
    public int getItemCount(){
        return sections.size();
    }



    public class PocTopicsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView topic_name;
        CardView topic_layout;
        ImageView topics_cloud;

        PocTopicsViewHolder(View itemView){
            super(itemView);
            topic_name = (TextView) itemView.findViewById(R.id.topic_name);
            topic_layout = (CardView) itemView.findViewById(R.id.topic_layout);
            topics_cloud = (ImageView) itemView.findViewById(R.id.topic_cloud);

            topic_layout.setOnClickListener(this);
        }


        @Override
        public void onClick(View v){

            if(mItemClickListener != null) mItemClickListener.onItemClick(itemView, getAdapterPosition());


        }
    }


    public interface OnItemClickListener{

        void onItemClick(View v, int position);

    }


    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }







}
