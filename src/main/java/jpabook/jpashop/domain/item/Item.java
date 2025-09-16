package jpabook.jpashop.domain.item;

import jakarta.persistence.*;
import jpabook.jpashop.domain.Category;
import jpabook.jpashop.domain.exception.NotEnoughStockException;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // 상속 관계 매핑 시, 모든 자식 엔티티를 하나의 테이블에 통합해서 저장하는 전략
@DiscriminatorColumn(name="dtype")
@Getter
public abstract class Item {
    @Id @GeneratedValue
    @Column(name="item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories=new ArrayList<>();

    //==비즈니스 로직==//

    //stock 증가
    public void addStock(int quantity){
        this.stockQuantity+=quantity;
    }

    //stock 감소
    public void removeStock(int quantity){
        int restStock=this.stockQuantity-quantity;
        if(restStock<0){
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity=restStock;
    }
}
