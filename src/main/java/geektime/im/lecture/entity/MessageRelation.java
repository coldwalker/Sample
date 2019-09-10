package geektime.im.lecture.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "IM_MSG_RELATION")
@IdClass(RelationMultiKeys.class)
public class MessageRelation {

    @Id
    private Long mid;
    @Id
    private Long ownerUid;

    private Integer type;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    private Long otherUid;
    private Date createTime;

    public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }

    public Long getOwnerUid() {
        return ownerUid;
    }

    public void setOwnerUid(Long ownerUid) {
        this.ownerUid = ownerUid;
    }

    public Long getOtherUid() {
        return otherUid;
    }

    public void setOtherUid(Long otherUid) {
        this.otherUid = otherUid;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
