package com.gabia.gyebalja.service;

import com.gabia.gyebalja.domain.User;
import com.gabia.gyebalja.dto.statistics.StatisticsEducationCategoryResponseDto;
import com.gabia.gyebalja.dto.statistics.StatisticsEducationHourResponseDto;
import com.gabia.gyebalja.dto.statistics.StatisticsEducationMonthResponseDto;
import com.gabia.gyebalja.dto.statistics.StatisticsEducationRankResponseDto;
import com.gabia.gyebalja.dto.statistics.StatisticsEducationTagResponseDto;
import com.gabia.gyebalja.dto.statistics.StatisticsMainCategoryResponseDto;
import com.gabia.gyebalja.dto.statistics.StatisticsMainMonthResponseDto;
import com.gabia.gyebalja.dto.statistics.StatisticsMainTagResponseDto;
import com.gabia.gyebalja.dto.statistics.StatisticsMainYearResponseDto;
import com.gabia.gyebalja.exception.NotExistUserException;
import com.gabia.gyebalja.repository.StatisticsRepository;
import com.gabia.gyebalja.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
@Transactional
@Service
public class StatisticsService {

    private final StatisticsRepository statisticsRepository;
    private final UserRepository userRepository;
    /**
     *
     * 메인 페이지 통계 Service
     */
    public StatisticsMainYearResponseDto getMainStatisticsWithYear(){
        int yearPage = 0;
        int yearSize = 5;
        List<ArrayList<String>> response = statisticsRepository.getMainStatisticsWithYear(PageRequest.of(yearPage, yearSize));
        ArrayList<String> years = new ArrayList<>();
        ArrayList<Long> totalEducationHourOfEmployees = new ArrayList<>();
        ArrayList<Long> totalEducationNumberOfEmployees = new ArrayList<>();

        int yearIdx = 0, hourIdx = 1, numberIdx = 2;
        for (ArrayList<String> row : response) {
            years.add(row.get(yearIdx));
            totalEducationHourOfEmployees.add(Long.parseLong(row.get(hourIdx)));
            totalEducationNumberOfEmployees.add(Long.parseLong(row.get(numberIdx)));
        }

        StatisticsMainYearResponseDto statisticsMainYearResponseDto = new StatisticsMainYearResponseDto(years, totalEducationHourOfEmployees, totalEducationNumberOfEmployees);

        return statisticsMainYearResponseDto;
    }

    public StatisticsMainMonthResponseDto getMainStatisticsWithMonth(){
        int monthPage = 0;
        int monthSize = 12;
        List<ArrayList<String>> response = statisticsRepository.getMainStatisticsWithMonth(PageRequest.of(monthPage, monthSize));
        ArrayList<String> months = new ArrayList<>();
        ArrayList<Long> totalEducationHourOfEmployees = new ArrayList<>();
        ArrayList<Long> totalEducationNumberOfEmployees = new ArrayList<>();

        int monthIdx = 0, hourIdx = 1, numberIdx = 2;
        for (ArrayList<String> row : response) {
            months.add(row.get(monthIdx));
            totalEducationHourOfEmployees.add(Long.parseLong(row.get(hourIdx)));
            totalEducationNumberOfEmployees.add(Long.parseLong(row.get(numberIdx)));
        }

        StatisticsMainMonthResponseDto statisticsMainMonthResponseDto = new StatisticsMainMonthResponseDto(months, totalEducationHourOfEmployees, totalEducationNumberOfEmployees);

        return statisticsMainMonthResponseDto;
    }

    public StatisticsMainCategoryResponseDto getMainStatisticsWithCategory(){
        int categoryPage = 0;
        int categorySize = 3;
        List<ArrayList<String>> response = statisticsRepository.getMainStatisticsWithCategory(PageRequest.of(categoryPage, categorySize));
        ArrayList<String> names = new ArrayList<>();
        ArrayList<Long> totalNumber = new ArrayList<>();

        int nameIdx = 0, numberIdx = 1;
        for (ArrayList<String> row : response) {
            names.add(row.get(nameIdx));
            totalNumber.add(Long.parseLong(row.get(numberIdx)));
        }

        StatisticsMainCategoryResponseDto statisticsMainCategoryResponseDto = new StatisticsMainCategoryResponseDto(names, totalNumber);

        return statisticsMainCategoryResponseDto;
    }

    public StatisticsMainTagResponseDto getMainStatisticsWithTag(){
        int tagPage = 0;
        int tagSize = 3;
        List<ArrayList<String>> response = statisticsRepository.getMainStatisticsWithTag(PageRequest.of(tagPage, tagSize));
        ArrayList<String> names = new ArrayList<>();
        ArrayList<Long> totalCount = new ArrayList<>();

        int nameIdx = 0, numberIdx = 1;
        for (ArrayList<String> row : response) {
            names.add(row.get(nameIdx));
            totalCount.add(Long.parseLong(row.get(numberIdx)));
        }

        StatisticsMainTagResponseDto statisticsMainTagResponseDto = new StatisticsMainTagResponseDto(names, totalCount);

        return statisticsMainTagResponseDto;
    }

