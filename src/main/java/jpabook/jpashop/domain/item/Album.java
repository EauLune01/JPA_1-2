package jpabook.jpashop.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;

@Entity
@Getter
@DiscriminatorValue("A") // 상속 매핑 시, 구분 컬럼(DTYPE 등)에 이 엔티티가 저장될 때 들어갈 값을 "A"로 지정
public class Album extends Item{
    private String artist;
    private String etc;
}
