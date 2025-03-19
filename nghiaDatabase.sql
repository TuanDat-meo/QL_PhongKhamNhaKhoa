
-- 1. Patients Table
CREATE TABLE Patients (
    PatientID INT PRIMARY KEY AUTO_INCREMENT,
    FullName VARCHAR(100) NOT NULL,
    DateOfBirth DATE,
    Gender VARCHAR(10) CHECK (Gender IN ('Male', 'Female', 'Other')),
    PhoneNumber VARCHAR(15) NOT NULL UNIQUE,
    Address VARCHAR(200),
    Email VARCHAR(100) UNIQUE,
    InsuranceID INT,
    FOREIGN KEY (InsuranceID) REFERENCES Insurance(InsuranceID)
);

-- 2. Dentists Table
CREATE TABLE Dentists (
    DentistID INT PRIMARY KEY AUTO_INCREMENT,
    FullName VARCHAR(100) NOT NULL,
    Specialty VARCHAR(50),
    PhoneNumber VARCHAR(15) UNIQUE,
    Email VARCHAR(100) UNIQUE,
    DepartmentID INT,
    FOREIGN KEY (DepartmentID) REFERENCES Departments(DepartmentID)
);

-- 3. Appointments Table
CREATE TABLE Appointments (
    AppointmentID INT PRIMARY KEY AUTO_INCREMENT,
    PatientID INT NOT NULL,
    DentistID INT NOT NULL,
    RoomID INT,
    AppointmentDate DATETIME NOT NULL,
    Status VARCHAR(20) CHECK (Status IN ('Scheduled', 'Completed', 'Cancelled')),
    Notes TEXT,
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID) ON DELETE CASCADE,
    FOREIGN KEY (DentistID) REFERENCES Dentists(DentistID) ON DELETE CASCADE,
    FOREIGN KEY (RoomID) REFERENCES Rooms(RoomID)
);

-- 4. Payments Table
CREATE TABLE Payments (
    PaymentID INT PRIMARY KEY AUTO_INCREMENT,
    AppointmentID INT NOT NULL,
    Amount DECIMAL(10,2) NOT NULL,
    PaymentDate DATETIME NOT NULL,
    PaymentMethod VARCHAR(50) CHECK (PaymentMethod IN ('Cash', 'Credit Card', 'Insurance')),
    PaymentStatus VARCHAR(20) CHECK (PaymentStatus IN ('Paid','Pending','Refunded')),
    FOREIGN KEY (AppointmentID) REFERENCES Appointments(AppointmentID) ON DELETE CASCADE
);

-- 5. MedicalRecords Table
CREATE TABLE MedicalRecords (
    RecordID INT PRIMARY KEY AUTO_INCREMENT,
    PatientID INT NOT NULL,
    AppointmentID INT,
    Diagnosis VARCHAR(255),
    TreatmentPlan TEXT,
    RecordDate DATETIME NOT NULL,
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID),
    FOREIGN KEY (AppointmentID) REFERENCES Appointments(AppointmentID)
);

-- 6. Treatments Table
CREATE TABLE Treatments (
    TreatmentID INT PRIMARY KEY AUTO_INCREMENT,
    RecordID INT NOT NULL,
    ServiceID INT NOT NULL,
    Description TEXT,
    TreatmentDate DATETIME NOT NULL,
    Cost DECIMAL(10,2),
    FOREIGN KEY (RecordID) REFERENCES MedicalRecords(RecordID) ON DELETE CASCADE,
    FOREIGN KEY (ServiceID) REFERENCES Services(ServiceID)
);

-- 7. Prescriptions Table
CREATE TABLE Prescriptions (
    PrescriptionID INT PRIMARY KEY AUTO_INCREMENT,
    RecordID INT NOT NULL,
    MedicationName VARCHAR(100),
    Dosage VARCHAR(50),
    Frequency VARCHAR(50),
    Duration VARCHAR(50),
    Notes TEXT,
    FOREIGN KEY (RecordID) REFERENCES MedicalRecords(RecordID) ON DELETE CASCADE
);

-- 8. Staff Table
CREATE TABLE Staff (
    StaffID INT PRIMARY KEY AUTO_INCREMENT,
    FullName VARCHAR(100) NOT NULL,
    Position VARCHAR(50),
    DepartmentID INT,
    PhoneNumber VARCHAR(15) UNIQUE,
    Email VARCHAR(100) UNIQUE,
    FOREIGN KEY (DepartmentID) REFERENCES Departments(DepartmentID)
);

-- 9. Departments Table
CREATE TABLE Departments (
    DepartmentID INT PRIMARY KEY AUTO_INCREMENT,
    DepartmentName VARCHAR(100) NOT NULL,
    Description TEXT
);

-- 10. Services Table
CREATE TABLE Services (
    ServiceID INT PRIMARY KEY AUTO_INCREMENT,
    ServiceName VARCHAR(100) NOT NULL,
    Description TEXT,
    Price DECIMAL(10,2)
);

-- 11. Rooms Table
CREATE TABLE Rooms (
    RoomID INT PRIMARY KEY AUTO_INCREMENT,
    RoomNumber VARCHAR(10) NOT NULL UNIQUE,
    Floor INT,
    Description TEXT
);

-- 12. Equipment Table
CREATE TABLE Equipment (
    EquipmentID INT PRIMARY KEY AUTO_INCREMENT,
    EquipmentName VARCHAR(100) NOT NULL,
    Description TEXT,
    PurchaseDate DATE,
    Status VARCHAR(20) CHECK (Status IN ('Operational','Under Maintenance','Out of Service'))
);

-- 13. Inventory Table
CREATE TABLE Inventory (
    InventoryID INT PRIMARY KEY AUTO_INCREMENT,
    ItemName VARCHAR(100) NOT NULL,
    Quantity INT NOT NULL,
    Unit VARCHAR(50),
    SupplierID INT,
    ReorderLevel INT,
    FOREIGN KEY (SupplierID) REFERENCES Suppliers(SupplierID)
);

-- 14. Suppliers Table
CREATE TABLE Suppliers (
    SupplierID INT PRIMARY KEY AUTO_INCREMENT,
    SupplierName VARCHAR(100) NOT NULL,
    ContactName VARCHAR(100),
    PhoneNumber VARCHAR(15),
    Email VARCHAR(100),
    Address VARCHAR(200)
);

-- 15. Insurance Table
CREATE TABLE Insurance (
    InsuranceID INT PRIMARY KEY AUTO_INCREMENT,
    InsuranceName VARCHAR(100) NOT NULL,
    ContactNumber VARCHAR(15),
    Email VARCHAR(100),
    Address VARCHAR(200),
    PolicyDetails TEXT
);

-- 16. Feedback Table
CREATE TABLE Feedback (
    FeedbackID INT PRIMARY KEY AUTO_INCREMENT,
    PatientID INT NOT NULL,
    AppointmentID INT,
    FeedbackText TEXT,
    Rating INT CHECK (Rating BETWEEN 1 AND 5),
    FeedbackDate DATETIME NOT NULL,
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID),
    FOREIGN KEY (AppointmentID) REFERENCES Appointments(AppointmentID)
);

-- 17. Schedules Table
CREATE TABLE Schedules (
    ScheduleID INT PRIMARY KEY AUTO_INCREMENT,
    DentistID INT NOT NULL,
    WorkDate DATE NOT NULL,
    StartTime TIME NOT NULL,
    EndTime TIME NOT NULL,
    Note TEXT,
    FOREIGN KEY (DentistID) REFERENCES Dentists(DentistID)
);
