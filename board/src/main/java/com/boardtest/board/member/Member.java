package com.boardtest.board.member;

import com.boardtest.board.audit.Auditable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import javax.annotation.processing.Generated;

@Entity
public class Member extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // (= AUTO_INCREMENT), 기본 키 생성을 데이터베이스에 위임
    private long id;

}
