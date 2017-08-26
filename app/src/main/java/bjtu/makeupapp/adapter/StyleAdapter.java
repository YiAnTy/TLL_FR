package bjtu.makeupapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import bjtu.makeupapp.R;
import bjtu.makeupapp.model.StyleItem;

/**
 * Created by Logic on 2017/8/26.
 */

public class StyleAdapter extends RecyclerView.Adapter<StyleAdapter.ViewHolder> {

    private List<StyleItem> styleItems;

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
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
        ViewHolder holder = new ViewHolder(view);
        return holder;
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
