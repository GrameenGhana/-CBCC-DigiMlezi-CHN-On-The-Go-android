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

import org.cbccessence.R;
import org.cbccessence.models.SubSection;

import java.util.List;

/**
 * Created by aangjnr on 14/02/2017.
 */

public class PocSubSectionsAdapter extends RecyclerView.Adapter<PocSubSectionsAdapter.PocSubSectionsViewHolder>{

    private Context context;
    private List<SubSection> sections;
    OnItemClickListener mItemClickListener;

    public PocSubSectionsAdapter(Context ctx, List<SubSection> sections){
        this.context = ctx;
        this.sections = sections;

    }

    @Override
    public PocSubSectionsAdapter.PocSubSectionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.poc_sub_section_item_view, parent, false);



        return new PocSubSectionsAdapter.PocSubSectionsViewHolder(v);
    }



    @Override

    public void onBindViewHolder(PocSubSectionsAdapter.PocSubSectionsViewHolder holder, int position){
        SubSection section = sections.get(position);
        String subSecName = section.getName();

        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        int color1 = generator.getRandomColor();

        TextDrawable drawable2 = TextDrawable.builder()
                .buildRound(subSecName.substring(0, 1), color1);





        holder.sub_section_name.setText(subSecName);
        holder.image.setImageDrawable(drawable2);



    }

    @Override
    public int getItemCount(){
        return sections.size();
    }



    public class PocSubSectionsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView sub_section_name;
        CardView sub_section_layout;
        ImageView image;

        PocSubSectionsViewHolder(View itemView){
            super(itemView);
            sub_section_name = (TextView) itemView.findViewById(R.id.sub_section_name);
            sub_section_layout = (CardView) itemView.findViewById(R.id.sub_section_layout);
            image = (ImageView) itemView.findViewById(R.id.sub_section_item_logo);

            sub_section_layout.setOnClickListener(this);
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
