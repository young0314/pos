package com.example.pos_app.Controller;

import com.example.pos_app.DTO.ContainerResponseDTO;
import com.example.pos_app.DTO.ContainerStateRequestDTO;
import com.example.pos_app.Model.ContainImage;
import com.example.pos_app.Model.Container;
import com.example.pos_app.Model.RegiContainer;
import com.example.pos_app.Repository.ContainImageRepository;
import com.example.pos_app.Repository.ContainerRepository;
import com.example.pos_app.Repository.RegiContainerRepository;
import com.example.pos_app.Service.AlertService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/container")
public class ContainerStateController {

    private final ContainerRepository containerRepository;
    private final ContainImageRepository containImageRepository;
    private final RegiContainerRepository regiContainerRepository;
    private final AlertService alertService;

    public ContainerStateController(
            ContainerRepository containerRepository,
            ContainImageRepository containImageRepository,
            RegiContainerRepository regiContainerRepository,
            AlertService alertService
    ) {
        this.containerRepository = containerRepository;
        this.containImageRepository = containImageRepository;
        this.regiContainerRepository = regiContainerRepository;
        this.alertService = alertService;
    }

    @PostMapping("/state")
    public ResponseEntity<?> saveState(@RequestBody ContainerStateRequestDTO dto) {

        String deviceId = dto.getDeviceId();

        Optional<RegiContainer> regiContainerOpt = regiContainerRepository.findByDeviceId(deviceId);
        if (regiContainerOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "message", "등록되지 않은 장치입니다."
                    ));
        }

        RegiContainer regiContainer = regiContainerOpt.get();
        String containerNumber = regiContainer.getContainNumber();

        Optional<Container> existingContainer = containerRepository.findById(containerNumber);

        int oldErrorStatus = existingContainer.map(Container::getErrorStatus).orElse(0);
        int oldDoorStatus = existingContainer.map(Container::getDoorStatus).orElse(0);

        Container container;

        if (existingContainer.isPresent()) {
            container = existingContainer.get().toBuilder()
                    .temperature(dto.getTemperature())
                    .humidity(dto.getHumidity())
                    .lifespan(dto.getLifespan())
                    .doorStatus(dto.getDoorStatus())
                    .errorStatus(dto.getErrorStatus())
                    .build();
        } else {
            container = Container.builder()
                    .containNumber(containerNumber)
                    .temperature(dto.getTemperature())
                    .humidity(dto.getHumidity())
                    .lifespan(dto.getLifespan())
                    .doorStatus(dto.getDoorStatus())
                    .errorStatus(dto.getErrorStatus())
                    .build();
        }

        Container savedContainer = containerRepository.save(container);

        alertService.checkAndSendAlert(regiContainer, oldErrorStatus, oldDoorStatus, savedContainer);

        return ResponseEntity.ok(ContainerResponseDTO.fromEntity(savedContainer));
    }

    private static final String upload = "C:/mean/img/";

    @PostMapping("/image")
    public ResponseEntity<Map<String, Object>> uploadImage(
            @RequestParam("containNumber") String containNumber,
            @RequestParam("picture") MultipartFile image) {

        Map<String, Object> response = new HashMap<>();
        System.out.println("이미지 업로드 요청: " + containNumber);

        if (image.isEmpty()) {
            response.put("success", false);
            response.put("message", "이미지가 제공되지 않았습니다.");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Container> containerOpt = containerRepository.findById(containNumber);
        if (containerOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "해당 컨테이너 번호가 존재하지 않습니다.");
            return ResponseEntity.badRequest().body(response);
        }

        Container container = containerOpt.get();

        File dir = new File(upload);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File[] existingFiles = dir.listFiles((d, name) -> name.startsWith(containNumber + "_"));
        if (existingFiles != null) {
            for (File file : existingFiles) {
                file.delete();
            }
        }

        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String fileName = containNumber + "_" + timestamp + ".jpg";
        String filePath = upload + fileName;

        try {
            byte[] fileBytes = image.getBytes();
            Path path = Path.of(filePath);
            Files.write(path, fileBytes, StandardOpenOption.CREATE);

            Optional<ContainImage> existImage = containImageRepository.findByContainer_ContainNumber(containNumber);

            if (existImage.isPresent()) {
                ContainImage containImage = existImage.get();
                containImage.setContainImage(filePath);
                containImageRepository.save(containImage);

                response.put("success", true);
                response.put("message", "이미지 업데이트 성공");
            } else {
                ContainImage newImage = ContainImage.builder()
                        .containImage(filePath)
                        .container(container)
                        .build();
                containImageRepository.save(newImage);

                response.put("success", true);
                response.put("message", "이미지 업로드 성공");
            }

            response.put("filePath", filePath);
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            System.out.println("이미지 저장 실패");
            response.put("success", false);
            response.put("message", "이미지 저장 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}