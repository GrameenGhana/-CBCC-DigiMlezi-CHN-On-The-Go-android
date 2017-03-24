package org.cbccessence.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import org.digitalcampus.mobile.learningGF.R;
import org.digitalcampus.oppia.model.POCSection;

import java.util.List;

/**
 * Created by aangjnr on 13/02/2017.
 */

public class PocSectionsAdapter extends RecyclerView.Adapter<PocSectionsAdapter.PocSectionsViewHolder>{

    Context context;
    List<POCSection> sections;
    OnItemClickListener mItemClickListener;

    public PocSectionsAdapter(Context ctx, List<POCSection> sections){
        this.context = ctx;
        this.sections = sections;

    }

    @Override
    public PocSectionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.poc_section_item_view, parent, false);



        return new PocSectionsViewHolder(v);
    }



    @Override

    public void onBindViewHolder(PocSectionsViewHolder holder, int position){

        POCSection section = sections.get(position);

        String secName = section.getSectionName();

        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        int color1 = generator.getRandomColor();

        TextDrawable drawable2 = TextDrawable.builder()
                .buildRound(secName.substring(0, 1), color1);

        holder.section_name.setText(secName);
        holder.image.setImageDrawable(drawable2);




    }

    @Override
    public int getItemCount(){
        return sections.size();
    }



    public class PocSectionsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView section_name;
        RelativeLayout section_layout;
        ImageView image;

        PocSectionsViewHolder(View itemView){
            super(itemView);
             section_name = (TextView) itemView.findViewById(R.id.section_name);
             section_layout = (RelativeLayout) itemView.findViewById(R.id.section_layout);
             image = (ImageView) itemView.findViewById(R.id.sec_ImageView);

            section_layout.setOnClickListener(this);
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
