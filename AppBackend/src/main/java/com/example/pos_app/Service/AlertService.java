package com.example.pos_app.Service;

import com.example.pos_app.DTO.AlertMessageDTO;
import com.example.pos_app.Model.Container;
import com.example.pos_app.Model.RegiContainer;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class AlertService {

    private final SimpMessagingTemplate messagingTemplate;

    public AlertService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void checkAndSendAlert(RegiContainer regiContainer,
                                  int oldErrorStatus,
                                  int oldDoorStatus,
                                  Container newContainer) {

        int newErrorStatus = newContainer.getErrorStatus();
        int newDoorStatus = newContainer.getDoorStatus();
/*
        System.out.println("알림 체크 시작");
        System.out.println("oldErrorStatus = " + oldErrorStatus + ", newErrorStatus = " + newErrorStatus);
        System.out.println("oldDoorStatus = " + oldDoorStatus + ", newDoorStatus = " + newDoorStatus);
*/
        if (regiContainer == null) {
            System.out.println("regiContainer가 null이라 알림을 보낼 수 없습니다.");
            return;
        }

        if (regiContainer.getUser() == null) {
            System.out.println("regiContainer.getUser()가 null이라 알림을 보낼 수 없습니다.");
            return;
        }

        if (oldErrorStatus == 0 && newErrorStatus >= 1 && newErrorStatus <= 7) {

            sendAlert(
                    regiContainer,
                    "ERROR",
                    regiContainer.getContainNumber() + "번 컨테이너에 이상징후가 감지되었습니다."
            );
        }

        if (oldDoorStatus != 1 && newDoorStatus == 1) {
            sendAlert(
                    regiContainer,
                    "DOOR",
                    regiContainer.getContainNumber() + "번 컨테이너의 문이 열려 있습니다."
            );
        }
    }

    private void sendAlert(RegiContainer regiContainer, String type, String message) {
        Long adPk = regiContainer.getUser().getAdPk();
        String destination = "/topic/alerts/" + adPk;
/*
        System.out.println("알림 전송 시작");
        System.out.println("type = " + type);
        System.out.println("message = " + message);
        System.out.println("containNumber = " + regiContainer.getContainNumber());*/
        System.out.println("deviceId = " + regiContainer.getDeviceId());
        System.out.println("adPk = " + adPk);
        System.out.println("destination = " + destination);

        AlertMessageDTO alertMessage = AlertMessageDTO.builder()
                .type(type)
                .message(message)
                .containNumber(regiContainer.getContainNumber())
                .deviceId(regiContainer.getDeviceId())
                .adPk(adPk)
                .build();

        messagingTemplate.convertAndSend(destination, alertMessage);

        //System.out.println("convertAndSend 완료");
    }
}