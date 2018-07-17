package com.xiaoyu.modules.common;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hongyu
 * @date 2018-07
 * @description
 */
public class MailBuilder {

    private String sender;
    private String senderName;
    private String subject;
    private String content;
    private List<Receiver> receiverList;

    public String getSender() {
        return sender;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public List<Receiver> getReceiverList() {
        return receiverList;
    }

    public MailBuilder() {
        receiverList = new ArrayList<>(8);
    }

    public MailBuilder sender(String sender, String senderName) {
        this.sender = sender;
        this.senderName = senderName;
        return this;
    }

    public MailBuilder receiver(String receiver, String receiverName) {
        Receiver re = new Receiver();
        re.setReceiver(receiver);
        re.setReceiverName(receiverName);
        receiverList.add(re);
        return this;
    }

    public MailBuilder title(String title) {
        this.subject = title;
        return this;
    }

    public MailBuilder content(String content) {
        this.content = content;
        return this;
    }

    protected class Receiver {
        private String receiverName;
        private String receiver;

        public String getReceiver() {
            return receiver;
        }

        public void setReceiver(String receiver) {
            this.receiver = receiver;
        }

        public String getReceiverName() {
            return receiverName;
        }

        public void setReceiverName(String receiverName) {
            this.receiverName = receiverName;
        }

    }
}