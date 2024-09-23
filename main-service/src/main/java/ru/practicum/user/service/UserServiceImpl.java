package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.NotUniqueException;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto create(NewUserRequest newUserRequest) {
        if (userRepository.existsByEmail(newUserRequest.getEmail())) {
            throw new NotUniqueException("Email is not unique.");
        }
        User savedUser = userRepository.save(UserMapper.toUser(newUserRequest));
        return UserMapper.toUserDto(savedUser);
    }

    @Override
    public List<UserDto> get(List<Integer> ids, int from, int size) {
        List<User> users;
        Pageable pageable = PageRequest.of(from / size, size);

        if (ids != null && !ids.isEmpty()) {
            users = userRepository.findAllByIdIn(ids);
        } else {
            users = userRepository.findAll(pageable).getContent();
        }

        return users.stream().map(UserMapper::toUserDto).toList();
    }

    @Override
    public void delete(int userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id %d is not found.", userId));
        }
        userRepository.deleteById(userId);
    }
}
