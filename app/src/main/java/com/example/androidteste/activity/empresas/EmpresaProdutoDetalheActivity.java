package com.example.androidteste.activity.empresas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidteste.DAO.EmpresaDAO;
import com.example.androidteste.DAO.ItemPedidoDAO;
import com.example.androidteste.R;
import com.example.androidteste.activity.usuario.CarrinhoActivity;
import com.example.androidteste.helper.FirebaseHelper;
import com.example.androidteste.helper.GetMask;
import com.example.androidteste.model.Empresa;
import com.example.androidteste.model.ItemPedido;
import com.example.androidteste.model.Produto;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class EmpresaProdutoDetalheActivity extends AppCompatActivity {

    private final int REQUEST_CATALOGO = 100;

    private ImageView img_produto;
    private TextView text_produto;
    private TextView text_descricao;
    private TextView text_valor;
    private TextView text_valor_antigo;
    private TextView text_empresa;
    private TextView text_tempo_entrega;

    private ConstraintLayout btn_adicionar;
    private TextView text_qtd_produto;
    private TextView text_total_produto;
    private ImageButton btn_remover;
    private ImageButton btn_add;

    private Produto produto;
    private Empresa empresa;

    private EmpresaDAO empresaDAO;
    private ItemPedidoDAO itemPedidoDAO;

    private int quantidade = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_produto_detalhe);

        empresaDAO = new EmpresaDAO(getBaseContext());
        itemPedidoDAO = new ItemPedidoDAO(getBaseContext());

        iniciaComponentes();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            produto = (Produto) bundle.getSerializable("produtoSelecionado");


           recuperaEmpresa();

            configDados();
        }
        configCliques();
    }

    private void addItemCarrinho(){
        if(empresaDAO.getEmpresa() != null){
            if(produto.getIdEmpresa().equals(empresaDAO.getEmpresa().getId())){
                salvarProduto();
            }else{
                Snackbar.make(btn_adicionar, "Empresas diferentes", Snackbar.LENGTH_LONG).show();
            }
        }else{
            salvarProduto();
        }

    }

    private void salvarProduto(){
        ItemPedido itemPedido = new ItemPedido();
        itemPedido.setQuantidade(quantidade);
        itemPedido.setUrlImagem(produto.getUrlImagem());
        itemPedido.setValor(produto.getValor());
        itemPedido.setIdItem(produto.getId());
        itemPedido.setItem(produto.getNome());

        itemPedidoDAO.salvar(itemPedido);
        if(empresaDAO.getEmpresa() == null) empresaDAO.salvar(empresa);

        Intent intent = new Intent(new Intent(this, CarrinhoActivity.class));
        startActivityForResult(intent, REQUEST_CATALOGO);

    }

    private void addQtdItem(){
        quantidade++;
        btn_remover.setImageResource(R.drawable.ic_remove_red);

        //Atualiza o saldo : Valor * quantidade
        atualizarSaldo();
    }

    private void delQtdItem(){
        if(quantidade > 1){
            quantidade--;

            if(quantidade == 1){
                btn_remover.setImageResource(R.drawable.ic_remove);
            }

            atualizarSaldo();
        }
    }

    private void atualizarSaldo(){
        text_qtd_produto.setText(String.valueOf(quantidade));
        text_total_produto.setText(getString(R.string.text_valor, GetMask.getValor(produto.getValor() * quantidade)));

    }

    private void recuperaEmpresa(){
        DatabaseReference empresaRef = FirebaseHelper.getDatabaseReference()
                .child("empresa")
                .child(produto.getIdEmpresa());
        empresaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                empresa = snapshot.getValue(Empresa.class);

              text_empresa.setText(empresa.getNome());
              text_tempo_entrega.setText(empresa.getTempominEntrega() + "-" + empresa.getTempomaxEntrega() + "min");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configDados(){
        Picasso.get().load(produto.getUrlImagem()).into(img_produto);
        text_produto.setText(produto.getNome());
        text_descricao.setText(produto.getDescricao());
        text_valor.setText(getString(R.string.text_valor, GetMask.getValor(produto.getValor())));
        text_total_produto.setText(getString(R.string.text_valor, GetMask.getValor(produto.getValor() * quantidade)));

        if(produto.getValor_antigo() > 0){
            text_valor_antigo.setText(getString(R.string.text_valor, GetMask.getValor(produto.getValor_antigo())));
        }else{
            text_valor_antigo.setText("");
        }
    }

    private void configCliques(){
        findViewById(R.id.ib_voltar).setOnClickListener(view -> finish());
        findViewById(R.id.btn_adicionar).setOnClickListener(view -> addItemCarrinho());
        btn_add.setOnClickListener(view -> addQtdItem());
        btn_remover.setOnClickListener(view -> delQtdItem());
    }

    private void iniciaComponentes(){
        TextView text_toolbar = findViewById(R.id.text_toolbar);
        text_toolbar.setText("Detalhes do Produto");

        img_produto = findViewById(R.id.img_produto);
        text_produto = findViewById(R.id.text_produto);
        text_descricao = findViewById(R.id.text_descricao);
        text_valor = findViewById(R.id.text_valor);
        text_valor_antigo = findViewById(R.id.text_valor_antigo);
        text_empresa = findViewById(R.id.text_empresa);
        text_tempo_entrega = findViewById(R.id.text_tempo_entrega);


        btn_adicionar = findViewById(R.id.btn_adicionar);
        text_qtd_produto = findViewById(R.id.text_qtd_produto);
        text_total_produto = findViewById(R.id.text_total_produto);
        btn_remover = findViewById(R.id.btn_remover);
        btn_add = findViewById(R.id.btn_add);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_CATALOGO){
                finish();

            }
        }
    }
}