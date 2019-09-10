package geektime.im.lecture.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "IM_MSG_CONTENT")
public class MessageContent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long mid;
    private Long senderId;
    private Long recipientId;
    private String content;
    private Integer msgType;
    private Date createTime;

    public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getMsgType() {
        return msgType;
    }

    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
