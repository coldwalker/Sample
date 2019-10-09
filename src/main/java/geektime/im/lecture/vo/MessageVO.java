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
    private String ownerName;
    private String otherName;

    public String getOwnerName() {
        return ownerName;
    }

    public String getOtherName() {
        return otherName;
    }

    public MessageVO(Long mid, String content, Long ownerUid, Integer type, Long otherUid, Date createTime, String ownerUidAvatar, String otherUidAvatar, String ownerName, String otherName) {
        this.mid = mid;
        this.content = content;
        this.ownerUid = ownerUid;
        this.type = type;
        this.otherUid = otherUid;
        this.createTime = createTime;
        this.ownerUidAvatar = ownerUidAvatar;
        this.otherUidAvatar = otherUidAvatar;
        this.ownerName = ownerName;
        this.otherName = otherName;
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
