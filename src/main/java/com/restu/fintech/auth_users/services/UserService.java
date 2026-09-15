package com.restu.fintech.auth_users.services;

import com.restu.fintech.auth_users.dtos.UpdatePasswordRequest;
import com.restu.fintech.auth_users.dtos.UserDTO;
import com.restu.fintech.auth_users.entity.User;
import com.restu.fintech.res.Response;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    User getCurrentLoggedInUser();

    Response<UserDTO> getMyProfile();

    Response<Page<UserDTO>> getAllUser(int page, int size);

    Response<?> updatePassword(UpdatePasswordRequest updatePasswordRequest);

    Response<?> uploadProfilePicture(MultipartFile multipartFile);

    Response<?> uploadProfilePictureToS3(MultipartFile multipartFile);
}
