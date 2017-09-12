package bjtu.makeupapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import bjtu.makeupapp.activity.MainActivity;
import bjtu.makeupapp.activity.R;
import bjtu.makeupapp.model.StyleItem;

/**
 * Created by Logic on 2017/8/26.
 */

public class StyleAdapter extends RecyclerView.Adapter<StyleAdapter.ViewHolder> {

    private List<StyleItem> styleItems;

    public class ViewHolder extends RecyclerView.ViewHolder {

        View styleView;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            styleView=itemView;
            imageView = (ImageView) itemView.findViewById(R.id.make_up_image);
        }
    }

    public StyleAdapter(List<StyleItem> list){
        this.styleItems=list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.style_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        holder.styleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int postion = holder.getAdapterPosition();
                StyleItem styleItem = styleItems.get(postion);

                //TODO 点击选择妆容，在显示界面显示相应妆容
                MainActivity mainActivity=(MainActivity)v.getContext();
                mainActivity.getName().setText(styleItem.getName());

            }
        });
        return holder;
    }

    public void turnToNext(int position,MainActivity v){
        StyleItem styleItem = styleItems.get(position);

        //TODO 点击选择妆容，在显示界面显示相应妆容
        v.getName().setText(styleItem.getName());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        StyleItem styleItem = styleItems.get(position);
        Picasso.with(holder.itemView.getContext())
                .load(styleItem.getImageUrl())
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return styleItems.size();
    }


}
