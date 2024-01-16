package com.enigma.tokopakedi.repository;

import com.enigma.tokopakedi.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {
}
