package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Member {

    @Id @GeneratedValue
    @Column(name="member_id")
    /**
     * @Id: 해당 필드를 엔티티의 기본 키(PK)로 지정
     * @GeneratedValue: 기본 키 값을 자동으로 생성 (전략은 옵션으로 지정 가능)
     * @Column(name="member_id"): DB테이블에서 컬럼 이름을 "member_id"로 매핑
     * */
    private Long id;

    private String name;

    @Embedded // @Embeddable 로 정의된 값 타입 객체를 엔티티에 포함시킬 때 사용
    private Address address;

    @OneToMany(mappedBy = "member")// 일대다 연관관계에서 주인이 아님을 표시, 외래키는 "member" 필드가 선언된 엔티티가 관리
    private List<Order> orders = new ArrayList<>();


}
