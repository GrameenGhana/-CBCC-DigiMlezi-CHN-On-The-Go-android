package org.cbccessence.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import org.cbccessence.R;
import org.digitalcampus.oppia.application.MobileLearning;
import org.cbccessence.models.LcReference;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Created by aangjnr on 14/02/2017.
 */

public class LcReferencesAdapter extends RecyclerView.Adapter<LcReferencesAdapter.LcReferencesViewHolder>{

    private Context context;
    private List<LcReference> references;
    LcReferencesAdapter.OnItemClickListener mItemClickListener;

    public LcReferencesAdapter(Context ctx, List<LcReference> references){
        this.context = ctx;
        this.references = references;

    }

    @Override
    public LcReferencesAdapter.LcReferencesViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.references_item_view, parent, false);



        return new LcReferencesAdapter.LcReferencesViewHolder(v);
    }



    @Override

    public void onBindViewHolder(LcReferencesAdapter.LcReferencesViewHolder holder, int position){

        LcReference reference = references.get(position);

        holder.file_name.setText(reference.getReferenceName());
        holder.file_size.setText(reference.getFileSize());


        if(MobileLearning.doesFileExist(reference.getShortName())) {
            holder.download.setVisibility(View.GONE);

            File thumbnail = new File(MobileLearning.REFERENCES_ROOT + "/thumbnails/" + reference.getShortName() + ".png");

            if(thumbnail.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(thumbnail.toString());
                holder.file_image.setImageBitmap(bitmap);

            }else{

                String loc = MobileLearning.REFERENCES_ROOT  + reference.getShortName() + ".pdf";

                if (generateImageFromPdf(Uri.parse( "file://" + loc), reference.getReferenceName())){
                    Bitmap bitmap = BitmapFactory.decodeFile(thumbnail.toString());
                    holder.file_image.setImageBitmap(bitmap);

                }




            }

        }

        else holder.download.setVisibility(View.VISIBLE);



    }

    @Override
    public int getItemCount(){
        return references.size();
    }



    public class LcReferencesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView file_image;
        ImageView no_preview;
        ImageView download;

        TextView file_name;
        TextView file_size;

        RelativeLayout downloadPlaceHolder;
        LinearLayout refLayout;

        LcReferencesViewHolder(View itemView){
            super(itemView);
            file_image = (ImageView) itemView.findViewById(R.id.file_image);
            //no_preview = (ImageView) itemView.findViewById(R.id.no_preview);
            download = (ImageView) itemView.findViewById(R.id.cloud_download);
            file_name = (TextView) itemView.findViewById(R.id.filename);
            file_size = (TextView) itemView.findViewById(R.id.filesize);

            file_image = (ImageView) itemView.findViewById(R.id.file_image);
            refLayout = (LinearLayout) itemView.findViewById(R.id.ref_layout);

            refLayout.setOnClickListener(this);

        }


        @Override
        public void onClick(View v){

            if(mItemClickListener != null) mItemClickListener.onItemClick(itemView, getAdapterPosition());


        }
    }


    public interface OnItemClickListener{

        void onItemClick(View v, int position);

    }


    public void setOnItemClickListener(final LcReferencesAdapter.OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }




    public boolean generateImageFromPdf(Uri pdfUri, String pdfThumbnailName) {


        Log.i("Doc Adapter", "URI is " + pdfUri + " and name of file is " + pdfThumbnailName + ".png");


        int pageNumber = 0;
        PdfiumCore pdfiumCore = new PdfiumCore(context);
        try {
            //http://www.programcreek.com/java-api-examples/index.php?api=android.os.ParcelFileDescriptor
            ParcelFileDescriptor fd = context.getApplicationContext().getContentResolver().openFileDescriptor(pdfUri, "r");
            PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
            pdfiumCore.openPage(pdfDocument, pageNumber);
            int width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNumber);
            int height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNumber);
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            pdfiumCore.renderPageBitmap(pdfDocument, bmp, pageNumber, 0, 0, width, height);
            saveImage(bmp, pdfThumbnailName);
            pdfiumCore.closeDocument(pdfDocument); // important!
            Log.i("Doc Adapter", "Thumbnail Generated!");
            return true;
        } catch(Exception e) {
            //todo with exception
            e.printStackTrace();
            Log.i("Doc Adapter", "Something happened!");
            return false;
        }
    }

    private void saveImage(Bitmap bmp, String thumbnailName) {
        FileOutputStream out = null;

        String FOLDER = MobileLearning.REFERENCES_ROOT + File.separator + "thumbnails";


        Log.i("Doc Adapter", "Thumbnail folder location is " + FOLDER);


        try {
            File no_media = new File(FOLDER);
            if(!no_media.exists())
                no_media.mkdirs();

            File file = new File(no_media + File.separator , ".nomedia");
            if(!file.exists()) {
                out = new FileOutputStream(file);
                out.write(0);
                out.close();


                Log.i("Doc Adapter", "No media created!  " + file);
            }
            else { Log.i("Doc Adapter", "No media already exists!!!!!!  " + file);}

        }catch (Exception e){
            e.printStackTrace();

        }





        try {
            File folder = new File(FOLDER);
            if(!folder.exists())
                folder.mkdirs();
            File file = new File(folder + File.separator , thumbnailName + ".png");

            Log.i("Doc Adapter", "Thumbnail will be saved as  " + file);
            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance

            Log.i("Doc Adapter", "Thumbnail successfully saved! with name " + thumbnailName);
        } catch (Exception e) {
            //todo with exception
            e.printStackTrace();
            Log.i("Doc Adapter", "Thumbnail cound not save :(" );
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception e) {
                //todo with exception
            }
        }
    }




}