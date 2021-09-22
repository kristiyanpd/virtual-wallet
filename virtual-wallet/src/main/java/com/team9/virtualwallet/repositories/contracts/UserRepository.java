package com.team9.virtualwallet.repositories.contracts;

import com.team9.virtualwallet.models.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends BaseRepository<User> {

    void update(User user, Optional<MultipartFile> multipartFile) throws IOException;

    List<User> filter(Optional<String> userName, Optional<String> phoneNumber, Optional<String> email, int userId);

    List<User> search(String searchTerm, int userId);

}
