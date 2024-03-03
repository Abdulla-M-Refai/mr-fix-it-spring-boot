package com.Mr.fix.it.Entity;

import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.Mr.fix.it.Entity.Enum.Gender;
import com.Mr.fix.it.Entity.Enum.UserType;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "dob", nullable = false)
    private LocalDate dob;

    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone", unique = true, nullable = false)
    private String phone;

    @Column(name = "img", nullable = false)
    private String img;

    @Column(name = "fcm")
    private String fcm;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType type;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(
        fetch = FetchType.LAZY,
        mappedBy = "user",
        cascade = CascadeType.ALL
    )
    private List<Task> tasks;

    @OneToMany(
        fetch = FetchType.LAZY,
        mappedBy = "user",
        cascade = CascadeType.ALL
    )
    private List<Donation> donations;

    @OneToMany(
        fetch = FetchType.LAZY,
        mappedBy = "user",
        cascade = CascadeType.ALL
    )
    private List<Favorite> favorites;

    @OneToMany(
        fetch = FetchType.LAZY,
        mappedBy = "user",
        cascade = CascadeType.ALL
    )
    private List<Like> likes;

    @OneToMany(
        fetch = FetchType.LAZY,
        mappedBy = "user",
        cascade = CascadeType.ALL
    )
    private List<Comment> comments;

    @OneToMany(
        fetch = FetchType.LAZY,
        mappedBy = "user",
        cascade = CascadeType.ALL
    )
    private List<CanceledTasks> canceledTasks;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return List.of(new SimpleGrantedAuthority(type.name()));
    }

    @Override
    public String getUsername()
    {
        return email;
    }

    @Override
    public String getPassword()
    {
        return password;
    }

    @Override
    public boolean isAccountNonExpired()
    {
        return true;
    }

    @Override
    public boolean isAccountNonLocked()
    {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired()
    {
        return true;
    }

    @Override
    public boolean isEnabled()
    {
        return isActive && isVerified;
    }
}