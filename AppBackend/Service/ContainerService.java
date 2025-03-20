package com.example.pos_app.Service;

import com.example.pos_app.DTO.RegiContainerDTO;
import com.example.pos_app.Model.RegiContainer;
import com.example.pos_app.Model.Container;
import com.example.pos_app.Model.User;
import com.example.pos_app.Repository.RegiContainerRepository;
import com.example.pos_app.Repository.UserRepository;
import com.example.pos_app.Repository.ContainerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContainerService {

    @Autowired
    private RegiContainerRepository regiContainerRepository;
    @Autowired
    private ContainerRepository containerRepository;
    @Autowired
    private UserRepository userRepository;

    // 컨테이너 등록 메서드
    public RegiContainer registerContainer(RegiContainerDTO containerDto, String adminId) {
        // adminId로 User 엔티티 찾기
        User admin = userRepository.findByAdminId(adminId).orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        // 기존 컨테이너 찾기
        Container container = containerRepository.findById(containerDto.getContainNumber())
                .orElseThrow(() -> new IllegalArgumentException("Container not found"));

        // RegiContainer 객체 생성
        RegiContainer regiContainer = RegiContainer.builder()
                .containNumber(containerDto.getContainNumber())
                .destination(containerDto.getDestination())
                .cargo(containerDto.getCargo())
                .containerOwner(containerDto.getContainerOwner())
                .container(container) // 기존 Container와 연결
                .user(admin) // 관리자와 연결
                .build();

        // RegiContainer 저장
        return regiContainerRepository.save(regiContainer);
    }
}
