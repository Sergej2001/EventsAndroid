package com.example.eventsandroid.models;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
    private SharedPreferences sharedPreferences;

    public EventAdapter(List<Event> eventList, Context context) {
        this.eventList = eventList;
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
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
        holder.authorTextView.setText(event.getCreatedBy() != null ? event.getCreatedBy().getUsername() : "N/A");

        String imageUrl = generateImageUrl(position);

        Glide.with(context)
                .load(imageUrl)
                .into(holder.imageViewEvent);

        String roles = sharedPreferences.getString("roles", "");

        if (roles.contains("ROLE_ADMIN")) {
            holder.imageViewDelete.setVisibility(View.VISIBLE);
            holder.imageViewDelete.setOnClickListener(v -> deleteEvent(event, position));
        } else {
            holder.imageViewDelete.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (roles.contains("ROLE_ADMIN") || roles.contains("ROLE_USER")) {
                Intent intent = new Intent(context, IzmeniActivity.class);
                intent.putExtra("event_id", event.getId());
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Samo ulogovani korisnici mogu da menjaju događaje", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteEvent(Event event, int position) {
        Call<Void> call = ApiClient.getRetrofitInstance(context).create(ApiService.class).deleteEvent(event.getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    eventList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, eventList.size());

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
        TextView authorTextView;
        ImageView imageViewDelete;
        ImageView imageViewEvent;

        EventViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textViewNameValue);
            descriptionTextView = itemView.findViewById(R.id.textViewDescriptionValue);
            locationTextView = itemView.findViewById(R.id.textViewLocationValue);
            dateTextView = itemView.findViewById(R.id.textViewDateValue);
            authorTextView = itemView.findViewById(R.id.textViewAuthorValue);
            imageViewDelete = itemView.findViewById(R.id.imageViewDelete);
            imageViewEvent = itemView.findViewById(R.id.imageViewEvent);
        }
    }

    private String generateImageUrl(int position) {
        return "https://picsum.photos/seed/" + position+1 + "/200/200";
    }
}