package jpabook.jpashop;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
/**
 * @PersistenceContext: JPA에서 EntityManager를 주입받을 때 사용되는 애노테이션
 * EntityManager: JPA에서 엔티티 객체를 관리하고 데이터베이스에 CRUD 작업을 수행
 * @PersistenceContext를 사용하면 스프링이 자동으로 EntityManager를 주입
 * 또한, JPA의 컨텍스트에 맞춰 엔티티의 상태를 관리
 * */
@Repository
public class MemberRepository {
    @PersistenceContext
    private EntityManager em;

    public Long save(Member member){
        em.persist(member);
        return member.getId(); //Member 객체를 DB에 저장하고 저장된 객체의 id를 반환
    }

    public Member find(Long id) {
        return em.find(Member.class, id); // 주어진 id에 해당하는 Member 객체를 조회
    }
}
