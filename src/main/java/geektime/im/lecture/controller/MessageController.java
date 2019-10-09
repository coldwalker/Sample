package geektime.im.lecture.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import geektime.im.lecture.entity.MessageContent;
import geektime.im.lecture.service.MessageService;
import geektime.im.lecture.vo.MessageContactVO;
import geektime.im.lecture.vo.MessageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;


    @PostMapping(path = "/sendMsg")
    @ResponseBody
    public String sendMsg(@RequestParam Long senderUid, @RequestParam Long recipientUid, String content, Integer msgType, Model model, HttpSession session) {
        MessageVO messageContent = messageService.sendNewMsg(senderUid, recipientUid, content, msgType);
        if (null != messageContent) {
            return JSONObject.toJSONString(messageContent);
        } else {
            return "";
        }
    }

    @GetMapping(path = "/queryMsg")
    @ResponseBody
    public String queryMsg(@RequestParam Long ownerUid, @RequestParam Long otherUid, Model model, HttpSession session) {
        List<MessageVO> messageVO = messageService.queryConversationMsg(ownerUid, otherUid);
        if (messageVO != null) {
            return JSONArray.toJSONString(messageVO);
        } else {
            return "";
        }
    }

    @GetMapping(path = "/queryMsgSinceMid")
    @ResponseBody
    public String queryMsgSinceMid(@RequestParam Long ownerUid, @RequestParam Long otherUid, @RequestParam Long lastMid, Model model, HttpSession session) {
        List<MessageVO> messageVO = messageService.queryNewerMsgFrom(ownerUid, otherUid, lastMid);
        if (messageVO != null) {
            return JSONArray.toJSONString(messageVO);
        } else {
            return "";
        }
    }

    @GetMapping(path = "/queryContacts")
    @ResponseBody
    public String queryContacts(@RequestParam Long ownerUid, Model model, HttpSession session) {
        MessageContactVO contactVO = messageService.queryContacts(ownerUid);
        if (contactVO != null) {
            return JSONObject.toJSONString(contactVO);
        } else {
            return "";
        }
    }
}
