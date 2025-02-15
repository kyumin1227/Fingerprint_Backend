package com.example.fingerprint_backend.entity;

import com.example.fingerprint_backend.types.CleanAttendanceStatus;
import com.example.fingerprint_backend.types.CleanRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@NoArgsConstructor
@Getter @Setter
public class CleanMember {

    @Id
    private String studentNumber;
    @ManyToOne
    private SchoolClass schoolClass;
    @ManyToOne
    private CleanArea cleanArea;
    private String name;
    @Enumerated(EnumType.STRING)
    private CleanRole cleanRole = CleanRole.MEMBER;
    private String profileImage;
    private Integer cleaningCount = 0;


    public CleanMember(String studentNumber, String name, SchoolClass schoolClass, CleanAttendanceStatus cleanAttendanceStatus, CleanRole cleanRole) {
        if (schoolClass == null) {
            throw new IllegalStateException("Classroom은 null일 수 없습니다.");
        }
        this.studentNumber = studentNumber;
        this.name = name;
        this.schoolClass = schoolClass;
        this.cleanAttendanceStatus = cleanAttendanceStatus;
        this.cleanRole = cleanRole;
        schoolClass.appendMember(this);
    }

    /**
     * 멤버에 반을 추가하면 반에도 자동으로 멤버를 추가 하는 메소드
     * CleanMemberにClassroomを追加したら自動にClassroomにもCleanMemberを追加すろメソッド
     */
    public void setSchoolClass(SchoolClass schoolClass) {
        if (this.schoolClass.equals(schoolClass)) {
            return;
        }
        this.schoolClass.removeMember(this);
        schoolClass.appendMember(this);
        this.schoolClass = schoolClass;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CleanMember that = (CleanMember) o;
        return Objects.equals(studentNumber, that.studentNumber) && Objects.equals(name, that.name) && Objects.equals(schoolClass, that.schoolClass) && cleanAttendanceStatus == that.cleanAttendanceStatus && cleanRole == that.cleanRole;
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentNumber, name, schoolClass, cleanAttendanceStatus, cleanRole);
    }
}
