package com.boardtest.board.audit;

// JPA 를 통해 entity 와 테이블들을 매핑할 때 공통적으로 도메인들이 가지고 있는
// 컬럼(필드)이 존재한다. 생성일, 수정일 등 공통적인 컬럼이 존재하는데 이러한 컬럼들을
// 공통으로 가지기 위해 Auditing 을 이용.

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@MappedSuperclass // JPA 엔티티들이 해당 클래스를 상속받았을 때 필드들을 컬럼으로 인식
@EntityListeners(AuditingEntityListener.class)
public class Auditable {
    // 생성 날짜
    // 수정 불가
    @CreatedDate
    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // 수정 날짜
    // 수정 가능
    @LastModifiedDate
    @Column(name = "MODIFIED_AT")
    private LocalDateTime modifiedAt;
}
