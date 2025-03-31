package com.example.pos_app.Controller;

import com.example.pos_app.DTO.ContainerDTO;
import com.example.pos_app.Model.*;
import com.example.pos_app.Repository.*;
import com.example.pos_app.DTO.RegiContainerDTO;
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
    private final ContainerService containerService;  // 추가

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

        //  컨테이너 데이터 찾기
        Optional<Container> existingContainerOpt = containerRepository.findById(containerDto.getContainNumber());
        if (existingContainerOpt.isPresent()) {

            List<RegiContainer> existingRegiContainers = regiContainerRepository.findByContainNumber(containerDto.getContainNumber());
            if (!existingRegiContainers.isEmpty()) {
                response.put("status", "false");
                response.put("message", "해당 컨테이너 번호는 등록되어 있습니다.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // 이미 등록된 경우
            }

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
            Map<String, Object> response = new HashMap<>();
            response.put("status", "false");
            response.put("message", "없는 컨테이너입니다.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); // 404 Not Found
        }
        Container container = containerOpt.get();

        ContainerDTO containerDTO = ContainerDTO.fromEntity(container);

        String imageUrl = null;
        // 컨테이너 이미지 조회
        try {
            Optional<ContainImage> containImageOpt = containImageRepository.findByContainer_ContainNumber(containNumber);

            if (containImageOpt.isPresent()) {
                ContainImage containImage = containImageOpt.get();

                if (containImage.getContainImage() != null) {
                    String filePath = containImage.getContainImage();
                    String fileName = filePath.replace("C:/mean/img/", "");

                    imageUrl = "http://192.168.137.243:8080/img/" + containImage.getContainImage();  
                } else {
                    // 이미지 경로가 없는 경우 예외
                    throw new RuntimeException("해당 컨테이너의 이미지 경로가 없습니다.");
                }
            } else {
                // 컨테이너가 존재하지 않는 경우 예외
                throw new RuntimeException("해당 컨테이너의 이미지가 존재하지 않습니다.");
            }
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }

        // 동적이미지 URL을 세션에 저장
        sessionManager.saveContainImageUrl(imageUrl, request);

        // 세션에서 이미지 URL 가져오기
        String sessionImageUrl = sessionManager.getContainImageUrl(request);

        // 세션에 컨테이너 정보 저장
        sessionManager.saveContainerInfo(containerDTO, request);

        // 세션에서 데이터 가져오기
        ContainerDTO sessionContainer = sessionManager.getContainerInfo(request);

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

        // 이미지 URL을 반환 (세션 값이 있으면 세션 값을, 없으면 새로 생성한 URL을)
        response.put("chillerImage", sessionImageUrl != null ? sessionImageUrl : imageUrl);

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

    @DeleteMapping("delete/{containNumber}")
    public ResponseEntity<Map<String, Object>> deleteContainer(@PathVariable("containNumber") String containNumber) {
        Map<String, Object> response = new HashMap<>();

        // 컨테이너 존재 여부 확인
        if (!containerRepository.existsById(containNumber)) {
            response.put("status", "false");
            response.put("message", "해당 컨테이너 번호는 존재하지 않습니다.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); // 404 Not Found 응답
        }

        // 컨테이너 삭제 서비스 호출
        try {
            containerService.deleteByContainNumber(containNumber);
            response.put("status", "success");
            response.put("message", "컨테이너 삭제 성공");
        } catch (Exception e) {
            response.put("status", "false");
            response.put("message", "컨테이너 삭제 실패");
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //컨테이너 상세 등록 정보 조회
    @GetMapping("/info/{containNumber}")
    public ResponseEntity<?> getContainerInfo(@PathVariable("containNumber") String containNumber) {
        Map<String, Object> response = new HashMap<>();
        // containNumber로 RegiContainer 조회
        List<RegiContainer> regiContainers = regiContainerRepository.findByContainNumber(containNumber);
        if (regiContainers.isEmpty()) {
            response.put("status", "false");
            response.put("message", "컨테이너를 찾을 수 없습니다.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); // 404 Not Found
        }

        // RegiContainer 데이터 가져오기 (리스트에서 첫 번째 항목 사용)
        RegiContainer regiContainer = regiContainers.get(0);
        // 필요한 데이터만 직접 맵에 푸시
        response.put("containNumber", regiContainer.getContainNumber());
        response.put("destination", regiContainer.getDestination());
        response.put("cargo", regiContainer.getCargo());
        response.put("containerOwner", regiContainer.getContainerOwner());

        return new ResponseEntity<>(response, HttpStatus.OK); // 200 OK
    }
}
