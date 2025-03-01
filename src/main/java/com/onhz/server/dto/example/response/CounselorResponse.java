package com.onhz.server.dto.example.response;

import java.util.ArrayList;

//public record CounselorResponse(
//        Long id,
//        String name,
//        String introduction,
//        boolean isUse,
//        String profile,
//        List<SubjectResponse> subjects
//) {
//
//    public static CounselorResponse of(Counselor counselor, List<CounselorSubject> counselorSubjectList) {
//        List<SubjectResponse> subjectList = new ArrayList<>();
//        for (CounselorSubject counselorSubject : counselorSubjectList) {
//            subjectList.add(SubjectResponse.of(counselorSubject.getSubject(), counselorSubject));
//        }
//
//        return new CounselorResponse(counselor.getId(), counselor.getName(), counselor.getIntroduction(),
//                counselor.getIsUse(), counselor.getProfile(), subjectList);
//    }
//}

