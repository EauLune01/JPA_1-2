package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    public void save(Member member){
        //persist()를 호출하면 member가 영속성 컨텍스트에 저장되고, 트랜잭션이 커밋될 때 실제 DB에 insert 쿼리가 나감
        em.persist(member);
    }

    public Member findOne(Long id){
        return em.find(Member.class, id);
    }

    public List<Member> findAll(){
        //JPQL(select m from Member m)을 사용 → SQL이 아니라 엔티티 객체(Member)를 대상으로 조회
        return em.createQuery("select m from Member m",Member.class)
                .getResultList();
    }

    public List<Member> findByName(String name){
        //주어진 name 값과 동일한 이름을 가진 모든 Member 엔티티를 리스트로 반환
        return em.createQuery("select m from Member m where m.name=:name",Member.class)
                .setParameter("name",name)
                .getResultList();
    }
}
