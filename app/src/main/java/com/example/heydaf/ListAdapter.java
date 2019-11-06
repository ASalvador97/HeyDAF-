package com.example.heydaf;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Functionality> items;
    private MainActivity mainActivity;

    public ListAdapter(List<Functionality> items, MainActivity mainActivity) {
        this.items = items;
        this.mainActivity = mainActivity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = ((ViewHolder) holder);
            final Functionality item = items.get(position);

            // set values of the listitem
            viewHolder.functionalityName.setText(item.getName());
            viewHolder.functionalitySwitch.setChecked(item.getStatus() == 1);
            item.setFunctionalitySwitch(viewHolder.functionalitySwitch);
            viewHolder.functionalitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        item.activate();
                    } else {
                        item.deactivate();
                    }
                    if (mainActivity.bluetoothConnector.BluetoothConnected) {
                        mainActivity.sendMessage();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Viewholder to easily set values of the UI elements in the adapter
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView functionalityName;
        Switch functionalitySwitch;
        final View mView;

        ViewHolder(View view) {
            super(view);
            mView = view;
            this.functionalityName = view.findViewById(R.id.functionName);
            //functionalityName.setTextColor(R.color.white);
            this.functionalitySwitch = view.findViewById(R.id.functionSwitch);
        }
    }
}

