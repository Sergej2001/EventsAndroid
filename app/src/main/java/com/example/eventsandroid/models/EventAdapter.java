package com.example.eventsandroid.models;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventsandroid.R;
import com.example.eventsandroid.activities.IzmeniActivity;
import com.example.eventsandroid.services.ApiClient;
import com.example.eventsandroid.services.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventList;
    private Context context;

    public EventAdapter(List<Event> eventList, Context context) {
        this.eventList = eventList;
        this.context = context;
    }
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.nameTextView.setText(event.getName());
        holder.descriptionTextView.setText(event.getDescription());
        holder.locationTextView.setText(event.getLocation());
        holder.dateTextView.setText(event.getDate().toString());

        // Click listener for delete icon
        holder.imageViewDelete.setOnClickListener(v -> {
            // Implement delete functionality here
            deleteEvent(event, position);
            // You can also notify adapter about the removed item
        });

        // Click listener for the whole item (edit functionality)
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, IzmeniActivity.class);
            intent.putExtra("event_id", event.getId()); // Pass event ID to edit activity
            context.startActivity(intent);
        });
    }

    private void deleteEvent(Event event, int position) {
        Call<Void> call = ApiClient.getRetrofitInstance(context).create(ApiService.class).deleteEvent(event.getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Remove the event from the list
                    eventList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, eventList.size());

                    // Display a confirmation message
                    Toast.makeText(context, "Deleted: " + event.getName(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to delete event", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView descriptionTextView;
        TextView locationTextView;
        TextView dateTextView;
        ImageView imageViewDelete;

        EventViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textViewNameValue);
            descriptionTextView = itemView.findViewById(R.id.textViewDescriptionValue);
            locationTextView = itemView.findViewById(R.id.textViewLocationValue);
            dateTextView = itemView.findViewById(R.id.textViewDateValue);
            imageViewDelete = itemView.findViewById(R.id.imageViewDelete);
        }
    }
}