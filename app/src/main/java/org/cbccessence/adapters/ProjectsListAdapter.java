package org.cbccessence.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.cbccessence.R;
import org.cbccessence.models.Projects;

import java.util.List;

/**
 * Created by AangJnr on 10/9/16.
 */
public class ProjectsListAdapter extends RecyclerView.Adapter<ProjectsListAdapter.ProjectsViewHolder>{


    Context mContext;
    List<Projects> projectsList;
    OnItemClickListener mItemClickListener;


public ProjectsListAdapter(Context context, List<Projects> project_list){
    this.mContext = context;
    this. projectsList = project_list;

}


    @Override
    public ProjectsViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_item, parent, false);



        return new ProjectsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ProjectsViewHolder holder, int position) {
        Projects project_item = projectsList.get(position);


        holder.project_name.setText(project_item.getProjectName());
    }



    @Override
    public int getItemCount() {
        return projectsList.size();
    }

    public class ProjectsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        TextView project_name;


    ProjectsViewHolder(View itemView){
        super(itemView);


        project_name = (TextView) itemView.findViewById(R.id.project_name);

        project_name.setOnClickListener(this);

    }


        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(itemView, getAdapterPosition());


            }

        }
}


    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}