    /**
     *
     * 개인 교육 관리 페이지 통계 Service
     */
    // 통계 - 당해년도의 월별 교육 건수, 시간
    public StatisticsEducationMonthResponseDto getEducationStatisticsWithMonth(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotExistUserException("존재하지 않는 사용자입니다."));

        String currentYear = Integer.toString(LocalDate.now().getYear());
        List<ArrayList<String>> response = statisticsRepository.getEducationStatisticsWithMonth(userId, currentYear);
        String[] months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"}; // Month는 사이즈가 불변이기때문에 배열로 초기화
        long[] EducationHoursOfUser = new long[12]; // Month는 사이즈가 불변이기때문에 배열로 초기화하여 0으로 세팅
        long[] EducationNumbersOfUser = new long[12]; // Month는 사이즈가 불변이기때문에 배열로 초기화하여 0으로 세팅

        int monthIdx = 0, hourIdx = 1, numberIdx = 2;
        for (ArrayList<String> row : response) {
            // 쿼리 결과 데이터가 있는 월만 값 대입
            int idx = Integer.parseInt(row.get(monthIdx))-1;
            EducationHoursOfUser[idx] = Long.parseLong(row.get(hourIdx));
            EducationNumbersOfUser[idx] = Long.parseLong(row.get(numberIdx));
        }

        return new StatisticsEducationMonthResponseDto(currentYear, months, EducationHoursOfUser, EducationNumbersOfUser);
    }

    // 통계 - 누적 개인 최다 카테고리
    public StatisticsEducationCategoryResponseDto getEducationStatisticsWithCategory(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotExistUserException("존재하지 않는 사용자입니다."));
        int categoryPage = 0;
        int categorySize = 1;
        List<ArrayList<String>> response = statisticsRepository.getEducationStatisticsWithCategory(userId, PageRequest.of(categoryPage, categorySize));
        String categoryName = "없음";
        Long totalNumber = 0L;

        int nameIdx = 0, numberIdx = 1;
        for (ArrayList<String> row : response) {
            categoryName = row.get(nameIdx);
            totalNumber = Long.parseLong(row.get(numberIdx));
        }

        return new StatisticsEducationCategoryResponseDto(categoryName, totalNumber);
    }

    // 통계 - 누적 개인 TOP 3 태그
    public StatisticsEducationTagResponseDto getEducationStatisticsWithTag(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotExistUserException("존재하지 않는 사용자입니다."));
        int tagPage = 0;
        int tagSize = 3;
        List<ArrayList<String>> response = statisticsRepository.getEducationStatisticsWithTag(userId, PageRequest.of(tagPage, tagSize));
        ArrayList<String> tagNames = new ArrayList<>();
        ArrayList<Long> totalCount = new ArrayList<>();

        int nameIdx = 0, numberIdx = 1;
        for (ArrayList<String> row : response) {
            tagNames.add(row.get(nameIdx));
            totalCount.add(Long.parseLong(row.get(numberIdx)));
        }

        return new StatisticsEducationTagResponseDto(tagNames, totalCount);
    }

    // 통계 - 당해년도 사용자 vs 회사
    public StatisticsEducationHourResponseDto getEducationStatisticsWithHour(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotExistUserException("존재하지 않는 사용자입니다."));
        String currentYear = Integer.toString(LocalDate.now().getYear());
        // 당해년도 개인 총 교육시간
        Long userTotalHours = statisticsRepository.getEducationStatisticsWithIndividualTotalHours(userId, currentYear);
        if( userTotalHours == null)
            userTotalHours = 0L;
        // 사용자 수
        Long totalUsers = userRepository.count();

        // 당해년도 회사 총 교육시간
        Long companyTotalHours = statisticsRepository.getEducationStatisticsWithCompanyTotalHours(currentYear);
        Long avgCompany = 0L;
        try{
            avgCompany = companyTotalHours/totalUsers;
        } catch (ArithmeticException e) {
            avgCompany = 0L;
        } finally {
            return new StatisticsEducationHourResponseDto(userTotalHours, avgCompany);
        }

    }

    // 통계 - 당해년도 부서 내 등수
    public StatisticsEducationRankResponseDto getEducationStatisticsWithRank(Long userId) {
        String currentYear = Integer.toString(LocalDate.now().getYear());
        // 사용자가 속한 부서 ID 조회
        User findUser = userRepository.findById(userId).orElseThrow(() -> new NotExistUserException("존재하지 않는 사용자입니다."));
        Long deptId = findUser.getDepartment().getId();
        Long totalUserOfDepartment = userRepository.getUserNumberInDepartment(deptId);
        // 사용자의 시간 조회
        Long userTotalHours = statisticsRepository.getEducationStatisticsWithIndividualTotalHours(userId, currentYear);
        ArrayList<Long> response = statisticsRepository.getEducationStatisticsWithRank(deptId, currentYear);

        // 등수 처리 로직 (동점자는 같은 등수 처리)
        int rank = 0;
        if( userTotalHours == null) {
            return new StatisticsEducationRankResponseDto(response.size() + 1, totalUserOfDepartment);
        } else {
            for (Long res : response) {
                ++rank;
                if (res == userTotalHours)
                    break;
            }
        }
        return new StatisticsEducationRankResponseDto(rank, totalUserOfDepartment);
    }
}