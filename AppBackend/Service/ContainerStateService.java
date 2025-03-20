package com.example.pos_app.Service;

import com.example.pos_app.Model.Container;
import com.example.pos_app.Repository.ContainerStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContainerStateService {

    @Autowired
    private ContainerStateRepository containerStateRepository; // 컨테이너 상태 저장

    public void saveContainerStatus(Container container) {
        // DB에 저장
        containerStateRepository.save(container);
    }
}
