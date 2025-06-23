package model;

public enum TrangThaiLichHen {
    CHO_XAC_NHAN("Chờ xác nhận"),
    DA_XAC_NHAN("Đã xác nhận"),
    DA_HOAN_THANH("Đã hoàn thành"),
    DA_HUY("Đã hủy");

    private final String value;

    TrangThaiLichHen(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    public static TrangThaiLichHen fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Trạng thái không được để trống");
        }        
        String normalizedText = text.trim();        
        for (TrangThaiLichHen t : TrangThaiLichHen.values()) {
            if (t.value.equalsIgnoreCase(normalizedText)) {
                return t;
            }
        }
        switch (normalizedText.toLowerCase()) {
            case "cho xac nhan":
                return CHO_XAC_NHAN;
            case "da xac nhan":
                return DA_XAC_NHAN;
            case "da hoan thanh":
                return DA_HOAN_THANH;
            case "da huy":
                return DA_HUY;
        }
        System.err.println("Không tìm thấy trạng thái: '" + text + "'. Các trạng thái hợp lệ:");
        for (TrangThaiLichHen t : TrangThaiLichHen.values()) {
            System.err.println("- " + t.value);
        }
        
        throw new IllegalArgumentException("Không tìm thấy trạng thái tương ứng: " + text);
    }
}