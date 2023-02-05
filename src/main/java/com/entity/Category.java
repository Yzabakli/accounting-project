package com.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "categories")
@Where(clause = "is_deleted = false")
@NoArgsConstructor
public class Category extends BaseEntity {

    private String description;

    @ManyToOne
    @JoinColumn(name = "company_id", updatable = false)
    private Company company;
}