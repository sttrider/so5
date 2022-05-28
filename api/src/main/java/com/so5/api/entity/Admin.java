package com.so5.api.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "admin_a")
@DiscriminatorValue("1")
@Getter
@Setter
public class Admin extends User {
}
