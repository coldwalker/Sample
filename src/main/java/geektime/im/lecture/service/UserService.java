package geektime.im.lecture.service;

import geektime.im.lecture.entity.User;
import geektime.im.lecture.vo.MessageContactVO;

import java.util.List;

public interface UserService {

    User login(String email, String password);

    List<User> getAllUsersExcept(long exceptUid);

    List<User> getAllUsersExcept(User exceptUser);

    MessageContactVO getContacts(User ownerUser);
}
