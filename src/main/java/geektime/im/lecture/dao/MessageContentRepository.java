package geektime.im.lecture.dao;

import geektime.im.lecture.entity.MessageContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageContentRepository extends JpaRepository<MessageContent, Long> {

}
