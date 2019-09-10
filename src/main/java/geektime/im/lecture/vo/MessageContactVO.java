package geektime.im.lecture.vo;

import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

public class MessageContactVO {
    private Long ownerUid;
    private String ownerAvatar;
    private String ownerName;
    private Long totalUnread;
    private List<ContactInfo> contactInfoList;

    public MessageContactVO(Long ownerUid, String ownerName, String ownerAvatar, Long totalUnread) {
        this.ownerUid = ownerUid;
        this.ownerAvatar = ownerAvatar;
        this.ownerName = ownerName;
        this.totalUnread = totalUnread;
    }

    public class ContactInfo {
        private Long otherUid;
        private String otherName;
        private String otherAvatar;
        private Long mid;
        private Integer type;
        private String content;

        public Long getConvUnread() {
            return convUnread;
        }

        private Long convUnread;
        private Date createTime;

        public ContactInfo(Long otherUid, String otherName, String otherAvatar, Long mid, Integer type, String content, Long convUnread, Date createTime) {
            this.otherUid = otherUid;
            this.otherName = otherName;
            this.otherAvatar = otherAvatar;
            this.mid = mid;
            this.type = type;
            this.content = content;
            this.convUnread = convUnread;
            this.createTime = createTime;
        }

        public Long getOtherUid() {
            return otherUid;
        }

        public Long getMid() {
            return mid;
        }

        public Integer getType() {
            return type;
        }

        public String getContent() {
            return content;
        }

        public String getOtherName() {
            return otherName;
        }

        public String getOtherAvatar() {
            return otherAvatar;
        }

        public Date getCreateTime() {
            return createTime;
        }
    }

    public void setContactInfoList(List<ContactInfo> contactInfoList) {
        this.contactInfoList = contactInfoList;
    }

    public void appendContact(ContactInfo contactInfo) {
        if (contactInfoList != null) {
        } else {
            contactInfoList = Lists.newArrayList();
        }
        contactInfoList.add(contactInfo);
    }

    public Long getOwnerUid() {
        return ownerUid;
    }

    public String getOwnerAvatar() {
        return ownerAvatar;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public Long getTotalUnread() {
        return totalUnread;
    }

    public List<ContactInfo> getContactInfoList() {
        return contactInfoList;
    }
}
