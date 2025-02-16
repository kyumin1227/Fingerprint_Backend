//package com.example.fingerprint_backend;
//
//import com.example.fingerprint_backend.entity.SchoolClass;
//import com.example.fingerprint_backend.service.CleanManagementService;
//import com.example.fingerprint_backend.types.CleanRole;
//import jakarta.transaction.Transactional;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ContextConfiguration;
//
//@SpringBootTest
//@ContextConfiguration(initializers = DotenvTestInitializer.class)
//@Transactional
//public class ScenarioTest {
//
//    @Autowired
//    private CleanManagementService cleanManagementService;
//
//    @Test
//    void createClassAndSetManager() {
//        // given
//        SchoolClass schoolClass = cleanManagementService.createClassroom("2027_A");
//        cleanManagementService.createMember("2423008", "김민수", "2027_A", CleanRole.MANAGER);
//        schoolClass.appendMember();
//    }
//}
