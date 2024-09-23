package ru.practicum.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.user.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>  {
    boolean existsByEmail(String email);

    List<User> findAllByIdIn(List<Integer> ids);
}
