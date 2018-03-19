package codman.seminho.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import codman.seminho.DataBase.DatabaseHelper;
import codman.seminho.EventsActivity;
import codman.seminho.MainActivity;
import codman.seminho.Model.AlarmEvent;
import codman.seminho.PagesActivity;
import codman.seminho.R;

import static android.content.ContentValues.TAG;

/**
 * Created by DI1 on 19.03.2018.
 */

public class AdapterCategories  extends RecyclerView.Adapter<AdapterCategories.ViewHolder> {

    private List<String> categories;
private Context context;
    LayoutInflater inflater;

    public AdapterCategories(Context context,LayoutInflater inflater,final List<String> categories) {
        this.context=context;
        this.categories = categories;
        this.inflater=inflater;
    }


    @Override
    public AdapterCategories.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        //  Log.d(TAG,"onCreateViewHolder");
        // View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_item, viewGroup, false);
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_category, viewGroup, false);

        return new AdapterCategories.ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(AdapterCategories.ViewHolder viewHolder, int i) {

        String category = categories.get(i);
          Log.d(TAG,"onBindViewHolder event = "+category);
        viewHolder.tv.setText(category);
        viewHolder.position=i;
        //viewHolder.alarmName.setText(event.getAlarmName());
      //  viewHolder.category.setText(event.getCategory());

/*
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        String date = format.format(new Date(event.getStartTime()));
        date+=" - "+format.format(new Date(event.getFinishTime()));
        viewHolder.date.setText(date);
        viewHolder.id=(int)event.getId();
*/
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv;
        //private TextView alarmName;
        private TextView category;
        private TextView date;
        //private TextView id;
        private int position;

        public ViewHolder(View itemView) {
            super(itemView);
            //this.id = (TextView) itemView.findViewById(R.id.tvItemID);
            this.tv = (TextView) itemView.findViewById(R.id.tvCategory);
            // this.alarmName = (TextView) itemView.findViewById(R.id.tvItemAlarmName);
           // this.category = (TextView) itemView.findViewById(R.id.tvItemCategory);
           // this.date = (TextView) itemView.findViewById(R.id.tvItemDate);


            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder=new AlertDialog.Builder(context);
                    final  View view=inflater.inflate(R.layout.dialog_category,null);
                    final EditText et=view.findViewById(R.id.etCategory);
                    et.setText(tv.getText());
                    builder.setCancelable(true).setView(view).setTitle(R.string.newCategory);
                    builder.setPositiveButton(R.string.addNewCategory, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            long res= DatabaseHelper.getInstance(context).updateCategory(et.getText().toString(),tv.getText().toString());
                            tv.setText(et.getText());
                            Toast.makeText(context,"Save res="+res,Toast.LENGTH_SHORT).show();

                            dialog.cancel();
                            categories.add(position,tv.getText().toString());
                            categories.remove(position+1);
                            AdapterCategories.this.notifyDataSetChanged();
                        }
                    }).setNeutralButton(R.string.buttonCancel,new DialogInterface.OnClickListener()
                    { @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context,"Cancel",Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                    })
                            .setNegativeButton(R.string.buttonDeleteCategory,new DialogInterface.OnClickListener()
                            { @Override
                            public void onClick(DialogInterface dialog, int which) {
                                long res= DatabaseHelper.getInstance(context).removeCategory(et.getText().toString());
                                Toast.makeText(context,"DELETE="+res,Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                                categories.remove(position);
                                AdapterCategories.this.notifyDataSetChanged();
                            }
                            })
                    ;
                    //dialog.cancel();
                    builder.create().show();

                       Toast.makeText(context, "CLICK Category = " +((TextView)v).getText(), Toast.LENGTH_SHORT).show();

                }
            });
        }
    }
}
