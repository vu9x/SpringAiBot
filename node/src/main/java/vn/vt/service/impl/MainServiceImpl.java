package vn.vt.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import vn.vt.dao.AppUserDAO;
import vn.vt.dao.RawDataDAO;
import vn.vt.entity.*;
import vn.vt.exceptions.UploadFileException;
import vn.vt.service.*;
import vn.vt.service.enums.LinkType;
import vn.vt.service.enums.ServiceCommand;

import static vn.vt.entity.enums.UserState.*;
import static vn.vt.service.enums.ServiceCommand.*;

@Service
@Log4j2
public class MainServiceImpl implements MainService {
    private final RawDataDAO rawDataDAO;
    private final ProducerService producerService;
    private final AppUserDAO appUserDAO;
    private final FileService fileService;
    private final AppUserService appUserService;
//    private final TextService textService;
//    private final OpenAiService openAiService;

    public MainServiceImpl(RawDataDAO rawDataDAO,
                           ProducerService producerService,
                           AppUserDAO appUserDAO,
                           FileService fileService,
                           AppUserService appUserService) {
        this.rawDataDAO = rawDataDAO;
        this.producerService = producerService;
        this.appUserDAO = appUserDAO;
        this.fileService = fileService;
        this.appUserService = appUserService;
    }

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var userState = appUser.getState();
        String text = update.getMessage().getText();
        var output = "";

        var serviceCommand = ServiceCommand.fromValue(text);
        if(CANCEL.equals(serviceCommand)){
            output = cancelProcess(appUser);
        } else if (BASIC_STATE.equals(userState)) {
            output = processServiceCommand(appUser, text);
        } else if (WAIT_FOR_EMAIL_STATE.equals(userState)) {
            output = appUserService.setEmail(appUser, text);
        } else {
            log.error("Unknown user state: " + userState);
            output = "Неизвестная ошибка! Введите /cancel и попробуйте снова!";
        }

        // TODO добавить зависимость для отправки сообщения в OpenAi
//        String openAiResponse = openAiService.sendRequestToOpenAiServer(String request);
//        AppOpenAiResponses appOpenAiResponses = textService.processText(update, appUser, openAiResponse);

        Long chatId = update.getMessage().getChatId();
        sendAnswer(output, chatId);

    }

    @Override
    public void processDocMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        Long chatId = update.getMessage().getChatId();

        if(isNotAllowToSendContent(chatId, appUser)){
            return;
        }

        try{
            AppDocument doc = fileService.processDoc(update.getMessage());

            String link = fileService.generateLink(doc.getId(), LinkType.GET_DOC);

            var answer = "Документ успешно загружен!"
                    + "Ссылка для скачивания: " + link;

            sendAnswer(answer, chatId);
        } catch (UploadFileException e){
            log.error(e);
            String error = "К сожалению, загрузка файла не удалась. Повторите попытку";
            sendAnswer(error, chatId);
        }

    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        Long chatId = update.getMessage().getChatId();

        if(isNotAllowToSendContent(chatId, appUser)){
            return;
        }

        try{
            AppPhoto appPhoto = fileService.processPhoto(update.getMessage());
            String link = fileService.generateLink(appPhoto.getId(), LinkType.GET_PHOTO);

            var answer = "Фото успешно загружено! " +
                    "Ссылка для скачивания: " + link;

            sendAnswer(answer, chatId);
        } catch (UploadFileException e){
            log.error(e);
            String error = "К сожалению, загрузка фото не удалась. Повторите попытку";
            sendAnswer(error, chatId);
        }



    }


    private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
        var userState = appUser.getState();
        if(!appUser.getIsActive()){
            var error = "Зарегистрируйтесь или активируйте свою учетную запись для загрузки контента.";
            sendAnswer(error, chatId);
            return true;

        } else if (!BASIC_STATE.equals(userState)){
            var error = "Отмените текущкую команду с помощью /cancel для отправки файлов";
            sendAnswer(error, chatId);
            return true;
        }

        return false;
    }


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
        } else if (CHAT_GPT.equals(serviceCommand)){
            //TODO добавить подключеие к API CHAT_GPT
            return "Временно не доступно.";
        } else if (START.equals(serviceCommand)){
            return "Приветствую! Чтобы посмотреть список доступных команд введите /help";
        } else {
            return "Неизвестная команда! Чтобы посмотреть список доступных команд введите /help";
        }
    }

    private String help() {
        return "Список доступных команд:\n"
                + "/registration - регистрация пользователей\n"
                + "/chatgpt - подключение к чат gpt\n"
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
