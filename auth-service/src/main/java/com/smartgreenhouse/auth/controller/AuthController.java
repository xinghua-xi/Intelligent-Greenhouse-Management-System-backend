package com.smartgreenhouse.auth.controller;

import com.smartgreenhouse.auth.dto.LoginDTO;
import com.smartgreenhouse.auth.dto.UserDTO;
import com.smartgreenhouse.auth.entity.User;
import com.smartgreenhouse.auth.repository.UserRepository;
import com.smartgreenhouse.auth.util.JwtUtil;
import com.smartgreenhouse.common.core.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {
        User user = userRepository.findByUsername(loginDTO.username());
        
        System.out.println("========== 登录调试 ==========");
        System.out.println("输入用户名: [" + loginDTO.username() + "]");
        System.out.println("输入密码: [" + loginDTO.password() + "]");
        System.out.println("查询到用户: " + (user != null ? user.getUsername() : "null"));
        if (user != null) {
            System.out.println("数据库密码: [" + user.getPassword() + "]");
            System.out.println("密码匹配: " + user.getPassword().equals(loginDTO.password()));
        }
        System.out.println("==============================");

        if (user == null || !user.getPassword().equals(loginDTO.password())) {
            return R.fail(401, "用户名或密码错误");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        return R.ok(Map.of("token", token, "user", user));
    }

    // ========== 人员管理 API ==========

    /** 获取用户列表 */
    @GetMapping("/users")
    public R<List<User>> listUsers() {
        return R.ok(userRepository.findAll());
    }

    /** 获取单个用户 */
    @GetMapping("/users/{id}")
    public R<User> getUser(@PathVariable("id") String id) {
        return userRepository.findById(id)
                .map(R::ok)
                .orElse(R.fail(404, "用户不存在"));
    }

    /** 创建用户 */
    @PostMapping("/users")
    public R<User> createUser(@RequestBody UserDTO dto) {
        if (userRepository.findByUsername(dto.username()) != null) {
            return R.fail(400, "用户名已存在");
        }
        User user = new User();
        user.setUsername(dto.username());
        user.setPassword(dto.password());
        user.setRole(dto.role() != null ? dto.role() : "STANDARD");
        user.setDefaultMode(dto.defaultMode() != null ? dto.defaultMode() : "STANDARD");
        return R.ok(userRepository.save(user));
    }

    /** 更新用户 */
    @PutMapping("/users/{id}")
    public R<User> updateUser(@PathVariable("id") String id, @RequestBody UserDTO dto) {
        return userRepository.findById(id).map(user -> {
            if (dto.username() != null) user.setUsername(dto.username());
            if (dto.password() != null) user.setPassword(dto.password());
            if (dto.role() != null) user.setRole(dto.role());
            if (dto.defaultMode() != null) user.setDefaultMode(dto.defaultMode());
            return R.ok(userRepository.save(user));
        }).orElse(R.fail(404, "用户不存在"));
    }

    /** 删除用户 */
    @DeleteMapping("/users/{id}")
    public R<String> deleteUser(@PathVariable("id") String id) {
        if (!userRepository.existsById(id)) {
            return R.fail(404, "用户不存在");
        }
        userRepository.deleteById(id);
        return R.ok("删除成功");
    }
}