package com.pineapple.softgroup.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pineapple.softgroup.DB.DBHelperContact;
import com.pineapple.softgroup.R;
import com.pineapple.softgroup.interfaces.IFruitImgButton;
import com.pineapple.softgroup.DB.model.Contacts;

import java.util.ArrayList;
import java.util.List;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> implements IFruitImgButton {

    private Contacts userContact;
    private List<Contacts> contacts;
    private DBHelperContact dbHelperContact;
    private Context context;


    public RecyclerAdapter(ArrayList<Contacts> contacts, Context context) {
        this.contacts = contacts;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.nameTV.setText(contacts.get(position).getName());
        holder.numberTV.setText(contacts.get(position).getNumber());
        holder.imgEdit.setVisibility(View.INVISIBLE);
        holder.imgDelete.setVisibility(View.INVISIBLE);


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                holder.imgDelete.setVisibility(View.VISIBLE);
                holder.imgEdit.setVisibility(View.VISIBLE);
                holder.call.setVisibility(View.INVISIBLE);
                return true;
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.imgDelete.setVisibility(View.INVISIBLE);
                holder.imgEdit.setVisibility(View.INVISIBLE);
                holder.call.setVisibility(View.VISIBLE);
            }
        });

        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               callMethod(position);
            }
        });

        //button after maintenance
        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { fruitDelete(position, v); }
        });

        holder.imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) { edit(position, userContact, v); }
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public void callMethod(final int position) {
        try {
            Intent intentCall = new Intent();
            intentCall.setAction(Intent.ACTION_CALL);
            intentCall.setData(Uri.parse("tel:" + contacts.get(position).getNumber()));
            context.startActivity(intentCall);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //delet method
    @Override
    public void fruitDelete(int position, View v) {
        dbHelperContact = new DBHelperContact(v.getContext());
        userContact = contacts.get(position);
        dbHelperContact.deleteConatct(userContact);

        contacts.remove(position);
        RecyclerAdapter.this.notifyItemRemoved(position);
    }

    //edit method
    @Override
    public void edit(final int position, Contacts contacts, final View v) {
        dbHelperContact = new DBHelperContact(v.getContext());

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());

        Context context = v.getContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText inputName = new EditText(v.getContext());
        inputName.setHint("Enter name");
        inputName.setText(RecyclerAdapter.this.contacts.get(position).getName());
        layout.addView(inputName);

        final TextView inputView = new TextView(v.getContext());
        inputView.setText("-------------------------------------------");
        layout.addView(inputView);

        final EditText inputPrice = new EditText(v.getContext());
        inputPrice.setHint("Enter number");
        inputPrice.setText(RecyclerAdapter.this.contacts.get(position).getNumber());
        layout.addView(inputPrice);

        alertDialog.setView(layout);

        alertDialog.setPositiveButton("change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (inputName.getText().toString().length() < 3) {
                                Toast.makeText(v.getContext(), "name short",
                                Toast.LENGTH_LONG).show();
                    if (validPrice(inputPrice.getText().toString())) {
                        RecyclerAdapter.this.contacts.get(position).setNumber(inputPrice.getText().toString());
                        dbHelperContact.updateContatct(RecyclerAdapter.this.contacts.get(position));
                        notifyDataSetChanged();
                    } else {    Toast.makeText(v.getContext(), "invalid price! enter number!",
                                Toast.LENGTH_LONG).show(); }
                } else {
                    RecyclerAdapter.this.contacts.get(position).setName(inputName.getText().toString());
                    if (validPrice(inputPrice.getText().toString())) {
                        RecyclerAdapter.this.contacts.get(position).setNumber(inputPrice.getText().toString());
                    } else {
                                Toast.makeText(v.getContext(), "invalid price! enter number!",
                                Toast.LENGTH_LONG).show(); }

                    dbHelperContact.updateContatct(RecyclerAdapter.this.contacts.get(position));
                    notifyDataSetChanged();
                }
            }
        });
        alertDialog.setNegativeButton("back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.setCancelable(true);
            }
        });
        alertDialog.show();

    }

    public boolean validPrice(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTV;
        public TextView numberTV;
        public ImageButton imgDelete, imgEdit, call;

        public ViewHolder(View v) {
            super(v);
            nameTV = (TextView) v.findViewById(R.id.nameTV);
            numberTV = (TextView) v.findViewById(R.id.numberTV);

            imgDelete = (ImageButton) v.findViewById(R.id.imgDelete);
            imgEdit = (ImageButton) v.findViewById(R.id.imgEdit);
            call = (ImageButton)v.findViewById(R.id.call);
        }
    }
}
