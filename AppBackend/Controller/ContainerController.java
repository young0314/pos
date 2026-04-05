package com.example.pos_app.Controller;

import com.example.pos_app.DTO.ContainerDTO;
import com.example.pos_app.DTO.RegiContainerDTO;
import com.example.pos_app.Model.*;
import com.example.pos_app.Repository.*;
import com.example.pos_app.Service.ContainerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/container")
public class ContainerController {

    private final UserRepository userRepository;
    private final ContainerStateRepository containerStateRepository;
    private final ContainerRepository containerRepository;
    private final RegiContainerRepository regiContainerRepository;
    private final ContainImageRepository containImageRepository;
    private final ContainerService containerService;
    private final SessionManager sessionManager;

    public ContainerController(
            UserRepository userRepository,
            ContainerStateRepository containerStateRepository,
            ContainerRepository containerRepository,
            RegiContainerRepository regiContainerRepository,
            ContainImageRepository containImageRepository,
            ContainerService containerService,
            SessionManager sessionManager
    ) {
        this.userRepository = userRepository;
        this.containerStateRepository = containerStateRepository;
        this.containerRepository = containerRepository;
        this.regiContainerRepository = regiContainerRepository;
        this.containImageRepository = containImageRepository;
        this.containerService = containerService;
        this.sessionManager = sessionManager;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerContainer(@RequestBody RegiContainerDTO containerDto,
                                                                 HttpServletRequest request) {

        Long adPk = sessionManager.getAdPk(request);
        if (adPk == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "success", false,
                            "message", "로그인을 해주세요."
                    ));
        }

        Optional<User> adminOpt = userRepository.findById(adPk);
        if (adminOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "success", false,
                            "message", "해당 관리자 정보를 찾을 수 없습니다."
                    ));
        }

        User admin = adminOpt.get();

        if (regiContainerRepository.existsByContainNumber(containerDto.getContainNumber())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "success", false,
                            "message", "이미 등록된 컨테이너입니다."
                    ));
        }

        if (regiContainerRepository.existsByDeviceId(containerDto.getDeviceId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "success", false,
                            "message", "이미 등록된 장치입니다."
                    ));
        }

        RegiContainer regiContainer = RegiContainer.builder()
                .containNumber(containerDto.getContainNumber())
                .deviceId(containerDto.getDeviceId())
                .destination(containerDto.getDestination())
                .cargo(containerDto.getCargo())
                .containerOwner(containerDto.getContainerOwner())
                .user(admin)
                .build();

        regiContainerRepository.save(regiContainer);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "컨테이너 등록 성공",
                "containNumber", regiContainer.getContainNumber()
        ));
    }

    @GetMapping("/monitoring/{containNumber}")
    public ResponseEntity<Map<String, Object>> getContainerMonitoringData(
            @PathVariable("containNumber") String containNumber,
            HttpServletRequest request) {

        Optional<Container> containerOpt = containerRepository.findByContainNumber(containNumber);
        if (containerOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "success", false,
                            "message", "없는 컨테이너입니다."
                    ));
        }

        Container container = containerOpt.get();
        ContainerDTO containerDTO = ContainerDTO.fromEntity(container);

        String imageUrl;

        try {
            Optional<ContainImage> containImageOpt = containImageRepository.findByContainer_ContainNumber(containNumber);

            if (containImageOpt.isPresent()) {
                ContainImage containImage = containImageOpt.get();

                if (containImage.getContainImage() != null) {
                    String filePath = containImage.getContainImage();
                    String fileName = filePath.replace("C:/mean/img/", "");
                    imageUrl = "http://192.168.137.243:8080/img/" + fileName;
                } else {
                    throw new RuntimeException("해당 컨테이너의 이미지 경로가 없습니다.");
                }
            } else {
                throw new RuntimeException("해당 컨테이너의 이미지가 존재하지 않습니다.");
            }
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }

        sessionManager.saveContainImageUrl(imageUrl, request);
        String sessionImageUrl = sessionManager.getContainImageUrl(request);

        sessionManager.saveContainerInfo(containerDTO, request);
        ContainerDTO sessionContainer = sessionManager.getContainerInfo(request);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "컨테이너 모니터링 조회 성공");
        response.put("containNumber", container.getContainNumber());

        if (sessionContainer != null) {
            response.put("temperature", sessionContainer.getTemperature());
            response.put("humidity", sessionContainer.getHumidity());
            response.put("lifespan", sessionContainer.getLifespan());
            response.put("doorStatus", sessionContainer.getDoorStatus());
            response.put("errorStatus", sessionContainer.getErrorStatus());
        } else {
            response.put("temperature", container.getTemperature());
            response.put("humidity", container.getHumidity());
            response.put("lifespan", container.getLifespan());
            response.put("doorStatus", container.getDoorStatus());
            response.put("errorStatus", container.getErrorStatus());
        }

        response.put("chillerImage", sessionImageUrl != null ? sessionImageUrl : imageUrl);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> listContainers(HttpServletRequest request) {

        Long adPk = sessionManager.getAdPk(request);
        if (adPk == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        List<RegiContainer> regiContainers = regiContainerRepository.findByUser_AdPk(adPk);

        List<String> containNumbers = regiContainers.stream()
                .map(RegiContainer::getContainNumber)
                .collect(Collectors.toList());

        List<Map<String, Object>> responseList = new ArrayList<>();

        for (String containNumber : containNumbers) {
            Map<String, Object> containerMap = new HashMap<>();
            containerMap.put("containNumber", containNumber);
            responseList.add(containerMap);
        }

        return ResponseEntity.ok(responseList);
    }

    @DeleteMapping("/delete/{containNumber}")
    public ResponseEntity<Map<String, Object>> deleteContainer(@PathVariable("containNumber") String containNumber) {

        boolean existsInRegi = regiContainerRepository.existsByContainNumber(containNumber);
        boolean existsInContainer = containerRepository.existsById(containNumber);

        if (!existsInRegi && !existsInContainer) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "success", false,
                            "message", "해당 컨테이너 번호는 존재하지 않습니다."
                    ));
        }

        try {
            containerService.deleteByContainNumber(containNumber);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "컨테이너 삭제 성공"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "컨테이너 삭제 실패"
                    ));
        }
    }

    @GetMapping("/info/{containNumber}")
    public ResponseEntity<?> getContainerInfo(@PathVariable("containNumber") String containNumber) {

        Optional<RegiContainer> regiContainerOpt = regiContainerRepository.findByContainNumber(containNumber);

        if (regiContainerOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "success", false,
                            "message", "컨테이너를 찾을 수 없습니다."
                    ));
        }

        RegiContainer regiContainer = regiContainerOpt.get();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "컨테이너 정보 조회 성공");
        response.put("containNumber", regiContainer.getContainNumber());
        response.put("destination", regiContainer.getDestination());
        response.put("cargo", regiContainer.getCargo());
        response.put("containerOwner", regiContainer.getContainerOwner());

        return ResponseEntity.ok(response);
    }
}
