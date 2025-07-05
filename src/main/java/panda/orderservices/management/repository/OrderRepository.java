package panda.orderservices.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import panda.orderservices.management.entities.Orders;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Integer> {

}
