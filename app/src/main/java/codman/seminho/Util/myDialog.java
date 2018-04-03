package codman.seminho.Util;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.ProgressBar;

import codman.seminho.DataBase.DatabaseHelper;

/**
 * Created by DI1 on 02.04.2018.
 */

public class myDialog {

    private static myDialog instance;
    private Context context;


    public static synchronized myDialog getInstance(Context context) {
        if (instance == null) {
            instance = new myDialog(context.getApplicationContext());
        }
        return instance;
    }
    private myDialog(Context context) {

        this.context = context;
    }

    private ProgressBar getProgressBar(String title)
    {
        ProgressBar pBar= new ProgressBar(context);

        pBar.setMax(10);


        return pBar;
    }
}
