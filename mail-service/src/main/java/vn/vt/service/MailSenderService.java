package vn.vt.service;

import vn.vt.dto.MailParams;

public interface MailSenderService {
    void send (MailParams mailParams) throws Exception;
}
