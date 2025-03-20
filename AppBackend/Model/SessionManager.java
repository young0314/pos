package com.example.pos_app.Model;

import com.example.pos_app.DTO.ContainImageDTO;
import com.example.pos_app.DTO.ContainerDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

@Component
public class SessionManager {

    // 세션에 adminId 저장
    public void createSession(String adminId, HttpServletRequest request) {
        HttpSession session = request.getSession(true);  // 세션이 없으면 새로 생성
        session.setAttribute("adminId", adminId);       // adminId 세션에 저장
    }

    // 세션에서 adminId 조회
    public String getAdminId(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // 세션이 없으면 null 반환
        if (session != null) {
            return (String) session.getAttribute("adminId"); // adminId 반환
        }
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
    public void saveContainerInfo(ContainerDTO containerDTO, HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        session.setAttribute("containerInfo", containerDTO);
    }

    // 컨테이너 이미지 정보 세션에 저장
    public void saveContainImageInfo(ContainImageDTO containImageDTO, HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        session.setAttribute("containImageInfo", containImageDTO);
    }

    // 세션에서 컨테이너 정보 조회
    public ContainerDTO getContainerInfo(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (ContainerDTO) session.getAttribute("containerInfo");
        }
        return null;
    }

    // 세션에서 컨테이너 이미지 정보 조회
    public ContainImageDTO getContainImageInfo(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (ContainImageDTO) session.getAttribute("containImageInfo");
        }
        return null;
    }

}

