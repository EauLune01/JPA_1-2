package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

@Entity
@Data
public class Delivery {
    @Id @GeneratedValue
    @Column(name="delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING) // EnumType.ORDINAL 절대 사용하지 말자!
    private DeliveryStatus status; //READY,COMP

}
