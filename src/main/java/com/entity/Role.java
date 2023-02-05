package com.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "roles")
@Where(clause = "is_deleted = false")
@NoArgsConstructor
public class Role extends BaseEntity {

    private String description;
}