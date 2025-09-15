package jpabook.jpashop.domain;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable // 다른 엔티티 안에 내장(embedded) 되어 함께 매핑되는 값 객체(Value Object)
public class Address {
    private String city;
    private String street;
    private String zipcode;
}
