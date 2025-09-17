package jpabook.jpashop.repository;

import jakarta.persistence.*;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.*;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    /**
     * 주문 저장
     */
    public void save(Order order) {
        em.persist(order);
    }

    /**
     * 주문 단건 조회
     */
    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    /**
     * JPQL을 이용한 주문 조회 (동적 쿼리 - 문자열 처리)
     */
    public List<Order> findAllByString(OrderSearch orderSearch) {
        // 기본 JPQL
        String jpql = "select o from Order o join o.member m";
        boolean isFirstCondition = true;

        // 주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }

        // 회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000); // 최대 1000건

        if (orderSearch.getOrderStatus() != null) {
            query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query.setParameter("name", "%" + orderSearch.getMemberName() + "%");
        }

        return query.getResultList();
    }

    /**
     * Criteria API를 이용한 주문 조회 (JPA 표준 동적 쿼리)
     */
    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);

        Root<Order> o = cq.from(Order.class);
        Join<Order, Member> m = o.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();

        // 주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }

        // 회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name = cb.like(m.get("name"), "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[0])));

        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); // 최대 1000건
        return query.getResultList();
    }

    /**
     * Order와 그에 관련된 Member 및 Delivery 데이터를 Fetch Join으로 한 번에 조회
     * inner join, 재사용 굿
     * */
    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery(
                        "select o from Order o" +
                                " join fetch o.member m" +  // Order와 Member를 JOIN FETCH
                                " join fetch o.delivery d",  // Order와 Delivery를 JOIN FETCH
                        Order.class)
                .getResultList();
    }

    /**
     * findAllwithItem():
     *
     * 이 메서드는 Order와 그와 관련된 Member, Delivery, OrderItem, Item 정보를 모두 한 번에 조회합니다.
     *
     * distinct로 중복된 Order 객체를 제거합니다.
     *
     * findAllWithMemberDelivery():
     *
     * 이 메서드는 Order, Member, Delivery 데이터를 조회하면서 페이징 처리를 적용합니다.
     *
     * offset과 limit을 사용하여 데이터의 일부만을 조회합니다.
     *
     */

    /**
     * Order와 관련된 Member, Delivery, OrderItem, Item을 Fetch Join 방식으로 모두 조회
     * Order와 관련된 Member, Delivery는 기존과 동일하게 join fetch를 사용하여 즉시 로딩
     * Order와 OrderItem을 join fetch하여 Order 객체에 연결된 OrderItem들을 한 번에 가져옴
     * OrderItem과 관련된 Item도 join fetch하여 OrderItem 객체와 관련된 Item도 즉시 로딩
     * distinct 키워드는 중복된 Order 객체를 제거하기 위해 사용
     * */
    public List<Order> findAllwithItem() {
        return em.createQuery(
                        "select distinct o from Order o" +  // 중복된 Order를 제거
                                " join fetch o.member m" +  // Order와 Member를 JOIN FETCH
                                " join fetch o.delivery d" + // Order와 Delivery를 JOIN FETCH
                                " join fetch o.orderItems oi" + // Order와 OrderItems를 JOIN FETCH
                                " join fetch oi.item i", Order.class) // OrderItems와 Item을 JOIN FETCH
                .getResultList();
    }

    /**
     * Order, Member, Delivery 데이터를 조회하면서 페이징 처리(offset과 limit)를 적용
     * 기본적으로 Order, Member, Delivery 엔티티를 join fetch를 사용하여 조회
     * setFirstResult(offset)는 결과 목록에서 시작할 인덱스를 설정
     * setMaxResults(limit)는 한 번에 가져올 최대 결과 수를 설정
     * */
    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery(
                        "select o from Order o" +
                                " join fetch o.member m" +  // Order와 Member를 JOIN FETCH
                                " join fetch o.delivery d", Order.class)
                .setFirstResult(offset)  // 페이징 처리: 첫 번째 결과의 인덱스 (오프셋)
                .setMaxResults(limit)    // 페이징 처리: 한 번에 반환할 최대 결과 개수
                .getResultList();
    }

}
