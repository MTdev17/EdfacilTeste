package com.example.androidteste.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidteste.R;
import com.example.androidteste.model.CategoriaCatalogo;

import java.util.List;

public class CatalogoAdapter extends RecyclerView.Adapter<CatalogoAdapter.MyViewHolder> {

    private final List<CategoriaCatalogo> categoriaList;
    private final Activity activity;

    public CatalogoAdapter(List<CategoriaCatalogo> categoriaList, Activity activity) {
        this.categoriaList = categoriaList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_catalogo, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CategoriaCatalogo categoria = categoriaList.get(position);
        holder.textCategoriaNome.setText(categoria.getNome());

        holder.rvProdutos.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        holder.rvProdutos.setHasFixedSize(true);

        ProdutoCatalogoAdapter produtoCatalogoAdapter = new ProdutoCatalogoAdapter(categoria.getProdutoList(), activity);
        holder.rvProdutos.setAdapter(produtoCatalogoAdapter);

        produtoCatalogoAdapter.notifyDataSetChanged();



    }

    @Override
    public int getItemCount() {
        return categoriaList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textCategoriaNome;
        RecyclerView rvProdutos;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textCategoriaNome = itemView.findViewById(R.id.textCategoriaNome);
            rvProdutos = itemView.findViewById(R.id.rvProdutos);
        }
    }
}
