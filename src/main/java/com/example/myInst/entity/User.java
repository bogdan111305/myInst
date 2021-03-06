package com.example.myInst.entity;

import com.example.myInst.entity.enums.ERole;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Data //Создает гетеры и сетеры автоматически
@Entity //Для установки зависимостей hibernates
@Table(name="\"user\"")
public class User implements UserDetails {

    @Id//Определяет итендефикатор
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)//Говорит что объект no может быть пустым
    private String name;
    @Column(unique = true, updatable = false)//Говорит что объект имеет уникальные значения и его нельзя обновить
    private String username;
    @Column(nullable = false)//Говорит что объект no может быть пустым
    private String lastname;
    @Column(unique = true)//Говорит что объект имеет уникальные значения
    private String email;
    @Column(columnDefinition = "text")//В базе храниться как тип текст
    private String bio;

    @Column(length = 3000)//Длина пароля 3000 символов(введенный пользователем пароль будет кодироваться)
    private String password;

    @ElementCollection(targetClass = ERole.class)
    @CollectionTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"))//Создаем таблицу в базе данных с двумя полями
    private Set<ERole> roles = new HashSet<>();

    // каскад - удаляем пользователя, удаляются все посты / фетч = не нужно получать все посты при получении данных пользователя
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user", orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss")// format time
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @Transient
    private Collection<? extends GrantedAuthority>  authorities;

    public User(){

    }

    public User(Long id,
                String username,
                String email,
                String password,
                Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    @PrePersist //Задает значение атрибута до того как будет сделана запись в базу данных
    protected void onCreate(){
        this.createdDate = LocalDateTime.now();
    }

    // *Security

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
