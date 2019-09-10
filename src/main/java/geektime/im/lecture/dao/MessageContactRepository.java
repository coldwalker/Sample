package geektime.im.lecture.dao;

import geektime.im.lecture.entity.ContactMultiKeys;
import geektime.im.lecture.entity.MessageContact;
import geektime.im.lecture.entity.MessageRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageContactRepository extends JpaRepository<MessageContact, ContactMultiKeys> {

    public List<MessageContact> findMessageContactsByOwnerUidOrderByMidDesc(Long ownerUid);
}
