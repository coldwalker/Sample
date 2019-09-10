package geektime.im.lecture.vo;

import java.util.Date;

public class MessageVO {
    private Long mid;
    private String content;
    private Long ownerUid;
    private Integer type;
    private Long otherUid;
    private Date createTime;
    private String ownerUidAvatar;
    private String otherUidAvatar;

    public MessageVO(Long mid, String content, Long ownerUid, Integer type, Long otherUid, Date createTime, String ownerUidAvatar, String otherUidAvatar) {
        this.mid = mid;
        this.content = content;
        this.ownerUid = ownerUid;
        this.type = type;
        this.otherUid = otherUid;
        this.createTime = createTime;
        this.ownerUidAvatar = ownerUidAvatar;
        this.otherUidAvatar = otherUidAvatar;
    }

    public Long getMid() {
        return mid;
    }

    public String getContent() {
        return content;
    }

    public Long getOwnerUid() {
        return ownerUid;
    }

    public Integer getType() {
        return type;
    }

    public Long getOtherUid() {
        return otherUid;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public String getOwnerUidAvatar() {
        return ownerUidAvatar;
    }

    public String getOtherUidAvatar() {
        return otherUidAvatar;
    }
}
