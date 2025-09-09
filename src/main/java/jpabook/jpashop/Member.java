package jpabook.jpashop;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
/**
 * @Id:해당 필드를 엔티티의 PK로 지정
 * @GeneratedValue: 기본 키 값을 자동으로 생성, GenerationType.AUTO: DB에 맞춰 자동으로 키 생성 전략을 결정
 *
 * */
@Data
public class Member {
    @Id @GeneratedValue
    private Long id;
    private String username;
}
