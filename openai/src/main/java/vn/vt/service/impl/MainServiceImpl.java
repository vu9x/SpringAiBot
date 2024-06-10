package vn.vt.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import vn.vt.dao.AppOpenAiResponsesDAO;
import vn.vt.dao.AppUserDAO;
import vn.vt.dao.RawDataDAO;
import vn.vt.entity.AppOpenAiResponses;
import vn.vt.entity.AppUser;
import vn.vt.entity.RawData;
import vn.vt.service.AppUserService;
import vn.vt.service.MainService;
import vn.vt.service.ProducerService;
import vn.vt.service.enums.ServiceCommand;

import static vn.vt.enums.UserState.*;
import static vn.vt.service.enums.ServiceCommand.*;

@Log4j2
@RequiredArgsConstructor
@Service
public class MainServiceImpl implements MainService {
    private final RawDataDAO rawDataDAO;
    private final AppOpenAiResponsesDAO appOpenAiResponsesDAO;
    private final AppUserDAO appUserDAO;
    private final ProducerService producerService;
    private final AppUserService appUserService;
    private final OpenAiService openAiService;

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);

        var userState = appUser.getState();
        String message = update.getMessage().getText();
        var output = "";

        if (BASIC_STATE.equals(userState)) {
            output = processServiceCommand(appUser, message);
        } else if (WAIT_FOR_EMAIL_STATE.equals(userState)) {
            output = appUserService.setEmail(appUser, message);
        } else if (appUser.getIsActive()) {
            output = openAiService.sendRequestToOpenAiServer(message);
            log.info("openai: Received OpenAiResponse");
            saveOpenAiResponse(appUser, message, output);
        } else{
            output = "Зарегистрируйтесь или активируйте свою учетную запись для загрузки контента.";
        }

        Long chatId = update.getMessage().getChatId();
        sendAnswer(output, chatId);

    }


//    public void processTextMessage111(Update update) {
//        saveRawData(update);
//        var appUser = findOrSaveAppUser(update);
//
//        Long chatId = update.getMessage().getChatId();
//        if(isNotAllowToSendContent(chatId, appUser)){
//            return;
//        }
//
//        var userState = appUser.getState();
//        String message = update.getMessage().getText();
//        var output = "";
//
//        if (BASIC_STATE.equals(userState)) {
//            output = processServiceCommand(appUser, message);
//        }
//
//
//
//        var serviceCommand = ServiceCommand.fromValue(message);
//        if(CANCEL.equals(serviceCommand)){
//            output = cancelProcess(appUser);
//        } else if (BASIC_STATE.equals(userState)) {
//            output = processServiceCommand(appUser, message);
//        } else if (WAIT_FOR_EMAIL_STATE.equals(userState)) {
//            output = appUserService.setEmail(appUser, message);
//        } else {
////            log.error("Unknown user state: " + userState);
////            output = "Неизвестная ошибка! Введите /cancel и попробуйте снова!";
//            output = openAiService.sendRequestToOpenAiServer(message);
//            log.info("openai: Received OpenAiResponse");
//
//            saveOpenAiResponse(appUser, message, output);
//        }
//
//        sendAnswer(output, chatId);
//
//    }




//    private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
//        var userState = appUser.getState();
//        if(!appUser.getIsActive()){
//            var error = "Зарегистрируйтесь или активируйте свою учетную запись для загрузки контента.";
//            sendAnswer(error, chatId);
//            return true;
//
//        }
//        else if (!BASIC_STATE.equals(userState)){
//            var error = "Отмените текущкую команду с помощью /cancel для отправки файлов";
//            sendAnswer(error, chatId);
//            return true;
//        }
//
//        return false;
//    }



    private void sendAnswer(String output, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.producerAnswer(sendMessage);
    }

    private String processServiceCommand(AppUser appUser, String cmd) {
        var serviceCommand = ServiceCommand.fromValue(cmd);

        if (REGISTRATION.equals(serviceCommand)){
            return appUserService.registerUser(appUser);
        } else if (HELP.equals(serviceCommand)){
            return help();
        } else if (START.equals(serviceCommand)){
            return "Приветствую!\n" +
                    "Вы подключились к умному чатботу R2D2.\n" +
                    "Я умею отвечать на ваши текстовые сообщения с помощью ChatGPT." +
                    "А также сохранять ваши фото и документы на сервере.\n" +
                    "Чтобы начать работу вы должны быть зарегистрированы. Введите команду /registration.\n" +
                    "Чтобы посмотреть список доступных команд введите /help";
        } else if (CANCEL.equals(serviceCommand)){
            return cancelProcess(appUser);
        } else {
            return "Неизвестная команда! Чтобы посмотреть список доступных команд введите /help";
        }
    }

    private String help() {
        return "Список доступных команд:\n"
                + "/registration - регистрация пользователей\n"
//                + "/chatgpt - подключение к чат gpt\n"
                + "/cancel отмена выполнения текущей команды";
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setState((BASIC_STATE));
        appUserDAO.save(appUser);
        return "Команда отменена!";
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .event(update)
                .build();
        rawDataDAO.save(rawData);
    }

    private void saveOpenAiResponse(AppUser appUser, String message, String output) {
        AppOpenAiResponses appOpenAiResponses = AppOpenAiResponses.builder()
                .appUser(appUser)
                .userRequest(message)
                .openAiResponse(output)
                .build();
        appOpenAiResponsesDAO.save(appOpenAiResponses);
        log.info("openai: save appOpenAiResponses");
    }

    private AppUser findOrSaveAppUser(Update update){
        User telegramUser = update.getMessage().getFrom();

        var optionalPersistentAppUser = appUserDAO.findByTelegramUserId(telegramUser.getId());
        if(optionalPersistentAppUser.isEmpty()){
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .userName(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .isActive(false)
                    .state(BASIC_STATE)
                    .build();
            return appUserDAO.save(transientAppUser);
        }

        return optionalPersistentAppUser.get();
    }
}
