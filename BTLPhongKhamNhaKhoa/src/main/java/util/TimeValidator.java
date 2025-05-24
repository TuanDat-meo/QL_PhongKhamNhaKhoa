package util;

import java.sql.Time;
import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeValidator {
    
    // Thời gian làm việc buổi sáng (7:30 - 12:00)
    private static final LocalTime MORNING_START = LocalTime.of(7, 30);
    private static final LocalTime MORNING_END = LocalTime.of(12, 0);
    
    // Thời gian làm việc buổi chiều (13:00 - 17:00)
    private static final LocalTime AFTERNOON_START = LocalTime.of(13, 0);
    private static final LocalTime AFTERNOON_END = LocalTime.of(17, 0);
    
    // Thời gian tối thiểu giữa các lịch hẹn (30 phút)
    private static final int MIN_APPOINTMENT_INTERVAL = 30;
    
    // Danh sách các ngày nghỉ lễ cố định của Việt Nam
    private static final Map<Month, List<Integer>> FIXED_HOLIDAYS = new HashMap<>();
    
    static {
        // Ngày Quốc tế Lao động
        FIXED_HOLIDAYS.put(Month.MAY, new ArrayList<>(Arrays.asList(1)));
        
        // Ngày Quốc khánh
        FIXED_HOLIDAYS.put(Month.SEPTEMBER, new ArrayList<>(Arrays.asList(2)));
        
        // Ngày Tết Dương lịch
        FIXED_HOLIDAYS.put(Month.JANUARY, new ArrayList<>(Arrays.asList(1)));
        
        // Ngày Giỗ Tổ Hùng Vương (mùng 10 tháng 3 âm lịch - chỉ đưa vào dương lịch tương đối)
        // Vì phụ thuộc vào lịch âm nên có thể thay đổi, ở đây chỉ đưa dương lịch tương đối
        FIXED_HOLIDAYS.put(Month.APRIL, new ArrayList<>(Arrays.asList(21)));
        
        // Ngày Giải phóng miền Nam và Quốc tế Lao động
        FIXED_HOLIDAYS.put(Month.APRIL, new ArrayList<>(Arrays.asList(30)));
        
        // Ngày Chiến thắng
        FIXED_HOLIDAYS.put(Month.DECEMBER, new ArrayList<>(Arrays.asList(22)));
        
        // Tết Âm lịch thường vào tháng 1 hoặc 2 dương lịch
        // Do phụ thuộc vào lịch âm nên sẽ thay đổi hàng năm
        // Ở đây chỉ đưa dương lịch tương đối
        List<Integer> tetHolidays = FIXED_HOLIDAYS.getOrDefault(Month.JANUARY, new ArrayList<>());
        tetHolidays.addAll(Arrays.asList(23, 24, 25, 26, 27, 28, 29));
        FIXED_HOLIDAYS.put(Month.JANUARY, tetHolidays);
    }
    
    /**
     * Kiểm tra xem thời gian đặt lịch có nằm trong giờ làm việc hay không
     * @param time Thời gian cần kiểm tra
     * @return true nếu nằm trong giờ làm việc, false nếu không
     */
    public static boolean isWithinWorkingHours(LocalTime time) {
        return (time.isAfter(MORNING_START) || time.equals(MORNING_START)) && time.isBefore(MORNING_END) ||
               (time.isAfter(AFTERNOON_START) || time.equals(AFTERNOON_START)) && time.isBefore(AFTERNOON_END);
    }
    
    /**
     * Kiểm tra xem ngày đặt lịch có phải là ngày nghỉ hay không
     * @param date Ngày cần kiểm tra
     * @return true nếu là ngày nghỉ, false nếu không
     */
    public static boolean isHoliday(LocalDate date) {
        // Kiểm tra ngày cuối tuần (thứ 7, chủ nhật)
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return true;
        }
        
        // Kiểm tra ngày lễ cố định
        List<Integer> holidaysInMonth = FIXED_HOLIDAYS.get(date.getMonth());
        if (holidaysInMonth != null && holidaysInMonth.contains(date.getDayOfMonth())) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Kiểm tra xem lịch hẹn có thời gian hợp lệ không
     * @param appointmentDate Ngày hẹn
     * @param appointmentTime Giờ hẹn
     * @param existingAppointments Danh sách các lịch hẹn đã có
     * @param doctorId ID của bác sĩ
     * @param roomId ID của phòng khám
     * @return Thông báo lỗi nếu không hợp lệ, null nếu hợp lệ
     */
    public static String validateAppointmentTime(Date appointmentDate, Time appointmentTime, 
                                                List<model.LichHen> existingAppointments,
                                                int doctorId, int roomId) {
        LocalDate date = appointmentDate.toLocalDate();
        LocalTime time = appointmentTime.toLocalTime();
        
        // Kiểm tra ngày nghỉ
        if (isHoliday(date)) {
            return "Ngày hẹn không được nằm trong ngày nghỉ hoặc cuối tuần";
        }
        
        // Kiểm tra giờ làm việc
        if (!isWithinWorkingHours(time)) {
            return "Giờ hẹn phải nằm trong khung giờ làm việc (7:30-12:00 hoặc 13:00-17:00)";
        }
        
        // Kiểm tra khoảng cách tối thiểu giữa các lịch hẹn
        for (model.LichHen existingAppointment : existingAppointments) {
            // Chỉ kiểm tra các lịch hẹn cùng ngày, cùng bác sĩ hoặc cùng phòng
            if (existingAppointment.getNgayHen().toLocalDate().equals(date) && 
                (existingAppointment.getIdBacSi() == doctorId || existingAppointment.getIdPhongKham() == roomId)) {
                
                LocalTime existingTime = existingAppointment.getGioHen().toLocalTime();
                long minutesDifference = Math.abs(java.time.Duration.between(time, existingTime).toMinutes());
                
                if (minutesDifference < MIN_APPOINTMENT_INTERVAL) {
                    if (existingAppointment.getIdBacSi() == doctorId) {
                        return "Lịch hẹn phải cách lịch hẹn khác của bác sĩ ít nhất 30 phút";
                    } else {
                        return "Lịch hẹn phải cách lịch hẹn khác trong cùng phòng ít nhất 30 phút";
                    }
                }
            }
        }
        
        return null; // Không có lỗi
    }
}
