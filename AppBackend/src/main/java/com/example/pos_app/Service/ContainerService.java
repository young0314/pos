package com.example.pos_app.Service;

import com.example.pos_app.Model.ContainImage;
import com.example.pos_app.Model.RegiContainer;
import com.example.pos_app.Repository.ContainImageRepository;
import com.example.pos_app.Repository.ContainerRepository;
import com.example.pos_app.Repository.RegiContainerRepository;
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
                            ContainImageRepository containImageRepository) {
        this.regiContainerRepository = regiContainerRepository;
        this.containerRepository = containerRepository;
        this.containImageRepository = containImageRepository;
    }

    @Transactional
    public void deleteByContainNumber(String containNumber) {

        // 1. 이미지 삭제
        List<ContainImage> containImages = containImageRepository.findAllByContainer_ContainNumber(containNumber);
        if (!containImages.isEmpty()) {
            containImageRepository.deleteAll(containImages);
        }

        // 2. 상태(Container) 삭제
        if (containerRepository.existsById(containNumber)) {
            containerRepository.deleteById(containNumber);
        }

        // 3. 등록(RegiContainer) 삭제
        regiContainerRepository.findByContainNumber(containNumber)
                .ifPresent(regiContainerRepository::delete);
    }
}