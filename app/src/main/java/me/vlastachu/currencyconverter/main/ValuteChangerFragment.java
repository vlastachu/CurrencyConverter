package me.vlastachu.currencyconverter.main;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.vlastachu.currencyconverter.R;

public class ValuteChangerFragment extends Fragment {
    public interface OnValuteChanged {
        void onValuteChanged(Boolean changingFromValute, String valuteId);
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
        @Override
        public Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.valute_item, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final ViewModel.Loaded.ValuteItem item = valuteItems.get(position);
            holder.itemText.setText(item.getName());
            holder.itemText.setGravity(item.getLeft() ? Gravity.LEFT : Gravity.RIGHT);
            if (item.getSelected()) {
                holder.itemText.setTypeface(null, Typeface.BOLD);
            } else {
                holder.itemText.setTypeface(null, Typeface.NORMAL);
            }
            if (item.getDisabled()) {
                holder.itemText.setTextColor(0xff7f7f7f);
            } else {
                holder.itemText.setTextColor(0xff0f0f0f);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!item.getSelected() && !item.getDisabled())
                        onValuteChanged.onValuteChanged(changingFromValute, item.getId());
                }
            });
        }

        @Override
        public int getItemCount() {
            return valuteItems.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder  {
            public TextView itemText;
            public ViewHolder(View itemView) {
                super(itemView);
                itemText = (TextView) itemView.findViewById(R.id.item_text);
            }
        }
    }

    private List<ViewModel.Loaded.ValuteItem> valuteItems = new ArrayList<>();
    private Boolean changingFromValute;
    private OnValuteChanged onValuteChanged;
    private RecyclerView recyclerView;
    private Adapter adapter = new Adapter();

    public ValuteChangerFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.valute_changer_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);
    }

    public void setValuteItems(List<ViewModel.Loaded.ValuteItem> valuteItems) {
        this.valuteItems = valuteItems;
        adapter.notifyDataSetChanged();
    }

    public void setChangingFromValute(Boolean changingFromValute) {
        this.changingFromValute = changingFromValute;
    }

    public void setOnValuteChanged(OnValuteChanged onValuteChanged) {
        this.onValuteChanged = onValuteChanged;
    }
}
