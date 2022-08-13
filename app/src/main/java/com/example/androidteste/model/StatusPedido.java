package com.example.androidteste.model;

import java.io.Serializable;

public enum StatusPedido implements Serializable {
    ENTREGUE, PENDENTE, SEPARAÇÃO, SAIU_ENTREGA, CANCELADO_EMPRESA, CANCELADO_USUÁRIO;

    public static String getStatus(StatusPedido statusPedido){
        String status;

        switch (statusPedido){
            case ENTREGUE:
                status = "Pedido entregue";
                break;
            case SEPARAÇÃO:
                status = "Pedido em separação";
                break;
            case SAIU_ENTREGA:
                status = "Pedido saiu para a entrega";
                break;
            case CANCELADO_EMPRESA:
                status = "Pedido cancelado pela empresa";
                break;
            case CANCELADO_USUÁRIO:
                status = "Pedido cancelado pelo usuário";
                break;
            default: status = "Pedido pendente";
                break;
        }
        return status;
    }
}
