package geektime.im.lecture.entity;

import java.io.Serializable;

public class RelationMultiKeys implements Serializable {

    protected Long mid;
    protected Long ownerUid;

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
}