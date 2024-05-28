//package vn.vt.service.impl;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.util.Assert;
//import org.telegram.telegrambots.meta.api.objects.Message;
//import org.telegram.telegrambots.meta.api.objects.Update;
//import vn.vt.dao.RawDataDAO;
//import vn.vt.entity.RawData;
//
//import java.util.HashSet;
//import java.util.Set;
//
//@SpringBootTest
//public class MainServiceImplTest {
//    @Autowired
//    private RawDataDAO rawDataDAO;
//
//    @Test
//    public void testSaveRawData(){
//        //Конфигурация входных данных
//        Update update = new Update();
//        Message msg = new Message();
//        msg.setText("Test");
//        update.setMessage(msg);
//
//        //Имплементация данных в код
//        RawData rawData = RawData.builder()
//                .event(update)
//                .build();
//        Set<RawData> testData = new HashSet<>();
//
//        testData.add(rawData);
//        rawDataDAO.save(rawData);
//
//        // Проверка на изменение данных
//        Assert.isTrue(testData.contains(rawData), "Entity is not found in the set");
//
//    }
//}
