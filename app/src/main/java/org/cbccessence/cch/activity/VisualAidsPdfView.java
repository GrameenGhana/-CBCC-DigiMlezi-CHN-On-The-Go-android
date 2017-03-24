package org.cbccessence.cch.activity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.shockwave.pdfium.PdfDocument;

import org.cbccessence.PlaceHolder;
import org.cbccessence.poc.BaseActivity;
import org.digitalcampus.mobile.learningGF.R;
import org.digitalcampus.oppia.application.MobileLearning;

import java.io.File;
import java.util.List;

/**
 * Created by aangjnr on 28/02/2017.
 */

public class VisualAidsPdfView extends BaseActivity implements OnPageChangeListener, OnLoadCompleteListener {

    PDFView pdfView;
    String doc_loc;
    String TAG = VisualAidsPdfView.class.getSimpleName();
    int pageNumber = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doc_loc = getIntent().getExtras().getString("pdf_location");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        if (getSupportActionBar()!= null) getSupportActionBar().hide();
        setContentView(R.layout.visual_aids_pdf_view);

        pdfView = (PDFView) findViewById(R.id.ref_pdfView);








        if(doc_loc != null)

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    displayFromAsset(doc_loc);
                }
            }, 1000);



        else
            PlaceHolder.inflateNoContentEmptyView(this);





    }



    public void displayFromAsset(String assetFileName) {

        //pdf_start_time = System.currentTimeMillis();

        File file = new File(MobileLearning.REFERENCES_ROOT + assetFileName + ".pdf");

         pdfView.fromFile(file)
                .defaultPage(0)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .load();

    }




    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();
        Log.e(TAG, "title = " + meta.getTitle());
        Log.e(TAG, "author = " + meta.getAuthor());
        Log.e(TAG, "subject = " + meta.getSubject());
        Log.e(TAG, "keywords = " + meta.getKeywords());
        Log.e(TAG, "creator = " + meta.getCreator());
        Log.e(TAG, "producer = " + meta.getProducer());
        Log.e(TAG, "creationDate = " + meta.getCreationDate());
        Log.e(TAG, "modDate = " + meta.getModDate());

        printBookmarksTree(pdfView.getTableOfContents(), "-");

    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        //setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
    }


    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }


    @Override
    public void onBackPressed(){
        super.onBackPressed();


    }

}
