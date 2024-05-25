package com.ticketcheater.webservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(indexes = @Index(name = "idx_payment_id_deletedAt", columnList = "id, deleted_at"))
@Entity(name = "payment")
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToOne(mappedBy = "payment")
    private Ticket ticket;

    @Enumerated(EnumType.STRING)
    @Column(name = "method")
    private PaymentMethod method;

    @Column(name = "amount")
    private int amount;

    public static Payment of(Member member, PaymentMethod method, int amount) {
        Payment payment = new Payment();
        payment.setMember(member);
        payment.setMethod(method);
        payment.setAmount(amount);
        return payment;
    }

}
