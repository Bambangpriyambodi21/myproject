package com.enigma.tokopakedi.repository;

import com.enigma.tokopakedi.entity.Order;
import com.enigma.tokopakedi.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, String> {
}
