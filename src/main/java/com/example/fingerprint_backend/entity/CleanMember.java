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
    private String name;
    @ManyToOne
    private Classroom classroom;
    @Enumerated(EnumType.STRING)
    private CleanAttendanceStatus cleanAttendanceStatus;
    @Enumerated(EnumType.STRING)
    private CleanRole cleanRole;


    public CleanMember(String studentNumber, String name, Classroom classroom, CleanAttendanceStatus cleanAttendanceStatus, CleanRole cleanRole) {
        if (classroom == null) {
            throw new IllegalStateException("Classroom은 null일 수 없습니다.");
        }
        this.studentNumber = studentNumber;
        this.name = name;
        this.classroom = classroom;
        this.cleanAttendanceStatus = cleanAttendanceStatus;
        this.cleanRole = cleanRole;
        classroom.appendMember(this);
    }

    /**
     * 멤버에 반을 추가하면 반에도 자동으로 멤버를 추가 하는 메소드
     * CleanMemberにClassroomを追加したら自動にClassroomにもCleanMemberを追加すろメソッド
     */
    public void setClassroom(Classroom classroom) {
        if (this.classroom.equals(classroom)) {
            return;
        }
        this.classroom.removeMember(this);
        classroom.appendMember(this);
        this.classroom = classroom;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CleanMember that = (CleanMember) o;
        return Objects.equals(studentNumber, that.studentNumber) && Objects.equals(name, that.name) && Objects.equals(classroom, that.classroom) && cleanAttendanceStatus == that.cleanAttendanceStatus && cleanRole == that.cleanRole;
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentNumber, name, classroom, cleanAttendanceStatus, cleanRole);
    }
}
