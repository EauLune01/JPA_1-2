package jpabook.jpashop.repository;

import jakarta.persistence.*;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
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
    public List<Order> findAll(OrderSearch orderSearch) {
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
}
