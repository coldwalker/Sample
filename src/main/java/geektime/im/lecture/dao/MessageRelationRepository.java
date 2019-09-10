package geektime.im.lecture.dao;

import geektime.im.lecture.entity.MessageRelation;
import geektime.im.lecture.entity.RelationMultiKeys;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRelationRepository extends JpaRepository<MessageRelation, RelationMultiKeys>{

    List<MessageRelation> findAllByOwnerUidAndOtherUidOrderByMidAsc(Long ownerUid, Long otherUid);

    List<MessageRelation> findAllByOwnerUidAndOtherUidAndMidIsGreaterThanOrderByMidAsc(Long ownerUid, Long otherUid, Long lastMid);
}
