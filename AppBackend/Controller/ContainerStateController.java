package com.example.pos_app.Controller;

import com.example.pos_app.DTO.ContainerDTO;
import com.example.pos_app.Model.ContainImage;
import com.example.pos_app.Repository.ContainImageRepository;
import com.example.pos_app.Repository.ContainerStateRepository;
import com.example.pos_app.Service.ContainerStateService;
import com.example.pos_app.Model.Container;
import com.example.pos_app.Repository.ContainerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/container")
public class ContainerStateController {

    private final ContainerRepository containerRepository;
    private final ContainImageRepository containImageRepository;

    public ContainerStateController(
            ContainerRepository containerRepository,
            ContainImageRepository containImageRepository
    ) {
        this.containerRepository = containerRepository;
        this.containImageRepository = containImageRepository;
    }

    //하드로부터 컨테이너 센서값 받기
    @PostMapping("/state")
    public ResponseEntity<ContainerDTO> saveStatus(@RequestBody ContainerDTO containerStateDto) {
        String containNumber = containerStateDto.getContainNumber();

        // 컨테이너 번호가 존재하는지 확인
        Optional<Container> existingContainer = containerRepository.findById(containNumber);

        Container container;

        if (existingContainer.isPresent()) {
            // 기존 컨테이너 업데이트 (DTO에서 받은 값을 사용하여 컨테이너 객체를 새로 빌드)
            container = existingContainer.get();
            container = container.toBuilder()
                    .temperature(containerStateDto.getTemperature())
                    .humidity(containerStateDto.getHumidity())
                    .lifespan(containerStateDto.getLifespan())
                    .doorStatus(containerStateDto.getDoorStatus())
                    .errorStatus(containerStateDto.getErrorStatus())
                    .build();

            System.out.println("기존 컨테이너 업데이트: " + containNumber);
        } else {
            // 새 컨테이너 생성 (DTO에서 받은 값으로 초기화)
            container = Container.builder()
                    .containNumber(containNumber)
                    .temperature(containerStateDto.getTemperature())
                    .humidity(containerStateDto.getHumidity())
                    .lifespan(containerStateDto.getLifespan())
                    .doorStatus(containerStateDto.getDoorStatus())
                    .errorStatus(containerStateDto.getErrorStatus())
                    .build();

            System.out.println("새 컨테이너 생성: " + containNumber);
        }

        System.out.println();
        // 저장 (신규 생성이든 기존 업데이트든)
        containerRepository.save(container);
        // DTO로 변환하여 반환
        ContainerDTO updatedContainerDto = ContainerDTO.fromEntity(container);

        // 응답으로 DTO 반환
        return new ResponseEntity<>(updatedContainerDto, HttpStatus.OK);
    }

    // 유니티로부터 컨테이너 이미지 받기
    private static final String upload = "C:/mean/img/"; // 저장할 폴더 경로
    @PostMapping("/image")
    public ResponseEntity<Map<String, Object>> uploadImage(
            @RequestParam("containNumber") String containNumber,
            @RequestParam("picture") MultipartFile image) {

        Map<String, Object> response = new HashMap<>();
        System.out.println("이미지 업로드 요청: " + containNumber);

        if (image.isEmpty()) {
            response.put("message", "이미지가 제공되지 않았습니다.");
            return ResponseEntity.badRequest().body(response);
        }

        // 컨테이너 존재 여부 확인
        Optional<Container> containerOpt = containerRepository.findById(containNumber);
        if (containerOpt.isEmpty()) {
            response.put("message", "해당 컨테이너 번호가 존재하지 않습니다.");
            return ResponseEntity.badRequest().body(response);
        }
        Container container = containerOpt.get();

        String fileName = containNumber + "_" + image.getOriginalFilename();
        String filePath = upload + fileName;

        try {
            // 저장 폴더 없으면 생성
            File dir = new File(upload);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 이미지 파일 저장
            byte[] fileBytes = image.getBytes();
            Path path = Path.of(filePath);
            Files.write(path, fileBytes, StandardOpenOption.CREATE);

            // 기존 이미지 확인 후 덮어쓰기 or 새로 저장
            Optional<ContainImage> existImage = containImageRepository.findByContainer_ContainNumber(containNumber);
            if (existImage.isPresent()) {
                ContainImage containImage = existImage.get();
                containImage.setContainImage(filePath); // 기존 이미지 덮어쓰기
                containImageRepository.save(containImage);
                response.put("message", "이미지 업데이트 성공");
            } else {
                ContainImage newImage = ContainImage.builder()
                        .containImage(filePath)
                        .container(container)
                        .build();
                containImageRepository.save(newImage);
                response.put("message", "이미지 업로드 성공");
            }

            response.put("filePath", filePath);
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            System.out.println("이미지 저장 실패");
            response.put("message", "이미지 저장 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }


}
