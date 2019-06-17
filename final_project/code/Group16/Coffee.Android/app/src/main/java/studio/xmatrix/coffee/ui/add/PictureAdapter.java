package studio.xmatrix.coffee.ui.add;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import studio.xmatrix.coffee.R;
import studio.xmatrix.coffee.databinding.AddPicItemBinding;

import static android.view.LayoutInflater.from;

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.ViewHolder> {
    private AddActivity activity;
    private ArrayList<Bitmap> originBitmaps;
    private ArrayList<Bitmap> thumbnails;
    private RecyclerView recyclerView;

    public PictureAdapter(AddActivity activity, RecyclerView recyclerView) {
        this.activity = activity;
        originBitmaps = new ArrayList<>();
        thumbnails = new ArrayList<>();
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(DataBindingUtil.inflate(from(activity), R.layout.add_pic_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind(i);
    }

    @Override
    public int getItemCount() {
        return originBitmaps.size();
    }

    public ArrayList<Bitmap> getBitmaps() {
        return originBitmaps;
    }

    public void addPics(List<LocalMedia> list) {
        try{
            for (LocalMedia localMedia: list) {
                String path = localMedia.getPath();
                Bitmap bmp = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), Uri.fromFile(new File(path)));
                originBitmaps.add(bmp);
                thumbnails.add(null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        notifyDataSetChanged();

        recyclerView.scrollToPosition(originBitmaps.size() - 1);
    }

    private void removePic(int pos) {
        originBitmaps.remove(pos);
        thumbnails.remove(pos);
        notifyDataSetChanged();
    }
    
    class ViewHolder extends RecyclerView.ViewHolder {
        private final AddPicItemBinding binding;

        public ViewHolder(AddPicItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(int pos) {
            Bitmap bmp = originBitmaps.get(pos);
            if(thumbnails.get(pos) == null) {
                int length, x, y;
                if(bmp.getWidth() < bmp.getHeight()) {
                    length = bmp.getWidth();
                    x = 0;
                    y = (bmp.getHeight() - length) / 2;
                } else {
                    length = bmp.getHeight();
                    x = (bmp.getWidth() - length) / 2;
                    y = 0;
                }
                bmp = Bitmap.createBitmap(bmp, x, y, length, length);
                Bitmap.createScaledBitmap(bmp, binding.pic.getLayoutParams().width, binding.pic.getLayoutParams().height, true);
                thumbnails.remove(pos);
                thumbnails.add(pos, bmp);
            } else {
                bmp = thumbnails.get(pos);
            }
            binding.pic.setImageBitmap(bmp);
            binding.remove.setOnClickListener((View v) -> {
                removePic(pos);
            });
        }
    }
}
