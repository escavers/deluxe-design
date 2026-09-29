package com.deluxedesign.app.ui.common;

import android.view.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.deluxedesign.app.databinding.ItemCardBinding;
import com.deluxedesign.app.util.AssetImages;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.Holder> {
  public static class Card {
    public final String title, detail, image, badge;
    public final int progress;
    public final Runnable action;
    public final String vehicleId, colorId;

    public Card(
        String title, String detail, String image, String badge, int progress, Runnable action) {
      this(title, detail, image, badge, progress, action, null, null);
    }

    public Card(
        String title,
        String detail,
        String image,
        String badge,
        int progress,
        Runnable action,
        String vehicleId,
        String colorId) {
      this.title = title;
      this.detail = detail;
      this.image = image;
      this.badge = badge;
      this.progress = progress;
      this.action = action;
      this.vehicleId = vehicleId;
      this.colorId = colorId;
    }
  }

  private final List<Card> cards;

  public CardAdapter(List<Card> cards) {
    this.cards = cards;
  }

  @NonNull
  public Holder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
    return new Holder(
        ItemCardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
  }

  public void onBindViewHolder(@NonNull Holder holder, int position) {
    Card c = cards.get(position);
    ItemCardBinding b = holder.binding;
    b.cardTitle.setText(c.title);
    b.cardDetail.setText(c.detail);
    b.badge.setText(c.badge);
    b.badge.setVisibility(c.badge.isEmpty() ? View.GONE : View.VISIBLE);
    b.thumbnail.setVisibility(c.image.isEmpty() ? View.GONE : View.VISIBLE);
    if (!c.image.isEmpty()) {
      if (c.vehicleId != null)
        com.deluxedesign.app.util.VehicleImages.show(b.thumbnail, c.vehicleId, c.colorId);
      else AssetImages.show(b.thumbnail, c.image);
    }
    b.progress.setVisibility(c.progress < 0 ? View.GONE : View.VISIBLE);
    b.progress.setProgress(Math.max(0, c.progress));
    b.getRoot()
        .setOnClickListener(
            v -> {
              if (c.action != null) c.action.run();
            });
  }

  public int getItemCount() {
    return cards.size();
  }

  static class Holder extends RecyclerView.ViewHolder {
    final ItemCardBinding binding;

    Holder(ItemCardBinding b) {
      super(b.getRoot());
      binding = b;
    }
  }
}
