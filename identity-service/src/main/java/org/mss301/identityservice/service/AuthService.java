package org.mss301.identityservice.service;

import org.mss301.identityservice.dto.request.CustomerRegistrationRequest;
import org.mss301.identityservice.dto.response.CustomerResponse;

public interface AuthService {
    CustomerResponse register(CustomerRegistrationRequest request);
}
