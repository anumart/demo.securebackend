package com.example.demo.securebackend.repository;

import com.example.demo.securebackend.repository.entity.User;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @NonNull
    Optional<User> findByUsername(String username);

    @Modifying // บอกว่าเป็น Query ที่เปลี่ยนแปลงข้อมูล
    @Transactional // ต้องทำงานภายใต้ Transaction
    @Query(value = "UPDATE users SET first_name = :firstName, last_name = :lastName, email = :email, role = :role, modified_by = :modifiedBy, modified_date = current_timestamp WHERE id = :id", nativeQuery = true)
    int update(@Param("firstName") String firstName, @Param("lastName") String lastName, @Param("email") String email, @Param("role") String role, @Param("modifiedBy") Long modifiedBy, @Param("id") Long id);

    @Modifying // บอกว่าเป็น Query ที่เปลี่ยนแปลงข้อมูล
    @Transactional // ต้องทำงานภายใต้ Transaction
    @Query(value = "UPDATE users SET refresh_token = :refreshToken WHERE id = :id", nativeQuery = true)
    void updateRefreshToken(@Param("refreshToken") String refreshToken, @Param("id") Long id);

}
