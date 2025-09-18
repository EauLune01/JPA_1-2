package jpabook.jpashop.repository.order.query;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    /**
     * V4
     * findOrderQueryDtos()
     *
     * 목적: 단건 조회에서 **루트 엔티티(Order)**는 한 번에 조회하고, **컬렉션(OrderItem)**은 별도로 조회하는 방식.
     *
     * 동작 방식
     *
     * findOrders()를 호출 → Order, Member, Delivery를 한 번에 조회.
     *
     * 각 Order에 대해 findOrderItems(orderId) 호출 → OrderItem 컬렉션 조회.
     *
     * 쿼리 수: 루트 1번 + 컬렉션 N번
     *
     * */

    /**
     * 컬렉션은 별도로 조회
     * Query: 루트 1번, 컬렉션 N번
     * 단건 조회에서 많이 사용하는 방식
     */
    public List<OrderQueryDto> findOrderQueryDtos() {

        // 루트 조회(toOne 관계: Member, Delivery)
        List<OrderQueryDto> result = findOrders();

        // 루프를 돌면서 1:N 관계인 컬렉션(OrderItem) 추가 (이게 바로 단점! 루프를 돈다!)
        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });

        return result;
    }

    /**
     * 1:N 관계(컬렉션)를 제외한 나머지를 한번에 조회
     * Member, Delivery 등 ToOne 관계를 한 번에 조회
     */
    private List<OrderQueryDto> findOrders() {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderQueryDto(" +
                        "o.id, m.name, o.orderDate, o.status, d.address" +
                        ") " +
                        "from Order o " +
                        "join o.member m " +     // Order와 Member를 조인
                        "join o.delivery d",     // Order와 Delivery를 조인
                OrderQueryDto.class
        ).getResultList();
    }

    /**
     * 1:N 관계인 OrderItems 조회
     * 각 Order의 orderId를 기준으로 OrderItem 컬렉션 조회
     */
    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                        "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(" +
                                "oi.order.id, i.name, oi.orderPrice, oi.count" +
                                ") " +
                                "from OrderItem oi " +
                                "join oi.item i " +       // OrderItem과 Item을 조인
                                "where oi.order.id = :orderId", // 파라미터 바인딩
                        OrderItemQueryDto.class
                )
                .setParameter("orderId", orderId)
                .getResultList();
    }


    //v5: 쿼리 2번

    /**
     * 최적화
     * Query: 루트 1번, 컬렉션 1번
     * 데이터를 한꺼번에 처리할 때 많이 사용하는 방식
     *
     */
    public List<OrderQueryDto> findAllByDto_optimization() {
        // 1. 루트 조회(toOne 관계: Member, Delivery)
        List<OrderQueryDto> result = findOrders();

        // 2. OrderItem 컬렉션을 한 번에 조회하여 Map으로 변환
        Map<Long, List<OrderItemQueryDto>> orderItemMap =
                findOrderItemMap(toOrderIds(result));

        // 3. 루프를 돌면서 DTO에 컬렉션 세팅 (추가 쿼리 실행 없음)
        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return result;
    }

    /**
     * OrderQueryDto 리스트에서 OrderId만 추출
     */
    private List<Long> toOrderIds(List<OrderQueryDto> result) {
        return result.stream()
                .map(OrderQueryDto::getOrderId)
                .collect(Collectors.toList());
    }

    /**
     * OrderId 리스트를 기반으로 OrderItem 컬렉션 조회 후 Map으로 그룹핑
     */
    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
        List<OrderItemQueryDto> orderItems = em.createQuery(
                        "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(" +
                                "oi.order.id, i.name, oi.orderPrice, oi.count" +
                                ") " +
                                "from OrderItem oi " +
                                "join oi.item i " +
                                "where oi.order.id in :orderIds", //in절 활용, orderId->orderIds
                        OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();

        // OrderId 기준으로 Map으로 그룹핑
        return orderItems.stream()
                .collect(Collectors.groupingBy(OrderItemQueryDto::getOrderId));
    }

    //v6: 쿼리 1번

    public List<OrderFlatDto> findAllByDto_flat() {
        return em.createQuery(
                        "select new jpabook.jpashop.repository.order.query.OrderFlatDto(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count)" +
                        " from Order o" +
                                " join o.member m" +
                                " join o.delivery d" +
                                " join o.orderItems oi" +
                                " join oi.item i", OrderFlatDto.class)
                .getResultList();
    }

}