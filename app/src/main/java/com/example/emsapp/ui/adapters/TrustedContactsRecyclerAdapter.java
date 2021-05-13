package com.example.emsapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emsapp.R;
import com.example.emsapp.model.TrustedContact;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TrustedContactsRecyclerAdapter extends RecyclerView.Adapter<TrustedContactsRecyclerAdapter.TrustedContactViewHolder> {

    private final List<TrustedContact> trustedContacts;
    private final ContactsDeleteListener deleteListener;

    public TrustedContactsRecyclerAdapter(List<TrustedContact> trustedContacts, ContactsDeleteListener deleteListener) {
        this.trustedContacts = trustedContacts;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @NotNull
    @Override
    public TrustedContactViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_trusted_contacts, parent, false);

        return new TrustedContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull TrustedContactsRecyclerAdapter.TrustedContactViewHolder holder, int position) {
        TrustedContact contact = trustedContacts.get(position);
        holder.textViewName.setText(contact.getName());
        holder.textViewPhone.setText(contact.getPhoneNo());

        holder.imageViewDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(contact);
            }
        });
    }

    @Override
    public int getItemCount() {
        return trustedContacts.size();
    }

    public interface ContactsDeleteListener {
        void onDelete(TrustedContact contact);
    }

    static class TrustedContactViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewName;
        private final TextView textViewPhone;
        private final ImageView imageViewDelete;

        public TrustedContactViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewPhone = itemView.findViewById(R.id.textViewPhone);
            imageViewDelete = itemView.findViewById(R.id.imageViewDelete);
        }
    }
}
