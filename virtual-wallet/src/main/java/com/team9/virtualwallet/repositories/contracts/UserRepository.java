package com.team9.virtualwallet.repositories.contracts;

import com.team9.virtualwallet.models.User;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends BaseRepository<User> {

    void updateProfilePhoto(User user, MultipartFile multipartFile);

    List<User> filter(Optional<String> userName, Optional<String> phoneNumber, Optional<String> email, Pageable pageable);

    User getByFieldNotDeleted(String fieldName, String searchTerm, int userId);

}
