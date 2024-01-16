package com.enigma.tokopakedi.service;

import com.enigma.tokopakedi.entity.Order;
import com.enigma.tokopakedi.model.request.OrderRequest;
import com.enigma.tokopakedi.model.reponse.OrderResponse;
import com.enigma.tokopakedi.model.request.SearchOrderRequest;
import org.springframework.data.domain.Page;

public interface OrderService {
    OrderResponse createTransactions(OrderRequest request);
    Order createTransaction(OrderRequest request);
    OrderResponse getById(String id);
    Page<Order> getAll(SearchOrderRequest orderRequest);
}
