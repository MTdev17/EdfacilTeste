package com.example.androidteste.fragment.empresa;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.androidteste.R;
import com.example.androidteste.adapter.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class EmpresaPedidoFragment extends Fragment {

    private TabLayout tab_layout;
    private ViewPager view_pager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_empresa_pedido, container, false);

        iniciaComponentes(view);

        configTabsLayout();

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        configTabsLayout();
    }

    private void configTabsLayout(){

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());

        viewPagerAdapter.addFragment(new EmpresaPedidoAndamentoFragment(), "Em andamento");
        viewPagerAdapter.addFragment(new EmpresaPedidoFinalizadoFragment(), "Concluídos");

        view_pager.setAdapter(viewPagerAdapter);
        tab_layout.setElevation(0);
        tab_layout.setupWithViewPager(view_pager);


    }


    private void iniciaComponentes(View view){

        tab_layout = view.findViewById(R.id.tab_layout);
        view_pager = view.findViewById(R.id.view_pager);

    }

}