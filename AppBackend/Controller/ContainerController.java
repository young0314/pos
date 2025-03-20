package com.example.pos_app.Controller;

import com.example.pos_app.DTO.ContainImageDTO;
import com.example.pos_app.DTO.ContainerDTO;
import com.example.pos_app.Model.*;
import com.example.pos_app.Repository.*;
import com.example.pos_app.DTO.RegiContainerDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/container")
public class ContainerController {

    private final UserRepository userRepository;
    private final ContainerStateRepository containerStateRepository;
    private final ContainerRepository containerRepository;
    private final RegiContainerRepository regiContainerRepository;
    private final ContainImageRepository containImageRepository;
    private final SessionManager sessionManager;

    public ContainerController(
            UserRepository userRepository,
            ContainerStateRepository containerStateRepository,
            ContainerRepository containerRepository,
            RegiContainerRepository regiContainerRepository,
            ContainImageRepository containImageRepository,
            SessionManager sessionManager
    ) {
        this.userRepository = userRepository;
        this.containerStateRepository = containerStateRepository;
        this.containerRepository = containerRepository;
        this.regiContainerRepository = regiContainerRepository;
        this.containImageRepository = containImageRepository;
        this.sessionManager = sessionManager;
    }


    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> updateContainer(@RequestBody RegiContainerDTO containerDto,
                                                               HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        // 현재 세션에서 adminId 가져오기
        String adminId = sessionManager.getAdminId(request);
        if (adminId == null) {
            response.put("status", "false");
            response.put("message", "로그인을 해주세요.");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 인증되지 않은 요청
        }

        // adminId로 User 엔티티 찾기
        Optional<User> adminOpt = userRepository.findByAdminId(adminId);
        if (adminOpt.isEmpty()) {
            response.put("status","false");
            response.put("message", "해당 관리자 정보를 찾을 수 없습니다.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        User admin = adminOpt.get();

        // 컨테이너 번호가 Container에 존재하는지 확인
        if (!containerStateRepository.existsByContainNumber(containerDto.getContainNumber())) {
            response.put("containNumber", containerDto.getContainNumber());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // 기존 컨테이너 찾기
        Optional<Container> existingContainerOpt = containerRepository.findById(containerDto.getContainNumber());
        if (existingContainerOpt.isPresent()) {
            // 컨테이너가 존재하면 데이터 업데이트
            Container container = existingContainerOpt.get();

            RegiContainer regiContainer = RegiContainer.builder()
                    .containNumber(containerDto.getContainNumber())
                    .destination(containerDto.getDestination())
                    .cargo(containerDto.getCargo())
                    .containerOwner(containerDto.getContainerOwner())
                    .container(container)
                    .user(admin)
                    .build();

            // 등록된 컨테이너 저장
            regiContainerRepository.save(regiContainer);
            System.out.println("ContainNumber22: " + containerDto.getContainNumber()); // 디버깅용

            response.put("containNumber", regiContainer.getContainNumber());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            // 존재하지 않는 컨테이너는 업데이트 불가
            response.put("containNumber", containerDto.getContainNumber());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    //컨테이너 조회 및 센서값 전달
    @GetMapping("/monitoring/{containNumber}")
    public ResponseEntity<Map<String, Object>> getContainerMonitoringData(
            @PathVariable("containNumber") String containNumber, HttpServletRequest request) {

        // 컨테이너 조회
        Optional<Container> containerOpt = containerRepository.findByContainNumber(containNumber);
        if (containerOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Container container = containerOpt.get();

        ContainerDTO containerDTO = ContainerDTO.fromEntity(container);

        String imageUrl = null;
        ContainImageDTO containImageDTO = new ContainImageDTO("", "");

        // 컨테이너 이미지 조회
        Optional<ContainImage> containImageOpt = containImageRepository.findByContainer_ContainNumber(containNumber);
        if (containImageOpt.isPresent()) {
            ContainImage containImage = containImageOpt.get();
            containImageDTO = ContainImageDTO.fromEntity(containImage);

            if (containImage.getContainImage() != null) {
                Path imageDirectory = Paths.get("C:/mean/img"); // 이미지 저장 경로
                try (Stream<Path> files = Files.list(imageDirectory)) {
                    Optional<Path> imagePath = files
                            .filter(path -> path.getFileName().toString().startsWith(containNumber + "_"))
                            .findFirst();

                    if (imagePath.isPresent()) {
                        // 동적 URL 생성
                        imageUrl = "http://192.168.137.158:8080/img/" + imagePath.get().getFileName().toString();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // 세션에 컨테이너 정보 저장
        sessionManager.saveContainerInfo(containerDTO, request);
        sessionManager.saveContainImageInfo(containImageDTO, request);

        // 세션에서 데이터 가져오기
        ContainerDTO sessionContainer = sessionManager.getContainerInfo(request);
        ContainImageDTO sessionContainImage = sessionManager.getContainImageInfo(request);

        // 응답 데이터 생성
        Map<String, Object> response = new HashMap<>();
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

        if (sessionContainImage != null) {
            response.put("chillerImage", sessionContainImage.getContainImage());
        } else {
            response.put("chillerImage", imageUrl);
        }


        return ResponseEntity.ok(response);
    }


    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> listContainers(
            HttpServletRequest request) {
        // 세션에서 adminId 값을 가져옴 (현재 로그인한 관리자의 ID)
        String adminId = sessionManager.getAdminId(request);
        // adminId가 없으면 인증되지 않은 요청이므로 UNAUTHORIZED 응답을 반환
        if (adminId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // RegiContainer 테이블에서 adminId와 연결된 컨테이너 정보를 찾음
        List<RegiContainer> regiContainers = regiContainerRepository.findByUser_AdminId(adminId);

        // RegiContainer에서 각 containNumber를 추출하여 리스트에 저장
        List<String> containNumbers = regiContainers.stream()
                .map(RegiContainer::getContainNumber) // RegiContainer에서 containNumber만 추출
                .collect(Collectors.toList()); // containNumber 리스트로 변환

        // 해당 containNumber와 일치하는 Container 엔티티들을 DB에서 조회
        List<Container> containers = containerRepository.findAllById(containNumbers);

        // containNumber만 담을 리스트 초기화
        List<Map<String, Object>> responseList = new ArrayList<>();

        // 각 Container 객체에서 containNumber만 추출하여 Map에 넣어 리스트에 추가
        for (Container container : containers) {
            Map<String, Object> containerMap = new HashMap<>();
            containerMap.put("containNumber", container.getContainNumber());
            responseList.add(containerMap);
        }

        // containNumber만 담은 리스트를 OK 응답과 함께 반환
        return ResponseEntity.ok(responseList);
    }

}
