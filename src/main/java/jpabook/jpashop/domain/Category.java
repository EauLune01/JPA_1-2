package jpabook.jpashop.domain;

import jakarta.persistence.*;
import jpabook.jpashop.domain.item.Item;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
@Entity
@Data
public class Category {
    @Id @GeneratedValue
    @Column(name="category_id")
    private Long id;

    private String name;

    private int price;

    private int stockQuantity;

    @ManyToMany
    @JoinTable(name = "category_item",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id"))
    private List<Item> items=new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)  //다대일 관계 매핑 (여러 Category가 하나의 부모 Category를 가질 수 있음)
    @JoinColumn(name="parent_id") //부모 Category의 외래키 컬럼 이름을 parent_id로 지정
    private Category parent;

    @OneToMany(mappedBy = "parent") //일대다 관계 매핑 (부모 Category 입장에서 여러 자식 Category를 가질 수 있음)
    private List<Category> child=new ArrayList<>();


    //==연관관계 메서드==//
    public void addChildCategory(Category child) {
        this.child.add(child);
        child.setParent(this);
    }
}
