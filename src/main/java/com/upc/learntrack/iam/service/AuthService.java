package com.upc.learntrack.iam.service;

import com.upc.learntrack.iam.dto.LoginRequestDto;
import com.upc.learntrack.iam.dto.RegisterRequestDto;

public interface AuthService {
    String register(RegisterRequestDto request);
    String login(LoginRequestDto request);
}