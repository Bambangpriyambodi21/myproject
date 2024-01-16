package com.enigma.tokopakedi.service.impl;

import com.enigma.tokopakedi.entity.Customer;
import com.enigma.tokopakedi.entity.Order;
import com.enigma.tokopakedi.entity.OrderDetail;
import com.enigma.tokopakedi.entity.Product;
import com.enigma.tokopakedi.model.reponse.OrderDetailResponse;
import com.enigma.tokopakedi.model.reponse.OrderResponse;
import com.enigma.tokopakedi.model.reponse.PagingResponse;
import com.enigma.tokopakedi.model.reponse.ProductResponse;
import com.enigma.tokopakedi.model.request.OrderDetailRequest;
import com.enigma.tokopakedi.model.request.OrderRequest;
import com.enigma.tokopakedi.model.request.SearchOrderRequest;
import com.enigma.tokopakedi.repository.OrderRepository;
import com.enigma.tokopakedi.repository.ProductRepository;
import com.enigma.tokopakedi.service.CustomerService;
import com.enigma.tokopakedi.service.OrderDetailService;
import com.enigma.tokopakedi.service.OrderService;
import com.enigma.tokopakedi.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailService orderDetailService;
    private final CustomerService customerService;
    private final ProductService productService;
    private final ProductRepository productRepository;



    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse createTransactions(OrderRequest request) {
//        Customer customer = customerService.readIdByCustomer(request.getCustomerId());
        Customer customer1 = Customer.builder()
                .id(request.getCustomerId())
                .build();

        Order order = Order.builder()
                .customer(customer1)
                .date(new Date())
                .build();
        orderRepository.saveAndFlush(order);

        List<OrderDetailResponse> orderDetailsa = new ArrayList<>();

        for (OrderDetailRequest orderDetailRequest : request.getOrderDetails()) {

            Product product = productService.readIdProduct(orderDetailRequest.getProductId());

            OrderDetail orderDetail = OrderDetail.builder()
                    .order(order)
                    .product(product)
                    .productPrice(product.getPrice())
                    .quantity(orderDetailRequest.getQuantity())
                    .build();

            if (product.getStock() - orderDetail.getQuantity() < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "quantity exceed");
            }

            product.setStock(product.getStock() - orderDetail.getQuantity());
            productService.update(product);

            orderDetailService.createOrUpdate(orderDetail);
            OrderDetailResponse orderDetailResponse = OrderDetailResponse.builder()
                    .id(orderDetail.getId())
                    .orderId(order.getId())
                    .product(product)
                    .productPrice(product.getPrice())
                    .quantity(orderDetailRequest.getQuantity())
                    .build();
            orderDetailsa.add(orderDetailResponse);
        }

//        order.setOrderDetails(orderDetails);
        OrderResponse orderResponse = OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomer().getId())
                .transDate(order.getDate())
                .orderDetails(orderDetailsa)
                .build();

        return orderResponse;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order createTransaction(OrderRequest request) {
//        Customer customer = customerService.readIdByCustomer(request.getCustomerId());
//        Order order = Order.builder()
//                .customer(customer)
//                .date(new Date())
//                .build();
//        orderRepository.saveAndFlush(order);
//
//        List<OrderDetail> orderDetails = new ArrayList<>();
//
//        for (OrderDetailRequest orderDetailRequest : request.getOrderDetails()){
//            Product product = productService.readIdByProduct(orderDetailRequest.getProductId());
//
//            OrderDetail orderDetail = OrderDetail.builder()
//                    .order(order)
//                    .product(product)
//                    .productPrice(product.getPrice())
//                    .quantity(orderDetailRequest.getQuantity())
//                    .build();
//
//            if (product.getStock()-orderDetail.getQuantity()<0){
//                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "quantity exceed");
//            }
//
//            product.setStock(product.getStock()-orderDetailRequest.getQuantity());
//            productService.update(product);
//
//            orderDetails.add(orderDetail);
//        }
//
//        order.setOrderDetails(orderDetails);
//
//        return order;
        return null;
    }

    @Override
    public OrderResponse getById(String id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"order not found"));
        OrderResponse orderResponse = OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomer().getId())
                .transDate(order.getDate())
                .orderDetails(order)
                .build();
        return orderResponse;
    }

    @Override
    public Page<Order> getAll(SearchOrderRequest orderRequest) {
        if (orderRequest.getPage()<=0)orderRequest.setPage(1);
        Pageable pageable = PageRequest.of(orderRequest.getPage(), orderRequest.getSize());

        return orderRepository.findAll(pageable);
    }
}
