package com.example.pos_app.Model;

import com.example.pos_app.DTO.ContainerResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

@Component
public class SessionManager {

    // 세션에 pk 저장
    public void createSession(Long adPk, HttpServletRequest request) {
        HttpSession session = request.getSession(true);  // 세션이 없으면 새로 생성
        session.setAttribute("adPk", adPk);       //  세션에 저장
        System.out.println("세션 생성 완료");
        System.out.println("sessionId = " + session.getId());
        System.out.println("saved adPk = " + session.getAttribute("adPk"));
    }

    // 세션에서 pk 조회
    public Long getAdPk(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // 세션이 없으면 null 반환
        if (session != null) {
            return (Long) session.getAttribute("adPk"); // adId 반환
        }

        System.out.println("sessionId = " + session.getId());
        System.out.println("read adPk = " + session.getAttribute("adPk"));
        return null;
    }

    // 세션 무효화 (로그아웃 시 사용)
    public void expireSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();  // 세션 무효화
        }
    }

    // 컨테이너 정보 세션에 저장
    public void saveContainerInfo(ContainerResponseDTO containerResponseDTO, HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        session.setAttribute("containerInfo", containerResponseDTO);
    }

    // 컨테이너 이미지 정보 세션에 저장
    public void saveContainImageUrl(String imageUrl , HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        session.setAttribute("containImageInfo", imageUrl);
    }

    // 세션에서 컨테이너 정보 조회
    public ContainerResponseDTO getContainerInfo(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (ContainerResponseDTO) session.getAttribute("containerInfo");
        }
        return null;
    }

    // 세션에서 컨테이너 이미지 URL 가져오기
    public String getContainImageUrl(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return (session != null) ? (String) session.getAttribute("containImageUrl") : null;
    }

}

