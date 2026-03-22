package com.shop.mapper.user;

import org.apache.ibatis.annotations.Param;

import com.shop.domain.Payment;

public interface PaymentMapper {
	void insertReadyPayment(Payment payment);

    void completePayment(
            @Param("orderNo") Long orderNo,
            @Param("paymentKey") String paymentKey,
            @Param("pgTid") String pgTid
    );

    void failPayment(
            @Param("orderNo") Long orderNo,
            @Param("failReason") String failReason
    );
}
