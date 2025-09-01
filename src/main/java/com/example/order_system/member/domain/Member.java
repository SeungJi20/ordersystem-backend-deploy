package com.example.order_system.member.domain;

import com.example.order_system.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@ToString
@Entity
@Builder
// jpql을 제외하고 모든 조회쿼리에 where del_yn = "N" 을 붙이는 효과
@Where(clause = "del_yn = 'N' ") // 이게 없으면 할때마다 findAllAndDel_yn이런식으로 해야해서
// jpql은 상관없다는 것!!! Y인것만 찾고 싶으면 jpql을 만들어야
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(length=50,  unique = true, nullable=false)
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER;

    @Builder.Default
    private String delYn = "N";

    public void deleteMember(String delYn){
        this.delYn = delYn;
    }

//    public void setDelYn(String delYn) {
//        this.delYn = delYn;
//    }
}
