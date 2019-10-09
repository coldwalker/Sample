package geektime.im.lecture.dao;

import geektime.im.lecture.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByEmail(String email);

    List<User> findUsersByUidIsNot(long uid);

}
