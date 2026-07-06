package com.upc.learntrack.iam.service;

import com.upc.learntrack.iam.dto.PendingUserDto;
import java.util.List;

public interface AdminService {
    List<PendingUserDto> getPendingUsers();
    void approveUser(Long userId);
    void rejectUser(Long userId);
    void verifyCode(String email, String code);
}