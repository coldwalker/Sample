package geektime.im.lecture.service.impl;

import com.google.common.collect.Lists;
import geektime.im.lecture.Constants;
import geektime.im.lecture.dao.MessageContactRepository;
import geektime.im.lecture.dao.MessageContentRepository;
import geektime.im.lecture.dao.MessageRelationRepository;
import geektime.im.lecture.dao.UserRepository;
import geektime.im.lecture.entity.*;
import geektime.im.lecture.service.MessageService;
import geektime.im.lecture.vo.MessageContactVO;
import geektime.im.lecture.vo.MessageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageContentRepository contentRepository;
    @Autowired
    private MessageRelationRepository relationRepository;
    @Autowired
    private MessageContactRepository contactRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public MessageContent sendNewMsg(long senderUid, long recipientUid, String content, int msgType) {
        Date currentTime = new Date();
        /**存内容*/
        MessageContent messageContent = new MessageContent();
        messageContent.setSenderId(senderUid);
        messageContent.setRecipientId(recipientUid);
        messageContent.setContent(content);
        messageContent.setMsgType(msgType);
        messageContent.setCreateTime(currentTime);
        messageContent = contentRepository.saveAndFlush(messageContent);
        Long mid = messageContent.getMid();

        /**存发件人的发件箱*/
        MessageRelation messageRelationSender = new MessageRelation();
        messageRelationSender.setMid(mid);
        messageRelationSender.setOwnerUid(senderUid);
        messageRelationSender.setOtherUid(recipientUid);
        messageRelationSender.setType(0);
        messageRelationSender.setCreateTime(currentTime);
        relationRepository.save(messageRelationSender);

        /**存收件人的收件箱*/
        MessageRelation messageRelationRecipient = new MessageRelation();
        messageRelationRecipient.setMid(mid);
        messageRelationRecipient.setOwnerUid(recipientUid);
        messageRelationRecipient.setOtherUid(senderUid);
        messageRelationRecipient.setType(1);
        messageRelationRecipient.setCreateTime(currentTime);
        relationRepository.save(messageRelationRecipient);

        /**更新发件人的最近联系人 */
        MessageContact messageContactSender = contactRepository.findOne(new ContactMultiKeys(senderUid, recipientUid));
        if (messageContactSender != null) {
            messageContactSender.setMid(mid);
        } else {
            messageContactSender = new MessageContact();
            messageContactSender.setOwnerUid(senderUid);
            messageContactSender.setOtherUid(recipientUid);
            messageContactSender.setMid(mid);
            messageContactSender.setCreateTime(currentTime);
            messageContactSender.setType(0);
        }
        contactRepository.save(messageContactSender);

        /**更新收件人的最近联系人 */
        MessageContact messageContactRecipient = contactRepository.findOne(new ContactMultiKeys(recipientUid, senderUid));
        if (messageContactRecipient != null) {
            messageContactRecipient.setMid(mid);
        } else {
            messageContactRecipient = new MessageContact();
            messageContactRecipient.setOwnerUid(recipientUid);
            messageContactRecipient.setOtherUid(senderUid);
            messageContactRecipient.setMid(mid);
            messageContactRecipient.setCreateTime(currentTime);
            messageContactRecipient.setType(1);
        }
        contactRepository.save(messageContactRecipient);

        /**更未读更新 */
        redisTemplate.opsForValue().increment(recipientUid + "_T", 1); //加总未读
        redisTemplate.opsForHash().increment(recipientUid + "_C", senderUid, 1); //加会话未读

        return messageContent;
    }

    @Override
    public List<MessageVO> queryConversationMsg(long ownerUid, long otherUid) {
        List<MessageRelation> relationList = relationRepository.findAllByOwnerUidAndOtherUidOrderByMidAsc(ownerUid, otherUid);
        return composeMessageVO(relationList, ownerUid, otherUid);
    }

    @Override
    public List<MessageVO> queryNewerMsgFrom(long ownerUid, long otherUid, long fromMid) {
        List<MessageRelation> relationList = relationRepository.findAllByOwnerUidAndOtherUidAndMidIsGreaterThanOrderByMidAsc(ownerUid, otherUid, fromMid);
        return composeMessageVO(relationList, ownerUid, otherUid);
    }

    private List<MessageVO> composeMessageVO(List<MessageRelation> relationList, long ownerUid, long otherUid) {
        if (null != relationList && !relationList.isEmpty()) {
            /** 先拼接消息索引和内容 */
            List<MessageVO> msgList = Lists.newArrayList();
            User self = userRepository.findOne(ownerUid);
            User other = userRepository.findOne(otherUid);
            relationList.stream().forEach(relation -> {
                Long mid = relation.getMid();
                MessageContent contentVO = contentRepository.findOne(mid);
                if (null != contentVO) {
                    String content = contentVO.getContent();
                    MessageVO messageVO = new MessageVO(mid, content, relation.getOwnerUid(), relation.getType(), relation.getOtherUid(), relation.getCreateTime(), self.getAvatar(), other.getAvatar());
                    msgList.add(messageVO);
                }
            });

            /** 再变更未读 */
            Object convUnreadObj = redisTemplate.opsForHash().get(ownerUid + Constants.CONVERSION_UNREAD_SUFFIX, otherUid);
            if (null != convUnreadObj) {
                long convUnread = Long.parseLong((String) convUnreadObj);
                redisTemplate.opsForHash().delete(ownerUid + Constants.CONVERSION_UNREAD_SUFFIX, otherUid);
                long afterCleanUnread = redisTemplate.opsForValue().increment(ownerUid + Constants.TOTAL_UNREAD_SUFFIX, -convUnread);
                /** 修正总未读 */
                if (afterCleanUnread <= 0) {
                    redisTemplate.delete(ownerUid + Constants.TOTAL_UNREAD_SUFFIX);
                }
            }
            return msgList;
        }
        return null;
    }

    @Override
    public MessageContactVO queryContacts(long ownerUid) {
        List<MessageContact> contacts = contactRepository.findMessageContactsByOwnerUidOrderByMidDesc(ownerUid);
        if (contacts != null) {
            User user = userRepository.findOne(ownerUid);
            long totalUnread = 0;
            Object totalUnreadObj = redisTemplate.opsForValue().get(user.getUid() + Constants.TOTAL_UNREAD_SUFFIX);
            if (null != totalUnreadObj) {
                totalUnread = Long.parseLong((String) totalUnreadObj);
            }

            MessageContactVO contactVO = new MessageContactVO(user.getUid(), user.getUsername(), user.getAvatar(), totalUnread);
            contacts.stream().forEach(contact -> {
                Long mid = contact.getMid();
                MessageContent contentVO = contentRepository.findOne(mid);
                User otherUser = userRepository.findOne(contact.getOtherUid());

                if (null != contentVO) {
                    long convUnread = 0;
                    Object convUnreadObj = redisTemplate.opsForHash().get(user.getUid() + Constants.CONVERSION_UNREAD_SUFFIX, otherUser.getUid());
                    if (null != convUnreadObj) {
                        convUnread = Long.parseLong((String) convUnreadObj);
                    }
                    MessageContactVO.ContactInfo contactInfo = contactVO.new ContactInfo(otherUser.getUid(), otherUser.getUsername(), otherUser.getAvatar(), mid, contact.getType(), contentVO.getContent(), convUnread, contact.getCreateTime());
                    contactVO.appendContact(contactInfo);
                }
            });
            return contactVO;
        }
        return null;
    }
}
