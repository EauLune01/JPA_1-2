package jpabook.jpashop.repository.order.simplequery;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {
    private final EntityManager em;

    /**
     * Order 엔티티와 그와 연관된 Member, Delivery 정보를 DTO 객체로 변환하여 조회하는 메서드
     * 재사용 불가 but 성능 더 최적화
     * */
    public List<OrderSimpleQueryDto> findOrderDtos() {
        return em.createQuery(
                        "select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" + // Order, Member, Delivery에서 필요한 데이터만 추출하여 DTO 생성
                                " from Order o" +  // Order 엔티티
                                " join o.member m" +  // Order와 Member 연관 관계를 JOIN
                                " join o.delivery d",  // Order와 Delivery 연관 관계를 JOIN
                        OrderSimpleQueryDto.class)  // 결과를 OrderSimpleQueryDto로 변환
                .getResultList();
    }
}
