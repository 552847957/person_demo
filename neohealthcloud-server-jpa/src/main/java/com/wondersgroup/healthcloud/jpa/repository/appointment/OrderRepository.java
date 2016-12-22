package com.wondersgroup.healthcloud.jpa.repository.appointment;

import com.wondersgroup.healthcloud.jpa.entity.appointment.AppointmentOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


/**
 * Created by longshasha on 16/3/7.
 */
public interface OrderRepository extends JpaRepository<AppointmentOrder, String> {

    @Query("select a from AppointmentOrder a where a.orderId = ?1")
    AppointmentOrder findOrderByOrderId(String orderId);


    @Modifying
    @Query("update AppointmentOrder a set a.orderStatus = '9',a.closeSms = ?1 where a.id = ?2 ")
    void updateOrderWhencloseNumberSource(String closeSms,String orderId);
}
