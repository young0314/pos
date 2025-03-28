package com.example.pos_app.Service;

import com.example.pos_app.Model.ContainImage;
import com.example.pos_app.Model.RegiContainer;
import com.example.pos_app.Repository.ContainImageRepository;
import com.example.pos_app.Repository.RegiContainerRepository;
import com.example.pos_app.Repository.ContainerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ContainerService {


    private final RegiContainerRepository regiContainerRepository;

    private final ContainerRepository containerRepository;


    private final ContainImageRepository containImageRepository;
    public ContainerService(RegiContainerRepository regiContainerRepository,
                            ContainerRepository containerRepository,
                            ContainImageRepository containImageRepository){
            this.regiContainerRepository = regiContainerRepository;
            this.containerRepository = containerRepository;
            this.containImageRepository = containImageRepository;
    }

    // 컨테이너 삭제 메서드
    @Transactional
    public void deleteByContainNumber(String containNumber) {
        // 1. RegiContainer 삭제
        List<RegiContainer> regiContainers = regiContainerRepository.findByContainNumber(containNumber);
        if (!regiContainers.isEmpty()) {
            regiContainerRepository.deleteAll(regiContainers);
        }

        // 2. ContainImage 삭제
        List<ContainImage> containImages = containImageRepository.findAllByContainer_ContainNumber(containNumber);
        if (!containImages.isEmpty()) {
            containImageRepository.deleteAll(containImages);
        }

        // 3. Container 삭제
        containerRepository.deleteById(containNumber);
    }
}
