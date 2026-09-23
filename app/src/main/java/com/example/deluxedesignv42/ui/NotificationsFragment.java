package com.example.deluxedesignv42.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deluxedesignv42.R;
import com.example.deluxedesignv42.databinding.FragmentNotificationsBinding;
import com.example.deluxedesignv42.model.AppNotification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotificationsFragment extends Fragment {
    private FragmentNotificationsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnBackNotif.setOnClickListener(v -> androidx.navigation.Navigation.findNavController(v).navigateUp());

        List<AppNotification> list = new ArrayList<>();
        list.add(new AppNotification("1", "Proyecto completado", "Tu proyecto BMW M4 está 100% listo para entrega.", new Date(), false));
        list.add(new AppNotification("2", "Cotización aprobada", "Samuel Jimenez aceptó la cotización #042.", new Date(), true));
        list.add(new AppNotification("3", "Nuevo modelo disponible", "Ferrari SF90 ya está disponible para personalizar.", new Date(), false));

        binding.rvNotifications.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvNotifications.setAdapter(new NotifAdapter(list));
    }

    private class NotifAdapter extends RecyclerView.Adapter<NotifAdapter.ViewHolder> {
        private List<AppNotification> list;
        NotifAdapter(List<AppNotification> list) { this.list = list; }
        @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int t) {
            View v = LayoutInflater.from(p.getContext()).inflate(android.R.layout.simple_list_item_2, p, false);
            return new ViewHolder(v);
        }
        @Override public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
            AppNotification n = list.get(pos);
            h.t1.setText(n.getTitle());
            h.t2.setText(n.getMessage());
            h.t1.setTextColor(getResources().getColor(R.color.white));
            h.t2.setTextColor(getResources().getColor(R.color.gray_text));
        }
        @Override public int getItemCount() { return list.size(); }
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView t1, t2;
            ViewHolder(View v) { super(v); t1 = v.findViewById(android.R.id.text1); t2 = v.findViewById(android.R.id.text2); }
        }
    }
}