package geektime.im.lecture.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "IM_MSG_CONTACT")
@IdClass(ContactMultiKeys.class)
public class MessageContact {
    @Id
    private Long ownerUid;
    @Id
    private Long otherUid;
    private Long mid;
    private Integer type;

    private Date createTime;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

}
