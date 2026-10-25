package com.technoboost.controller;

import com.technoboost.dto.request.UpdateUserStatusRequest;
import com.technoboost.dto.response.ApiResponse;
import com.technoboost.dto.response.PagedResponse;
import com.technoboost.dto.response.UserResponse;
import com.technoboost.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> getAllUsers(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(
                ApiResponse.success("Users retrieved", userService.getAllUsers(pageable)));
    }

    @PatchMapping("/{userId}/status")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserStatus(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserStatusRequest request) {
        log.info("Admin status change request for user id: {} -> active={}", userId, request.active());
        UserResponse user = userService.updateUserStatus(userId, request.active());
        return ResponseEntity.ok(ApiResponse.success("User status updated", user));
    }
}
