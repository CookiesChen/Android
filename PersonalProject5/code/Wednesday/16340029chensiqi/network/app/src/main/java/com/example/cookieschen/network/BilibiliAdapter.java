package com.example.cookieschen.network;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.example.cookieschen.network.databinding.ItemBilibiliBinding;

import java.util.List;

public class BilibiliAdapter extends RecyclerView.Adapter<BilibiliAdapter.CardViewHolder> {

    private List<BilibiliMovie> bilibiliMovies;

    public BilibiliAdapter(List<BilibiliMovie> bilibiliMovies){
        this.bilibiliMovies = bilibiliMovies;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemBilibiliBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(viewGroup.getContext()), R.layout.item_bilibili, viewGroup, false);
        return new CardViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final CardViewHolder cardViewHolder, @SuppressLint("RecyclerView") final int i) {
        cardViewHolder.binding.setBilibiliMovie(bilibiliMovies.get(i));
        if (cardViewHolder.binding.getBilibiliMovie() != null && cardViewHolder.binding.getBilibiliMovie().getBitmap() != null){
            cardViewHolder.binding.cover.setImageBitmap(cardViewHolder.binding.getBilibiliMovie().getBitmap());
            cardViewHolder.binding.cover.setVisibility(View.VISIBLE);
            cardViewHolder.binding.progressbar.setVisibility(View.INVISIBLE);
        }
        if(bilibiliMovies.get(i).getPreviews() != null){
            cardViewHolder.binding.seekbar.setVisibility(View.VISIBLE);
            cardViewHolder.binding.seekbar.setMax(bilibiliMovies.get(i).getPreviews().size() - 1);
            cardViewHolder.binding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        cardViewHolder.binding.cover.setImageBitmap(bilibiliMovies.get(i).getPreviews().get(progress));
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    cardViewHolder.binding.seekbar.setProgress(0);
                    cardViewHolder.binding.cover.setImageBitmap(bilibiliMovies.get(i).getBitmap());
                }
            });
        }
        cardViewHolder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        if (bilibiliMovies == null) return 0;
        return bilibiliMovies.size();
    }

    class CardViewHolder extends RecyclerView.ViewHolder{

        ItemBilibiliBinding binding;

        CardViewHolder(ItemBilibiliBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }


    }

    public int addItem(BilibiliMovie bilibiliMovie){
        bilibiliMovies.add(bilibiliMovie);
        notifyDataSetChanged();
        return bilibiliMovies.indexOf(bilibiliMovie);
    }

    public void setBitmapByIndex(Bitmap bitmap, int index){
        bilibiliMovies.get(index).setBitmap(bitmap);
        notifyDataSetChanged();
    }

    public void addPreviews(List<Bitmap> previews, int index){
        bilibiliMovies.get(index).addPreviews(previews);
    }

    public void refresh(){
        notifyDataSetChanged();
    }
}
