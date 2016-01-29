package com.patronage.lukaszpiskadlo;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends BaseAdapter {

    private Context context;
    private List<Item> imageList;
    private ImageLoader imageLoader;
    private int gridLayoutId;

    public ImageAdapter(Context c) {
        super();
        context = c;
        gridLayoutId = R.layout.grid_item;
        imageList = new ArrayList<>();

        int imageSize = context.getResources().getDimensionPixelSize(R.dimen.image_size);
        imageLoader = new ImageLoader(context, imageSize);
        imageLoader.setPlaceholderImage(R.drawable.placeholder);
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public Object getItem(int position) {
        return imageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Item item = imageList.get(position);

        if(convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(gridLayoutId, parent, false);
            holder.image = (ImageView) convertView.findViewById(R.id.imageView);
            holder.title = (TextView) convertView.findViewById(R.id.imageTitle);
            holder.desc = (TextView) convertView.findViewById(R.id.imageDesc);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(item.getTitle());
        holder.desc.setText(item.getDesc());
        // load images
        imageLoader.loadImage(item.getUrl(), holder.image);
        return convertView;
    }

    /**
     * Adds image item to list
     */
    public void addImageToList(Item item) {
        imageList.add(item);
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        ImageView image;
        TextView title;
        TextView desc;
    }
}
