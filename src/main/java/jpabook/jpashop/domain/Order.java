package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name="orders") //엔티티를 매핑할 데이터베이스 테이블 이름을 "orders"로 지정
@Data
public class Order {
    @Id @GeneratedValue
    @Column(name="order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) //cascade=부모 엔티티의 상태 변화를 자식 엔티티에도 자동으로 전파하는 기능
    private List<OrderItem> orderItems=new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name="delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate; //주문 시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; //주문상태 [ORDER, CANCEL]
}
